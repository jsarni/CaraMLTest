import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import io.github.jsarni.CaraModel
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.sql.functions.{array, col, mean, when}
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.types.DoubleType

import scala.collection.mutable


object MainWithCaraML extends AppConfig {
  def main(args: Array[String]): Unit = {

    val sparkMaster = conf.getString("spark_master_url")
    val yamlPath = conf.getString("yaml_path")
    val savePath = conf.getString("save_path")
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

    val caraModel = new CaraModel(yamlPath, trainDS, savePath)
    caraModel.run()

    val prediction = caraModel.evaluate(testDS)

    val accuracy = prediction.transform(computeAccuracy)

    accuracy.show()
  }

  def loadDataset(path: String)(implicit sparkSession: SparkSession) = {
    import sparkSession.implicits._

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
      .agg(mean("is_success").as("test_datase_accuracy"))
  }
}
