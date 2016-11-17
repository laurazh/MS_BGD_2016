# MS_BGD_2016
Spark
--------------

ExoPlanetsClassification

Note
--------------
The code reads a **.parquet** file.   

How to run
--------------

* Build project 

command (run the command on the folder containing **build.sbt**):

    sbt assembly


* Running the project

spark-submit command (run the command in spark-2.0.0-bin-hadoop2.6/bin):
 

    ./spark-submit --conf spark.eventLog.enabled=true --conf spark.eventLog.dir="/tmp" --driver-memory 3G --executor-memory 4G --class com.sparkProject.JobML /cal/homes/lazhou/Downloads/tp_spark/target/scala-2.11/tp_spark-assembly-1.0.jar /cal/homes/lazhou/Downloads/cleanedDataFrame.parquet

replace "/cal/homes/lazhou/Downloads/cleanedDataFrame.parquet" with your correct path

replace "/cal/homes/lazhou/Downloads/tp_spark/target/scala-2.11/tp_spark-assembly-1.0.jar" by the path of the jar

Execution Result:
--------------

    +-----+--------------------+--------------------+
    |label|            features|      scaledFeatures|
    +-----+--------------------+--------------------+
    |  1.0|[9.48803146,2.95E...|[0.08483524146264...|
    |  1.0|[54.418464,2.686E...|[0.48657127170465...|
    |  1.0|[2.525593315,3.66...|[0.02258206242440...|
    |  1.0|[11.09431923,2.13...|[0.09919752634764...|
    |  1.0|[4.13443005,1.061...|[0.03696713834485...|
    |  1.0|[2.56659092,1.598...|[0.02294863390282...|
    |  0.0|[7.36178044,1.589...|[0.06582381433443...|
    |  1.0|[16.06862959,1.16...|[0.14367427816700...|
    |  1.0|[2.470613385,1.9E...|[0.02209047092233...|
    |  1.0|[2.204735365,3.8E...|[0.01971317842268...|
    |  1.0|[3.522498573,1.94...|[0.03149568150696...|
    |  1.0|[3.709213846,6.32...|[0.03316515692307...|
    |  0.0|[11.521446107,2.0...|[0.10301659165094...|
    |  0.0|[19.40398222,1.39...|[0.17349663351247...|
    |  0.0|[16.46983563,1.20...|[0.14726157774787...|
    |  1.0|[9.27358194,1.178...|[0.08291778609927...|
    |  1.0|[6.029301321,5.36...|[0.05390973202127...|
    |  0.0|[2.696365214,7.04...|[0.02410898350890...|
    |  1.0|[5.34955671,9.141...|[0.04783193828184...|
    |  1.0|[3.94104632,1.16E...|[0.03523803832040...|
    +-----+--------------------+--------------------+
    only showing top 20 rows
    
    
    
 
with random training split:
--------------

Confusion matrix:
    
    +-----+----------+-----+
    |label|prediction|count|
    +-----+----------+-----+
    |  1.0|       1.0|  227|
    |  0.0|       1.0|   19|
    |  1.0|       0.0|   12|
    |  0.0|       0.0|  433|
    +-----+----------+-----+

The score with training split: ****************************************************************

Accuracy with training split = 0.9538776983744955

Test Error with training split = 0.04612230162550446

 

with cross validation:
--------------

 Confusion matrix: 
 
    +-----+----------+-----+
    |label|prediction|count|
    +-----+----------+-----+
    |  1.0|       1.0|  229|
    |  0.0|       1.0|   19|
    |  1.0|       0.0|   10|
    |  0.0|       0.0|  433|
    +-----+----------+-----+


 
The score with crossvalidator: ****************************************************************

Accuracy with crossvalidator = 0.9580617987929055

Test Error with crossvalidator = 0.04193820120709446

The score with cv or training split are similar.

Improvement
--------------
  
 - Save the the best model
 - Increase the iteration
    
    

