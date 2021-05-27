import org.apache.spark.sql.SparkSession

object Main {
  def main(args: Array[String]): Unit = {
    implicit val sparkSession: SparkSession =
      SparkSession
        .builder()
        .appName("CaraMLTest")
        .master("local[1]")
        .getOrCreate()

    val datasetPath: String = getClass.getResource("/test_dataset.csv").getPath
    val yamlPath: String = getClass.getResource("/cara_yaml.yaml").getPath
    val datasetFormat: String = "csv"
    val savePath: String = "./resultingModel.cml"

  }
}
