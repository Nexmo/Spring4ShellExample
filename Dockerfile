FROM tomcat:9.0.60-jre11-openjdk-slim-buster

ADD test-app/target/spring4shell-demo.war /usr/local/tomcat/webapps/
EXPOSE 8080

RUN echo 'my big secret' > dbpass
ENV SECRET='this should not be here'

CMD ["catalina.sh", "run"]

LABEL org.opencontainers.image.revision="xpto4"
LABEL org.opencontainers.image.source="https://github.com/your-org/repo"
