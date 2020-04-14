package spark

import akka.HouseAddressActor
import akka.actor.{ActorSystem, Props}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark._
import org.apache.spark.sql.{SQLContext, SparkSession}
import app.{HouseAddress, Listing, PopularArea}
import backend.{CORSHandler, WebServer}

import scala.util.Try

object SparkConnector {

  def createNewSparkServer(cleansed_addresses: Seq[Try[HouseAddress]],listings: Seq[Try[Listing]], popularAreas:Seq[Try[PopularArea]])(implicit system: ActorSystem) = {

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "group1",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val conf = new SparkConf().setAppName("AirbnbProfitPotentials").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(10))
    val sqlContext = SparkSession.builder().getOrCreate()

    val rdd = sc.makeRDD(listings.flatMap(_.toOption))
//    val model = TrainModel.trainModel(rdd, sqlContext)
//    model.write.overwrite().save("trained-model")

    val topics = Array("airbnb")

    val kafkaStream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    val actor = system.actorOf(Props(classOf[HouseAddressActor]), "sender")
    WebServer.initialize(cleansed_addresses, listings, popularAreas, actor);
    val newAddresses = kafkaStream.map(record=>{
      val x =HouseAddress.parse(record.value().toString.split(",").toSeq)
      x
    })

    newAddresses.foreachRDD( x => {
      val addresses = x.collect()
        println(addresses.toSeq)
        actor ! addresses.toSeq
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
