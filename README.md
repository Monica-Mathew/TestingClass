Cloud Computing
Programming assignment

Wine Quality Prediction Module 
1. Single Instance
2. Docker container

#### Building the project
```
mvn clean package
```

#### Running the project
```
spark-submit --class org.example.WineQualityTesting --master 'local[*]' target/TestingClass-1.0-SNAPSHOT.jar ValidationDataset.csv
```
#### Docker container
```
docker pull monicamathew/prediction-app:latest
sudo docker run -v "<PATH-TO-DATA>:/data" prediction-app /data/ValidationDataset.csv
```