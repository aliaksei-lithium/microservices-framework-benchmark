## How to Run

* `./gradlew build`
* `java -jar build/libs/reactorbenchmark-1.0.1.jar`

## Reproduce PathPatternParser issue

* Start server from previous section 
* Using _wrk_ simulate highload with 4 threads and 128 connections 
`wrk -t4 -c128 -d2s http://localhost:8080`

* Server will fail with java.lang.IndexOutOfBoundsException

Issue: https://jira.spring.io/browse/SPR-15246
