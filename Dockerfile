FROM openjdk:11
ENV APP_HOME=/usr/app
ENV MONGO_DB_PORT=27017
ENV MONGO_DB_DATABASE=awms
WORKDIR $APP_HOME
COPY target/awms-1.0.2.war awms.war
EXPOSE 8443
CMD ["java", "-jar", "awms.war"]
