# Simple Service Poller
Simple Spring Boot HTTP service poller

## Dependencies
* Docker
* Java 11

## Instructions
* Run ``docker-compose up -d`` in root directory to launch the database server
* Run ``gradlew build`` to build the bootJar and assemble the JS
* Run ``java -jar build/libs/servicepoller-1.jar``
* Browse to [http://localhost:8080](http://localhost:8080) in Chrome

## Writeup
Please read ``Assignment Writeup.docx`` for a more detailed write up.
