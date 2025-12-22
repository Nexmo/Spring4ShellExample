APP_NAME = spring4shell-demo
IMAGE_NAME = vulnerable-spring-app
CONTAINER_NAME = vulnerable-app

.PHONY: build package docker-build run stop clean shell logs check-shell

# Build Java project
build:
	mvn clean package -DskipTests

# Build Docker image (builds Java first)
docker-build: build
	docker build -t $(IMAGE_NAME) .

# Run container
run: docker-build
	docker run -d --name $(CONTAINER_NAME) -p 8080:8080 $(IMAGE_NAME)
	@echo ""
	@echo "App running at: http://localhost:8080/user/register"
	@echo "Chain debug at: http://localhost:8080/debug/chain"

# Stop and remove container
stop:
	docker stop $(CONTAINER_NAME) || true
	docker rm $(CONTAINER_NAME) || true

# Clean everything
clean: stop
	mvn clean
	docker rmi $(IMAGE_NAME) || true

# Shell into container
shell:
	docker exec -it $(CONTAINER_NAME) /bin/bash

# View container logs
logs:
	docker logs -f $(CONTAINER_NAME)

# Full restart
restart: stop run