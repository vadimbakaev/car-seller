package services.mongo

import java.util.UUID

import com.google.inject.{Inject, Singleton}
import com.mongodb.{ConnectionString, DuplicateKeyException, MongoWriteException}
import models.CarInfo
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.{DEFAULT_CODEC_REGISTRY, Macros}
import org.mongodb.scala.connection.ClusterSettings
import org.mongodb.scala.model.{Filters, IndexOptions, Indexes}
import org.mongodb.scala.{MongoClient, MongoClientSettings, MongoCollection, MongoDatabase}
import play.api.{Configuration, Logging}
import services.CarInfoRepository

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}
import MongoCarInfoRepository._

@Singleton
class MongoCarInfoRepository @Inject()(
    config: Configuration
)(implicit ec: ExecutionContext)
    extends CarInfoRepository
    with Logging {

  private lazy val mongodbURI     = config.get[String](Uri)
  private lazy val databaseName   = config.get[String](Db)
  private lazy val collectionName = config.get[String](CollectionName)

  private val carCodecProvider: CodecProvider = Macros.createCodecProviderIgnoreNone[CarInfo]()

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
        fromProviders(carCodecProvider),
        DEFAULT_CODEC_REGISTRY
      )
    )

  private lazy val collectionF: Future[MongoCollection[CarInfo]] = initCollection(database)

  private def initCollection(database: MongoDatabase): Future[MongoCollection[CarInfo]] = {
    val collection = database.getCollection[CarInfo](collectionName)
    collection
      .createIndex(Indexes.ascending("id"), IndexOptions().background(false).unique(true))
      .toFuture()
      .map(_ => collection)
  }

  override def create(model: CarInfo): Future[Option[CarInfo]] = collectionF.flatMap { collection =>
    collection.insertOne(model).toFuture.map(_ => Some(model)).recover {
      case dke: DuplicateKeyException =>
        logger.warn("Duplicated key", dke)
        None
      case mwe: MongoWriteException if mwe.getMessage.startsWith(DuplicateKeyCode) =>
        logger.warn("Duplicated key", mwe)
        None
    }
  }

  override def getById(id: UUID): Future[Option[CarInfo]] = collectionF.flatMap { collection =>
    collection.find[CarInfo](Filters.eq("id", id)).toFuture().map(_.headOption)
  }

  override def update(model: CarInfo): Future[Option[CarInfo]] = ???

  override def deleteById(id: UUID): Future[Option[CarInfo]] = ???

  override def getAll(criteria: String, desk: Boolean): Future[immutable.Seq[CarInfo]] = ???

}

object MongoCarInfoRepository {
  val Uri            = "services.mongo.uri"
  val Db             = "services.mongo.database"
  val CollectionName = "services.mongo.collection"

  val DuplicateKeyCode = "E11000"
}
