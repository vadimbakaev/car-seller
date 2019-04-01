package services.mongo

import java.time.Instant
import java.util.UUID

import com.mongodb.ConnectionString
import models.CarAdvertInfo
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.connection.ClusterSettings
import org.mongodb.scala.{Completed, MongoClient, MongoClientSettings, MongoDatabase}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Matchers, WordSpecLike}
import play.api.Configuration
import services.{CarAdvertsInfoRepository, SortKey}
import services.mongo.MongoCarAdvertInfoRepositoryTest._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

class MongoCarAdvertInfoRepositoryTest
    extends WordSpecLike
    with Matchers
    with ScalaFutures
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  val mongodbURI     = "mongodb://127.0.0.1:27017"
  val databaseName   = "test-car-seller"
  val collectionName = "test-adverts"

  override implicit val patienceConfig: PatienceConfig = PatienceConfig(3 seconds, 300 milliseconds)

  val database: MongoDatabase = {
    val clusterSettings: ClusterSettings = ClusterSettings
      .builder()
      .applyConnectionString(new ConnectionString(mongodbURI))
      .build()

    val clientSettings: MongoClientSettings = MongoClientSettings
      .builder()
      .applyToClusterSettings((b: ClusterSettings.Builder) => b.applySettings(clusterSettings))
      .build()
    MongoClient(clientSettings).getDatabase(databaseName)
  }

  val config: Configuration = Configuration(
    "services.mongo.uri"        -> mongodbURI,
    "services.mongo.database"   -> databaseName,
    "services.mongo.collection" -> collectionName
  )
  val repository: CarAdvertsInfoRepository = new MongoCarAdvertInfoRepository(config)

  "MongoCarAdvertInfoRepository" should {

    "create returns Some of carAdvert when create has successful" in {
      repository.create(AudiAdvertInfo).futureValue shouldBe Some(AudiAdvertInfo)
    }

    "create returns None when carAdvert already exist" in {
      val result = for {
        _   <- repository.create(AudiAdvertInfo)
        snd <- repository.create(AudiAdvertInfo)
      } yield snd

      result.futureValue shouldBe None
    }

    "getById returns None when carAdvert not exist" in {
      repository.getById(UUID.randomUUID()).futureValue shouldBe None
    }

    "getById returns Some with result when carAdvert exist" in {
      val result = for {
        _               <- repository.create(AudiAdvertInfo)
        maybeAdvertInfo <- repository.getById(AudiAdvertInfo.id)
      } yield maybeAdvertInfo

      result.futureValue shouldBe Some(AudiAdvertInfo)
    }

    "deleteById returns None when carAdvert not exist" in {
      repository.getById(UUID.randomUUID()).futureValue shouldBe None
    }

    "deleteById returns Some with deleted carAdvert" in {
      val result = for {
        _               <- repository.create(AudiAdvertInfo)
        maybeAdvertInfo <- repository.deleteById(AudiAdvertInfo.id)
      } yield maybeAdvertInfo

      result.futureValue shouldBe Some(AudiAdvertInfo)
    }

    "update returns None when carAdvert not exist" in {
      repository.update(AudiAdvertInfo).futureValue shouldBe None
    }

    "update returns Some with updated carAdvert" in {
      val result = for {
        _               <- repository.create(AudiAdvertInfo)
        maybeAdvertInfo <- repository.update(UpdatedAudiAdvertInfo)
      } yield maybeAdvertInfo

      result.futureValue shouldBe Some(AudiAdvertInfo)
    }

    "getAll returns list of adverts sorted by Id in asc" in {
      val result = for {
        _             <- repository.create(AudiAdvertInfo)
        _             <- repository.create(UpdatedAudiAdvertInfo.copy(id = AudiA6Id))
        listOfAdverts <- repository.getAll(SortKey.Id, desc = false)
      } yield listOfAdverts

      result.futureValue shouldBe List(UpdatedAudiAdvertInfo.copy(id = AudiA6Id), AudiAdvertInfo)
    }

    "getAll returns list of adverts sorted by Id in desc" in {
      val result = for {
        _             <- repository.create(AudiAdvertInfo)
        _             <- repository.create(UpdatedAudiAdvertInfo.copy(id = AudiA6Id))
        listOfAdverts <- repository.getAll(SortKey.Id, desc = true)
      } yield listOfAdverts

      result.futureValue shouldBe List(AudiAdvertInfo, UpdatedAudiAdvertInfo.copy(id = AudiA6Id))
    }

    "getAll returns list of adverts sorted by optional registration in desc" in {
      val result = for {
        _             <- repository.create(AudiAdvertInfo)
        _             <- repository.create(UpdatedAudiAdvertInfo.copy(id = AudiA6Id))
        listOfAdverts <- repository.getAll(SortKey.Registration, desc = true)
      } yield listOfAdverts

      result.futureValue shouldBe List(UpdatedAudiAdvertInfo.copy(id = AudiA6Id), AudiAdvertInfo)
    }
  }

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    database
      .getCollection[CarAdvertInfo](collectionName)
      .deleteMany(BsonDocument())
      .toFuture()
      .futureValue
      .wasAcknowledged() shouldBe true
  }

  override protected def afterAll(): Unit = {
    database.drop().toFuture().futureValue shouldBe Completed()
    super.afterAll()
  }

}

object MongoCarAdvertInfoRepositoryTest {
  val AudiA6Id: UUID = UUID.fromString("a647aecd-c247-4826-af86-252c0e13b8a0")
  val AudiId: UUID   = UUID.fromString("b647aecd-c247-4826-af86-252c0e13b8a0")
  val AudiAdvertInfo: CarAdvertInfo = CarAdvertInfo(
    AudiId,
    "Audi A4 Avant",
    "Diesel",
    7000,
    isNew = true,
    None,
    None
  )
  val UpdatedAudiAdvertInfo: CarAdvertInfo = CarAdvertInfo(
    AudiId,
    "Audi A6",
    "Gasoline",
    9000,
    isNew = false,
    Some(8000),
    Some(Instant.now())
  )
}
