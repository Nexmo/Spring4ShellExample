APP_NAME = spring4shell-demo
IMAGE_NAME = test-app
CONTAINER_NAME = test-app

.PHONY: build package docker-build run stop clean shell logs check-shell restart

# Build Java project
build:
	mvn clean package -DskipTests

# Build Docker image (builds Java first)
# Podman needs to be aliased as docker
docker-build: build
	podman build -t $(IMAGE_NAME) .

# Run container
run: docker-build
	podman run -d --name $(CONTAINER_NAME) -p 8080:8080 $(IMAGE_NAME)

# Stop and remove container
stop:
	podman stop $(CONTAINER_NAME) || true
	podman rm $(CONTAINER_NAME) || true

# Clean everything
clean: stop
	mvn clean
	podman rmi $(IMAGE_NAME) || true

# Shell into container
shell:
	podman exec -it $(CONTAINER_NAME) /bin/bash

# View container logs
logs:
	podman logs -f $(CONTAINER_NAME)

# Full restart
restart: stop run