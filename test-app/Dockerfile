FROM tomcat:9.0.60-jre11-openjdk-slim-buster

ADD target/spring4shell-demo.war /usr/local/tomcat/webapps/

EXPOSE 8888

CMD ["catalina.sh", "run"]