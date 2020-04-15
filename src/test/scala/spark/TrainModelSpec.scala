package spark

import app.{Coordinates, Decision, HouseAddress, Listing, ListingAddress, PopularArea}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}

class TrainModelSpec extends FlatSpec with SparkSpec with GivenWhenThen with Matchers{
  private val Listings = Seq(
    Listing(ListingAddress("abc"),-32.12,145.1,1234,3422,3,2,245,100,98.0,6,24.5,1,2.2,PopularArea(Coordinates(-32.12,134.22),"abdd", 78), 1),
    Listing(ListingAddress("abc"),-32.12,145.1,1234,3422,3,2,245,100,98.0,6,24.5,0,2.2,PopularArea(Coordinates(-32.12,134.22),"abdd", 78), 1),
    Listing(ListingAddress("abc"),-32.12,145.1,1234,3422,3,2,245,100,98.0,6,24.5,0,2.2,PopularArea(Coordinates(-32.12,134.22),"abdd", 78), 1),
    Listing(ListingAddress("abc"),-32.12,145.1,1234,3422,3,2,245,100,98.0,6,24.5,1,2.2,PopularArea(Coordinates(-32.12,134.22),"abdd", 78), 1)
  )

  private val houseAddress = Seq(
    HouseAddress(Decision(false),-34.14,144.12,250,234500,"aaa",2,false,true)
  )

  behavior of "TrainModel"

  it should "return a model with expected accuracy after training on test data set" in {
    val _sc = sc
    val rdd = _sc.makeRDD(Listings)
    val sqlContext = SparkSession.builder().getOrCreate()
    val featureColumns = Array("latitude", "longitude")
    val indexLabelColumn = "isWithinPopular"
    val model = TrainModel.trainModel(rdd, sqlContext);
    val obsDF = sqlContext.createDataFrame(rdd);
    val df = TrainModel.createLabeledDataFrame(obsDF, featureColumns, indexLabelColumn)
    val predictions = model.transform(df)
    assert(TrainModel.getModelAccuracy(predictions)==0.5)
  }

  it should "return a rdd of houseaddress" in {
    val rdd = sc.makeRDD(Listings)
    val sqlContext = SparkSession.builder().getOrCreate()
    val model = TrainModel.trainModel(rdd, sqlContext);
    val houseAddressDF = sqlContext.createDataFrame(sc.makeRDD(houseAddress))
    val rddAddress = TrainModel.getDecisionFromModel(houseAddressDF,model,sqlContext,sc)
    rddAddress shouldBe a [RDD[_]]
    val seqAddr = rddAddress.collect().toSeq
    seqAddr shouldBe a [Seq[_]]
    val decision = seqAddr.head.decision.decision;
    assert(decision==false)
  }
}
