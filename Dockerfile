FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

#Start with maven and java installed, and nickname this step build
#make a folder called /app inside the docker "worksite"
#copy my pom.xml to the docker "worksite"
#download everything my project needs from maven 
#copy my code into the new worksite
#compile, skipping tests to make this faster (this is only for testing, best practice is to not do this for security reasons)

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

#starting with java installed
#go to the app folder, and grab the compiled java .jar we made
#tell the computer that we only listen on port 8080
#run the app, identical to typing "java -jar app.jar" in terminal













#docker website example
#FROM python:3.13
#WORKDIR /usr/local/app

# Install the application dependencies
#COPY requirements.txt ./
#RUN pip install --no-cache-dir -r requirements.txt

# Copy in the source code
#COPY src ./src
#EXPOSE 8080

# Setup an app user so the container doesn't run as the root user
#RUN useradd app
#USER app

#CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8080"]