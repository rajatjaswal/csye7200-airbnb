package spark

import org.apache.spark.streaming.{ Duration, Seconds, StreamingContext}
import org.scalatest.Suite

trait SparkStreamingSpec extends SparkSpec {
  this: Suite =>

  private var _ssc: StreamingContext = _

  override def beforeAll(): Unit = {
    super.beforeAll()

    _ssc = new StreamingContext(sc, batchDuration)
  }

  def batchDuration: Duration = Seconds(1)

  override def afterAll(): Unit = {
    if (_ssc != null) {
      _ssc.stop(stopSparkContext = false, stopGracefully = false)
      _ssc = null
    }

    super.afterAll()
  }

  def ssc: StreamingContext = _ssc

}