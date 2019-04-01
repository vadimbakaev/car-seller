package services.mongo

import java.util.UUID

import com.google.inject.{Inject, Singleton}
import com.mongodb.{ConnectionString, DuplicateKeyException, MongoWriteException}
import models.CarAdvertInfo
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.connection.ClusterSettings
import org.mongodb.scala.model.{Filters, IndexOptions, Indexes, Sorts}
import org.mongodb.scala.{MongoClient, MongoClientSettings, MongoCollection, MongoDatabase}
import play.api.{Configuration, Logging}
import services.{CarAdvertsInfoRepository, SortKey}
import services.mongo.MongoCarAdvertInfoRepository._

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MongoCarAdvertInfoRepository @Inject()(
    config: Configuration
)(implicit ec: ExecutionContext)
    extends CarAdvertsInfoRepository
    with Logging {

  private lazy val mongodbURI     = config.get[String](Uri)
  private lazy val databaseName   = config.get[String](Db)
  private lazy val collectionName = config.get[String](CollectionName)

  private lazy val clusterSettings = ClusterSettings
    .builder()
    .applyConnectionString(new ConnectionString(mongodbURI))
    .build()

  private lazy val clientSettings: MongoClientSettings = MongoClientSettings
    .builder()
    .applyToClusterSettings((b: ClusterSettings.Builder) => b.applySettings(clusterSettings))
    .build()

  private lazy val database: MongoDatabase = MongoClient(clientSettings)
    .getDatabase(databaseName)
    .withCodecRegistry(
      fromRegistries(
        fromProviders(classOf[CarAdvertInfo]),
        DEFAULT_CODEC_REGISTRY
      )
    )

  private lazy val collectionF: Future[MongoCollection[CarAdvertInfo]] = initCollection(database)

  private def initCollection(database: MongoDatabase): Future[MongoCollection[CarAdvertInfo]] = {
    val collection = database.getCollection[CarAdvertInfo](collectionName)
    collection
      .createIndex(Indexes.ascending("id"), IndexOptions().background(false).unique(true))
      .toFuture()
      .map(_ => collection)
  }

  override def create(model: CarAdvertInfo): Future[Option[CarAdvertInfo]] = collectionF.flatMap { collection =>
    collection.insertOne(model).toFuture.map(_ => Some(model)).recover {
      case dke: DuplicateKeyException =>
        logger.warn("Duplicated key", dke)
        None
      case mwe: MongoWriteException if mwe.getMessage.startsWith(DuplicateKeyCode) =>
        logger.warn("Duplicated key", mwe)
        None
    }
  }

  override def getById(id: UUID): Future[Option[CarAdvertInfo]] = collectionF.flatMap { collection =>
    collection.find[CarAdvertInfo](Filters.eq("id", id)).toFuture().map(_.headOption)
  }

  override def update(model: CarAdvertInfo): Future[Option[CarAdvertInfo]] = collectionF.flatMap { collection =>
    collection.findOneAndReplace(Filters.eq("id", model.id), model).toFutureOption()
  }

  override def deleteById(id: UUID): Future[Option[CarAdvertInfo]] = collectionF.flatMap { collection =>
    collection.findOneAndDelete(Filters.eq("id", id)).toFutureOption()
  }

  override def getAll(sortBy: SortKey, desc: Boolean): Future[immutable.Seq[CarAdvertInfo]] = collectionF.flatMap {
    collection =>
      collection
        .find()
        .sort(if (desc) Sorts.descending(sortBy.internalValue) else Sorts.ascending(sortBy.internalValue))
        .toFuture()
        .map(_.toList)
  }

}

object MongoCarAdvertInfoRepository {
  val Uri            = "services.mongo.uri"
  val Db             = "services.mongo.database"
  val CollectionName = "services.mongo.collection"

  val DuplicateKeyCode = "E11000"
}
