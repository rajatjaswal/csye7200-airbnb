package spark

import app.{Decision, HouseAddress, Listing}
import org.apache.spark.SparkContext
import org.apache.spark.ml.classification.{LogisticRegression, LogisticRegressionModel}
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Encoders, SparkSession}


object TrainModel {
  def trainModel(rdd: RDD[Listing], sqlContext: SparkSession): LogisticRegressionModel = {
    import sqlContext.implicits._
    val obsDF = sqlContext.createDataFrame(rdd)

    val featureColumns = Array("latitude", "longitude","price","bedrooms")
    val indexLabelColumn = "isWithinPopular"

    val df = createLabeledDataFrame(obsDF, featureColumns, indexLabelColumn)

    val splitSeed = 3093
    val Array(trainingData, testData) = df.randomSplit(Array(0.8, 0.2), splitSeed)
    val lr = new LogisticRegression().setMaxIter(300).setRegParam(0.01).setElasticNetParam(0.92)
    val model = lr.fit(trainingData)
    val predictions = model.transform(testData)
    predictions.show(30)

    val accuracy = getModelAccuracy(predictions)
    println(s"Accuracy = $accuracy")

    val lp = predictions.select(s"label", s"prediction")
    val counttotal = predictions.count()
    val correct = lp.filter($"label" === $"prediction").count()
    val wrong = lp.filter(!($"label" === $"prediction")).count()
    val truep = lp.filter($"prediction" === 0.0).filter($"label" === $"prediction").count()
    val falseN = lp.filter($"prediction" === 0.0).filter(!($"label" === $"prediction")).count()
    val falseP = lp.filter($"prediction" === 1.0).filter(!($"label" === $"prediction")).count()
    val ratioWrong = wrong.toDouble / counttotal.toDouble
    val ratioCorrect = correct.toDouble / counttotal.toDouble

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

  def getModelAccuracy(predictedDataframe: DataFrame): Double = {
    val evaluator = new BinaryClassificationEvaluator().setLabelCol("label").setRawPredictionCol("rawPrediction").setMetricName("areaUnderROC")
    evaluator.evaluate(predictedDataframe)
  }

  def createLabeledDataFrame(obsDF: DataFrame, featureColumns: Array[String], labelColumn: String): DataFrame = {
    val assembler = new VectorAssembler().setInputCols(featureColumns).setOutputCol("features")
    val df2 = assembler.transform(obsDF)

    val labelIndexer = new StringIndexer().setInputCol(labelColumn).setOutputCol("label")
    val df3 = labelIndexer.fit(df2).transform(df2)
    df3.show()
    df3
  }

  def getDecisionFromModel(df: DataFrame, model: LogisticRegressionModel, sqlContext: SparkSession, sc: SparkContext): RDD[HouseAddress] = {
    import sqlContext.implicits._
    val featureColumns = Array("latitude", "longitude", "price", "rooms")
    val assembler = new VectorAssembler().setInputCols(featureColumns).setOutputCol("features")
    val df2 = assembler.transform(df)
    df2.show()
    val df3 = model.transform(df2)
    df3.show()
    df3.printSchema();
    val address = df3.map(row =>
      HouseAddress(Decision.parse(
        if(row.getDouble(12)==1.0) {
          "T"
        } else {
          "F"
        }
      ).get, row.getDouble(1), row.getDouble(2), row.getLong(3), row.getLong(4), row.getString(5), row.getInt(6), row.getBoolean(7), row.getBoolean(8))
    )
    sc.makeRDD(address.collect())
  }
}
