package org.example;
import org.apache.spark.ml.classification.*;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.StandardScaler;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.feature.StandardScalerModel;
import org.apache.spark.ml.linalg.Matrix;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;

import java.io.IOException;
import java.util.Arrays;

public class WineQualityTesting {
    public static void main(String[] args){
        if (args.length < 1) {
            System.err.println("Usage: WineQualityTesting <testingPath>");
            System.exit(1);
        }
        String testingPath = args[0];

        SparkSession sparkSession = SparkSession.builder().
                appName("WineQualityTesting").master("local[*]").getOrCreate();

        Dataset<Row> testData = sparkSession.read().option("delimiter", ";").option("inferSchema", "true")
                .option("header", "true").csv(testingPath);
        testData = preProcessData(testData);


        DecisionTreeClassifier dTree = new DecisionTreeClassifier().
                setFeaturesCol("trainingFeatures").setLabelCol("quality") ;

        DecisionTreeClassificationModel dtModel = dTree.fit(testData);


        Dataset<Row> dtModelPredictions = dtModel.transform(testData);
        dtModelPredictions.show();


        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("quality")
                .setPredictionCol("prediction")
                .setMetricName("f1");

        // Compute the F1 score of DTree
        double f1ScoreDT = evaluator.evaluate(dtModelPredictions);
        System.out.println("F1 Score of Decision Tree: " + f1ScoreDT);

        sparkSession.stop();

    }
    public static Dataset<Row> preProcessData(Dataset<Row> data) {
        String[] columnNames = {"fixed_acidity", "volatile_acidity", "citric_acid", "residual_sugar",
                "chlorides", "free_sulfur_dioxide", "total_sulfur_dioxide", "density",
                "pH", "sulphates", "alcohol", "quality"};

        // VectorAssembler is a transformer that combines a given list of columns into a single vector column.
        for (int i = 0; i < columnNames.length; i++) {
            data = data.withColumnRenamed(data.columns()[i], columnNames[i]);
        }
        String[] inputCols = Arrays.copyOfRange(columnNames, 0, columnNames.length - 1);

        VectorAssembler vectorAssembler = new VectorAssembler()
                .setInputCols(inputCols)
                .setOutputCol("trainingFeatures");

        data = vectorAssembler.transform(data);
        data.show();
        return data;
    }
}
