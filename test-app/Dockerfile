FROM tomcat:9.0.60-jre11-openjdk-slim-buster

# RUN rm -rf /usr/local/tomcat/webapps/*

ADD target/spring4shell-demo.war /usr/local/tomcat/webapps/

EXPOSE 8080

CMD ["catalina.sh", "run"]