package com.sparkProject

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._ //enable the "_" function

object Job {

  def main(args: Array[String]): Unit = {

    // SparkSession configuration
    val spark = SparkSession
      .builder
      .master("local")
      .appName("spark session TP_parisTech")
      .getOrCreate()

    val sc = spark.sparkContext

    import spark.implicits._


    /********************************************************************************
      *
      *        TP 1
      *
      *        - Set environment, InteliJ, submit jobs to Spark
      *        - Load local unstructured data
      *        - Word count , Map Reduce
      ********************************************************************************/



    // ----------------- word count ------------------------

    val df_wordCount = sc.textFile("/cal/homes/lazhou/spark-2.0.0-bin-hadoop2.6/README.md")
      .flatMap{case (line: String) => line.split(" ")}
      .map{case (word: String) => (word, 1)}
      .reduceByKey{case (i: Int, j: Int) => i + j}
      .toDF("word", "count")

    df_wordCount.orderBy($"count".desc).show()


    /********************************************************************************
      *
      *        TP 2 : début du projet
      *
      ********************************************************************************/
    val df = spark
      .read
      .option("header","true")
      .option("interSchema", "true")
      .option("comment","#")
      .csv("/cal/homes/lazhou/Downloads/cumulative.csv")

    df.printSchema()


    println("*********************************************************************number of columns", df.columns.length) //df.columns returns an Array
    println( "**********************************************************************number of rows",df.count)
    /*df.collect.foreach(println)*/

    df.show()
    println( "**********************************************************************")
    val columns = df.columns.slice(10, 20) // df.columns returns an Array. In scala arrays have a method “slice” returning a slice of the array
    df.select(columns.map(col): _*).show(50) // have to make a new import
    df.printSchema()
    df.groupBy("koi_disposition").count().show()
    //delete koi_disposition = CONFIRMED ou FALSE POSITIVE
    //df.filter($"koi_disposition"=="CONFIRMED").show()

    //4a - filtre des colonnes
    val df_cleaned = df.filter($"koi_disposition"==="CONFIRMED" || $"koi_disposition"==="FALSE POSITIVE" )
    //vérification
    df_cleaned.select("koi_disposition").filter($"koi_disposition"==="CONFIRMED" || $"koi_disposition"==="FALSE POSITIVE").show()

    //4b - Afficher le nombre d’éléments distincts dans la colonne “koi_eccen_err1//
    import org.apache.spark.sql.functions.countDistinct
    df_cleaned.groupBy("koi_eccen_err1").count().show() // nbre de ligne dans la colonne
    df_cleaned.agg(countDistinct("koi_eccen_err1")).show()  // 1 distinct
    df_cleaned.select("koi_eccen_err1") // verif; on voit que les lignes sont vides. 1 distinct => tous sont vides

    //4c drop columns
    val df_cleaned2 = df_cleaned.drop(df.col("koi_eccen_err1"))
    //verif
    df_cleaned2.printSchema()

    //4d
    //to drop in one line
    //"index", "kepid", "koi_fpflag_nt","koi_fpflag_ss", "koi_fpflag_co", "koi_fpflag_ec", "koi_sparprov", "koi_trans_mod", "koi_datalink_dvr", "koi_datalink_dvs", "koi_tce_delivname", "koi_parm_prov", "koi_limbdark_mod", "koi_fittype", "koi_disp_prov", "koi_comment", "kepoi_name", "kepler_name", "koi_vet_date", "koi_pdisposition
    val df_cleaned3 = df_cleaned2.drop("index", "kepid", "koi_fpflag_nt","koi_fpflag_ss", "koi_fpflag_co", "koi_fpflag_ec", "koi_sparprov", "koi_trans_mod", "koi_datalink_dvr", "koi_datalink_dvs", "koi_tce_delivname", "koi_parm_prov", "koi_limbdark_mod", "koi_fittype", "koi_disp_prov", "koi_comment", "kepoi_name", "kepler_name", "koi_vet_date", "koi_pdisposition")
    //verif
    df_cleaned3.printSchema()
    // /!\ je n'ai pas sauvegardé mon résultat, je dois créer une autre variable df_bis

    //4e pris du corriger
    import org.apache.spark.sql.functions._
    val useless_column = df_cleaned3.columns.filter { case (column: String) => df.agg(countDistinct(column)).first().getLong(0) <= 1 }
    useless_column.foreach(println)

    //4f
    val df5 = df_cleaned3.drop(useless_column: _*)

    df5.describe("koi_impact", "koi_duration").show()
    //g
    val df_filled = df5.na.fill(0.0)

    //5
    val df_labels = df_filled.select("rowid", "koi_disposition")
    val df_features = df_filled.drop("koi_disposition")

    //5a
    val df_joined = df_features
      .join(df_labels, usingColumn = "rowid")

    //6
    def udf_sum = udf((col1: Double, col2: Double) => col1 + col2)


    val df_newFeatures = df_filled
      .withColumn("koi_ror_min", udf_sum($"koi_ror", $"koi_ror_err2"))
      .withColumn("koi_ror_max", $"koi_ror" + $"koi_ror_err1")


    df_newFeatures.printSchema()

    val df_newFeatures2 = df_newFeatures.na.fill(0.0)

    //7
    df_newFeatures2
      .coalesce(1) // optional : regroup all data in ONE partition, so that results are printed in ONE file
      // >>>> You should not that in general, only when the data are small enough to fit in the memory of a single machine.
      .write
      .mode("overwrite")
      .option("header", "true")
      .csv("/cal/homes/lazhou/Downloads/tp_spark/cleanedDataFrame.csv")


  }


}
