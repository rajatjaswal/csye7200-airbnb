package spark

import app.Listing
import org.apache.spark.ml.classification.{LogisticRegression, LogisticRegressionModel}
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object TrainModel {
  def trainModel(rdd: RDD[Listing], sqlContext: SparkSession): LogisticRegressionModel ={
    import sqlContext.implicits._
    val obsDF = sqlContext.createDataFrame(rdd)
    val featureColumns = Array("latitude", "longitude")
    val assembler = new VectorAssembler().setInputCols(featureColumns).setOutputCol("features")
    val df2 = assembler.transform(obsDF)

    val labelIndexer = new StringIndexer().setInputCol("isWithinPopular").setOutputCol("label")
    val df3 = labelIndexer.fit(df2).transform(df2)
    df3.show()

    val splitSeed = 3093
    val Array(trainingData, testData) = df3.randomSplit(Array(0.7, 0.3), splitSeed)
    val lr = new LogisticRegression().setMaxIter(300).setRegParam(0.01).setElasticNetParam(0.92)
    val model = lr.fit(trainingData)
    val predictions = model.transform(testData)
    predictions.show(30)

    val evaluator = new BinaryClassificationEvaluator().setLabelCol("label").setRawPredictionCol("rawPrediction").setMetricName("areaUnderROC")
    val accuracy = evaluator.evaluate(predictions)
    println(s"Accuracy = $accuracy")

    val lp = predictions.select(s"label", s"prediction")
    val counttotal = predictions.count()
    val correct = lp.filter($"label" === $"prediction").count()
    val wrong = lp.filter(!($"label" === $"prediction")).count()
    val truep = lp.filter($"prediction" === 0.0).filter($"label" === $"prediction").count()
    val falseN = lp.filter($"prediction" === 0.0).filter(!($"label" === $"prediction")).count()
    val falseP = lp.filter($"prediction" === 1.0).filter(!($"label" === $"prediction")).count()
    val ratioWrong=wrong.toDouble/counttotal.toDouble
    val ratioCorrect=correct.toDouble/counttotal.toDouble

    println(s"Count Total: $counttotal")
    println(s"Correct: $correct")
    println(s"Wrong: $wrong")
    println(s"True Positives: $truep")
    println(s"False Negatives: $falseN")
    println(s"False Positives: $falseP")
    println(s"Ratio of wrong results: $ratioWrong")
    println(s"Ration of correct results: $ratioCorrect")

    model
  }
}
