.PHONY: build-backend
build-backend:
	docker-compose build courseBack

.PHONY: build-frontend
build-frontend:
	docker-compose build courseFront

.PHONY: run-backend
run-backend: build-backend
	touch transactions.log
	docker-compose up courseBack

.PHONY: run
run: build-backend build-frontend
	touch transactions.log
	docker-compose up

.PHONY: test
test: build-backend
	$(PWD)/gradlew --no-daemon --console=plain clean test