import org.apache.spark.sql.{DataFrame, SparkSession}
import io.github.jsarni.CaraModel
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.sql.functions.{array, col}
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql._

import scala.collection.mutable


object Main extends AppConfig {
  def main(args: Array[String]): Unit = {

    val sparkMaster = conf.getString("spark_master")
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

    trainDS.show()
    val caraModel = new CaraModel(yamlPath, trainDS, savePath)

    val evaluation = caraModel.evaluate(testDS)
    evaluation.show()

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
}
