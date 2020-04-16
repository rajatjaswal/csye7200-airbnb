package spark

import akka.HouseAddressActor
import akka.actor.{ActorRef, ActorSystem, Props}
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
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.ml.classification.LogisticRegressionModel
import org.apache.spark.streaming.dstream.InputDStream

import scala.collection.mutable
import scala.util.Try

object SparkConnector {

  def createNewSparkServer(listings: Seq[Try[Listing]], popularAreas:Seq[Try[PopularArea]])(implicit system: ActorSystem) = {

   val (sc,ssc,sqlContext) = createSparkConfiguration();
    val rdd = sc.makeRDD(listings.flatMap(_.toOption))
    val actor = system.actorOf(Props(classOf[HouseAddressActor]), "sender")
    WebServer.initialize(listings, popularAreas, actor);
   val model = TrainModel.trainModel(rdd, sqlContext)
   model.write.overwrite().save("trained-model")
//     val model = LogisticRegressionModel.load("trained-model")

    val kafkaStream = createKafkaStream(ssc)

    handleStreamMessages(kafkaStream, sc, sqlContext, model, actor, popularAreas)

    ssc.start()
    ssc.awaitTermination()
  }

  def handleStreamMessages(kafkaStream: InputDStream[ConsumerRecord[String, String]], sc:SparkContext, sqlContext: SparkSession, model: LogisticRegressionModel, actor:ActorRef, popularAreas: Seq[Try[PopularArea]]) = {
    val newAddresses = kafkaStream.map(record=>{
      val house = HouseAddress.parse(record.value().toString.split(","), popularAreas)
      house
    })

    newAddresses.foreachRDD( x => {
      if(!x.collect().isEmpty){
        val ha = x.collect().toSeq.flatMap(_.toOption).filter(p => p.latitude != 0.0 && p.longitude !=0.0 && p.price !=0)
        if(!ha.isEmpty) {
          val rdd = sc.makeRDD(ha)
          val df = sqlContext.createDataFrame(rdd)
          val addr = TrainModel.getDecisionFromModel(df, model, sqlContext, sc);
          println(addr.collect().toSeq)
          actor ! addr.collect().toSeq;
        }else {
          val addr = mutable.Seq[HouseAddress]()
          actor ! addr
        }
      }else{
        val addr = mutable.Seq[HouseAddress]()
        actor ! addr
      }
    })
  }

  def createSparkConfiguration(): (SparkContext, StreamingContext, SparkSession) = {
    val conf = new SparkConf().setAppName("AirbnbProfitPotentials").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(10))
    val sqlContext = SparkSession.builder().getOrCreate()
    (sc, ssc, sqlContext)
  }

  def createKafkaStream(ssc: StreamingContext): InputDStream[ConsumerRecord[String, String]] = {
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "group1",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val topics = Array("airbnb")

    KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
  }
}
