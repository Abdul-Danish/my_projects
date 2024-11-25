FROM openjdk:11-slim as runtime
EXPOSE 8080
EXPOSE 8090
#Set app home folder
ENV APP_HOME /app
#Possibility to set JVM options
ENV JAVA_OPTS=""

RUN apt-get update
RUN apt-get install git -y

#Create base app folder
RUN mkdir $APP_HOME
#Create folder to save configuration files
RUN mkdir $APP_HOME/config
#Create folder with application logs
RUN mkdir $APP_HOME/log

VOLUME $APP_HOME/log
VOLUME $APP_HOME/config

WORKDIR $APP_HOME
#Copy executable jar file from the builder image
COPY /target/*.jar app.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar" ]