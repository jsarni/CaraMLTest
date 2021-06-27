
# CaraMLTest

***
> This repository contains the source code of a basic CaraML Application, using the [CaraML Framework](https://github.com/jsarni/CaraML). It'll basically be a tutorial application where we'll show how to use the framework.

> We'll work on a Classification problem, using [Fashion MNIST Digit](https://www.kaggle.com/zalando-research/fashionmnist), and so how we could use CaraML to build an efficient SparkML Model.

# What we need

***

### *CaraYaml*

> First, we need to specify the architecture of our model on a Yaml File. Note that we can build models with as many number of stages (see all available stages [here](https://github.com/jsarni/CaraML)). In this application, we'll use a Logistic Regression model, with a Train/Validation split stage.
> 
> Here our full Yaml File Content. We'll see what each line means.

>
> ```yaml
> CaraPipeline:
> - stage: LogisticRegression
>   params:
>     - MaxIter: 2000
> - evaluator: MulticlassClassificationEvaluator
> - tuner: TrainValidationSplit
>   params:
>     - TrainRatio: 0.8
> ```

> Let's see what this Yaml means :
> 
> * ``` CaraPipeline ```: It's the entry point of every CaraML Yaml File. It's necessary to indicate that we start the description of our model from here.
>
> 
> * ``` - stage ```: It's the key word to add a stage to our model. As mentioned before, you can add as many stages as you want, as long as they are available in **CaraML**. So once the conception of you model done, you just need to add the identified stages one after another, in order. If you want to specify the hyperparameters you want for any stage, you'll need to add the ```param``` key word, and then specify the name of the parameter and its value, in a Yaml list. For instance, in our case, we are specifying that we want the **MaxIter** paramter to be set at **2000** iterations for that stage. 
> 
> 
> * ``` evaluator ```: Here you specify the evaluator you want to fit your model. In our case, we'll use the **MulticlassClassificationEvaluator**
> 
> 
> * ``` tuner ```: With this key word, we specify the tuner we want to use, for model selection. Only two tuners are available: [TrainValidationSplit](https://spark.apache.org/docs/latest/api/scala/org/apache/spark/ml/tuning/TrainValidationSplit.html) and [Cross Validation](https://spark.apache.org/docs/latest/api/scala/org/apache/spark/ml/tuning/CrossValidator.html). You can also chose not to use any tuner. You can also specify their parameters if you want to, in the same way as for the *stages*.

---

### *Your train dataset*

> In order to train the model, you need to pass your train dataset to the CaraModel object. So make sure your Dataset is well formatted. Otherwise, an exception will occure.
> 
> You can see an example of Dataset preparation in the source code, where we had to prepare the data.

---

### *A path where to save your trained model*

> This is a required information. You'll have to specify a path where to save your model.

---

### *Overwrite existing models ?*

> By default, the option is set to true. So basically, when you run a new train with the same saving path, it'll be overwritten. If the option is set to false and a model already exists in that folder, an exception will occure.

*** 

# Code example

```scala
def main(args: Array[String]): Unit = {
    val trainDS = ??? // Your train dataset
    val testDS = ??? // Your test dataset

    // Run CaraML
    val caraModel = new CaraModel(yamlPath, trainDS, savePath, overwrite = true)
    caraModel.run()

    // Evaluate your model
    val evaluation = caraModel.evaluate(testDS)
}
```