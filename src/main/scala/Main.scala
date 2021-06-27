import org.apache.spark.sql.SparkSession
import io.github.jsarni.CaraModel
import org.apache.spark.ml.linalg.Vectors

object Main {
  def main(args: Array[String]): Unit = {
    implicit val sparkSession: SparkSession =
      SparkSession
        .builder()
        .appName("CaraMLTest")
        .master("spark://laptop-juba:7077")
        .getOrCreate()

    val dataset = sparkSession.read.format("libsvm").load(getClass.getResource("sample_linear_regression_data.txt").getPath)

    val yamlPath: String = getClass.getResource("/cara_yaml.yaml").getPath
    val savePath: String = "./resultingModel.cml"

    val caraModel = new CaraModel(yamlPath, dataset, savePath)

    println(caraModel.run().get)

  }
}
