## How to Run

* `./gradlew build`
* `java -jar build/libs/reactorbenchmark-1.0.1.jar`

## Reproduce PathPatternParser issue

* Start server from previous section 
* Using _wrk_ simulate highload with 4 threads and 128 connections 
`wrk -t4 -c128 -d2s http://localhost:8080`

* Server will fail with java.lang.IndexOutOfBoundsException

Issue: https://jira.spring.io/browse/SPR-15246

## Performance

Test server/client on same machine(MacBook Pro Mid-2015 MJLT2LL/A) with latest JDK8.

Warning: make single request to Reactor endpoint before test(due the issue https://jira.spring.io/browse/SPR-15246)

```bash
Reactor Netty

wrk -t4 -c128 -d30s http://localhost:8080 -s pipeline.lua --latency                                                                         
Running 30s test @ http://localhost:8080
  4 threads and 128 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     3.99ms    7.58ms 251.57ms   97.06%
    Req/Sec    10.26k     1.84k   14.34k    89.50%
  Latency Distribution
     50%    2.80ms
     75%    3.40ms
     90%    3.72ms
     99%   30.94ms
  1225276 requests in 30.01s, 128.54MB read
Requests/sec:  40823.07
Transfer/sec:      4.28MB

Reactor Undertow

wrk -t4 -c128 -d30s http://localhost:8080 -s pipeline.lua --latency                                                                            
Running 30s test @ http://localhost:8080
  4 threads and 128 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     2.59ms    3.82ms 123.07ms   97.92%
    Req/Sec    14.17k     2.19k   19.48k    91.92%
  Latency Distribution
     50%    2.04ms
     75%    2.53ms
     90%    2.71ms
     99%   13.46ms
  1692019 requests in 30.01s, 277.55MB read
Requests/sec:  56375.78
Transfer/sec:      9.25MB

```

Clean Reactor App (w/o Spring)
```bash
wrk -t4 -c128 -d30s http://localhost:8080 -s pipeline.lua --latency                                                                                                   [±feature/reactor-improve ●●●]
Running 30s test @ http://localhost:8080
  4 threads and 128 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     3.23ms    8.42ms 189.84ms   99.28%
    Req/Sec    12.15k     1.03k   14.16k    87.29%
  Latency Distribution
     50%    2.49ms
     75%    2.60ms
     90%    3.26ms
     99%    4.56ms
  1447286 requests in 30.01s, 96.62MB read
Requests/sec:  48227.06
Transfer/sec:      3.22MB
```

And compare with [Light Java](https://github.com/aliaksei-lithium/microservices-framework-benchmark/tree/master/light-java) with Undertow  

```bash
wrk -t4 -c128 -d30s http://localhost:8080 -s pipeline.lua --latency                                                                          
Running 30s test @ http://localhost:8080
  4 threads and 128 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.62ms  289.84us  11.48ms   86.76%
    Req/Sec    19.78k     3.03k   85.31k    85.35%
  Latency Distribution
     50%    1.52ms
     75%    1.56ms
     90%    2.04ms
     99%    2.62ms
  2363921 requests in 30.10s, 238.97MB read
Requests/sec:  78530.42
Transfer/sec:      7.94MB
```

So, with Light Java
* There is no problem with latency
* Much higher throughput 