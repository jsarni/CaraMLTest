import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.tuning.{ParamGridBuilder, TrainValidationSplit}
import org.apache.spark.sql.functions.{array, col, mean, when}
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

import scala.collection.mutable


object MainWhitoutCaraML extends AppConfig {
  def main(args: Array[String]): Unit = {

    val sparkMaster = conf.getString("spark_master")
    val trainDatasetPath = conf.getString("train_dataset_path")
    val testDatasetPath = conf.getString("test_dataset_path")

    implicit val sparkSession: SparkSession =
      SparkSession
        .builder()
        .appName("CaraMLTest")
        .master(sparkMaster)
        .getOrCreate()

    val trainDS = loadDataset(trainDatasetPath)
    val testDS = loadDataset(testDatasetPath)

    val stagesList = Array(new LogisticRegression().setMaxIter(20))
    val pipeline = new Pipeline().setStages(stagesList)
    val evaluator = new MulticlassClassificationEvaluator()
    val model =
      new TrainValidationSplit()
        .setEstimator(pipeline)
        .setEvaluator(evaluator)
        .setEstimatorParamMaps(
          new ParamGridBuilder().build()
        )
    val fitModel = model.fit(trainDS)

    val prediction = fitModel.transform(testDS)
    val accuracy = prediction.transform(computeAccuracy)

    accuracy.show()
  }

  def loadDataset(path: String)(implicit sparkSession: SparkSession) = {

    val rawDF = sparkSession.read.option("header", true).format("csv").load(path)
    val featuresCol = array(rawDF.columns.drop(1).map(col).map(_.cast(DoubleType)): _*).as("features")

    rawDF
      .select(col("label").as("target").cast(DoubleType), featuresCol)
      .map(r =>
        LabeledPoint(r.getAs[Double](0), Vectors.dense(r.getAs[mutable.WrappedArray[Double]](1).toArray))
      )
  }

  def computeAccuracy(prediction: Dataset[_]): DataFrame = {
    prediction.withColumn("is_success", when(col("label") === col("prediction"), 1).otherwise(0))
      .agg(mean("is_success"))
  }
}
