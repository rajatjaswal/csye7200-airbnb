package spark

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark._
import org.apache.spark.sql.{SQLContext, SparkSession}
import app.{HouseAddress, Listing}

import scala.util.Try

object SparkConnector {

  def createNewSparkServer(listings: Seq[Try[Listing]]) = {

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
    val ssc = new StreamingContext(sc, Seconds(5))
    val sqlContext = SparkSession.builder().getOrCreate()

    val rdd = sc.makeRDD(listings.flatMap(_.toOption))
    val model = TrainModel.trainModel(rdd, sqlContext)
    model.write.overwrite().save("trained-model")
    val topics = Array("airbnb")

    val kafkaStream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    val newAddresses = kafkaStream.map(record=>{
      HouseAddress.parse(record.value().toString.split(",").toSeq)
    }).flatMap(_.toOption)

    newAddresses.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
