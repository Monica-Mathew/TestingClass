#Defines a base for your image.
FROM eclipse-temurin:11-jdk

# Sets the working directory
WORKDIR /app

RUN wget https://downloads.apache.org/spark/spark-3.5.1/spark-3.5.1-bin-hadoop3.tgz
RUN tar -xzf spark-3.5.1-bin-hadoop3.tgz
RUN mv spark-3.5.1-bin-hadoop3 /spark
RUN rm spark-3.5.1-bin-hadoop3.tgz

ENV PATH=/spark/bin:$PATH

# Copy the JAR file into the container at /app
COPY target/original-TestingClass-1.0-SNAPSHOT.jar /app/original-TestingClass-1.0-SNAPSHOT.jar
COPY model /model

# The EXPOSE instruction marks that our final image has a service listening on port 8000.
EXPOSE 8080

# Define environment variable
ENV JAVA_OPTS=""

# Run the application
#ENTRYPOINT ["java", "-jar", "TestingClass-1.0-SNAPSHOT.jar"]
ENTRYPOINT ["spark-submit", "--class", "org.example.WineQualityTesting", "--master", "local[*]", "original-TestingClass-1.0-SNAPSHOT.jar"]