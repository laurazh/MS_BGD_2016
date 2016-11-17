package com.sparkProject
import org.apache.spark.sql.SparkSession

/**
  * Created by laura ZHOU and pauline thoumin on 25/10/16.
  */
object JobML {
  //function main
  def main(args: Array[String]): Unit = {
    // SparkSession configuration
    val spark = SparkSession
      .builder
      .master("local")
      .appName("spark session TP_parisTech")
      .getOrCreate()

    val sc = spark.sparkContext

    /********************************************************************************
      *
      *        TP 5 : début du projet
      *
      ********************************************************************************/

    //Load the CSV file
    val df = spark.read.parquet(args(0))
    val df2 = spark.read.parquet(args(0))
    //val df = spark.read.parquet("/cal/homes/lazhou/Downloads/cleanedDataFrame.parquet")
    //val df2 = spark.read.parquet("/cal/homes/lazhou/Downloads/cleanedDataFrame.parquet")


    import org.apache.spark.ml.feature.VectorAssembler
    import org.apache.spark.ml.feature.StringIndexer

    /********************************************************************************
      *   QUestion 1
      ********************************************************************************/

    //drop data
    val df3 = df2.drop(df2.col("rowid"))
    val df4 = df3.drop(df3.col("koi_disposition"))

    val df_tmp = df4.columns

    //assemble the features
    val assembler = new VectorAssembler()
      .setInputCols(df_tmp)
      .setOutputCol("features")

    val output = assembler.transform(df)

    val output2 =output.select("koi_disposition","features")

    // index the features
    val indexer = new StringIndexer()
      .setInputCol("koi_disposition")
      .setOutputCol("label")

    val indexed = indexer.fit(output).transform(output)
    val output3 =indexed.select("label","features")

    ////standard scaler
    import org.apache.spark.ml.feature.StandardScaler

    val scaler = new StandardScaler()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")
      .setWithStd(true)
      .setWithMean(false)

    // Compute summary statistics by fitting the StandardScaler.
    val scalerModel = scaler.fit(output3)

    // Normalize each feature to have unit standard deviation.
    val scaledData = scalerModel.transform(output3)
    scaledData.show()//avec label+ features+ scaledFeatures




    /********************************************************************************
      *   QUestion 2
      ********************************************************************************/
    //////Split the data into training and test sets (10% held out for testing).
    val Array(trainingData, testData) = output3.randomSplit(Array(0.9, 0.1))


    ///Entraînement du classifieur et réglage des hyper-paramètres de l’algorithme.
    import org.apache.spark.ml.classification.LogisticRegression
    import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
    import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
    import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}

    // Load training data
    val lr = new LogisticRegression()
      .setElasticNetParam(1.0)
      .setLabelCol("label")
      .setStandardization(false)  // we already scaled the data
      .setFitIntercept(true)  // we want an affine regression (with false, it is a linear regression)
      .setTol(1.0e-5)  // stop criterion of the algorithm based on its convergence
      .setMaxIter(300)  // a security stop criterion to avoid infinite loops

    val r = (-6.0 to 0.0  by 0.5).toArray.map(math.pow(10,_))

    //////Split the training set into training and test sets (30% held out for testing).

    val paramGrid = new ParamGridBuilder()
      .addGrid(lr.regParam, r)
      .build()

    // Creating the BinaryClassificationEvaluator
    val evaluator = new BinaryClassificationEvaluator().setLabelCol("label")

    val cv = new CrossValidator()
      .setEstimator(lr)
      .setEvaluator(evaluator)
      .setEstimatorParamMaps(paramGrid)
      .setNumFolds(5)  //Use 3+ in practice cross fold better than random training split

    // Run cross-validation, and choose the best set of parameters.
    val cvModel = cv.fit(trainingData)


    // Make predictions on test . cvModel uses the best model found (lrModel).
    val df_WithPredictions = cvModel.transform(testData)

    // Select (prediction, true label) and compute test error.
    val accuracy = evaluator.evaluate(df_WithPredictions)

    println("The score with crossvalidator: ****************************************************************")
    evaluator.setRawPredictionCol("prediction")
    println("Accuracy with crossvalidator = "+evaluator.evaluate(df_WithPredictions) + "\n")
    println("Test Error with crossvalidator = "+ (1.0-evaluator.evaluate(df_WithPredictions)) + "\n")


    df_WithPredictions.groupBy("label", "prediction").count.show()

    ////////////METHODE 2 AVEC TRAIN SPLIT donne des résultats similaires

    // A TrainValidationSplit requires an Estimator, a set of Estimator ParamMaps, and an Evaluator.
    val evaluator1 = new BinaryClassificationEvaluator().setLabelCol("label")

    import org.apache.spark.ml.tuning.TrainValidationSplit
    val trainValidationSplit = new TrainValidationSplit()
      .setEstimator(lr)
      .setEvaluator(evaluator1)
      .setEstimatorParamMaps(paramGrid)
      // 70% of the data will be used for training and the remaining 30% for validation.
      .setTrainRatio(0.7)

    // Run train validation split, and choose the best set of parameters.
    val model = trainValidationSplit.fit(trainingData)

    // Make predictions on test data. model is the model with combination of parameters
    // that performed best.
    val df_WithPredictions1= model.transform(testData)
    df_WithPredictions1.groupBy("label", "prediction").count.show()

    // Select (prediction, true label) and compute test error.
    // Printing the score :
    println("The score with training split: ****************************************************************")
    evaluator1.setRawPredictionCol("prediction")
    println("Accuracy with training split = "+evaluator1.evaluate(df_WithPredictions1) + "\n")
    println("Test Error with training split = "+ (1.0-evaluator1.evaluate(df_WithPredictions1)) + "\n")



  }



}
