package io.lithium.reactor.benchmark;

import io.netty.buffer.Unpooled;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.NettyContext;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.server.HttpServer;
import reactor.ipc.netty.resources.PoolResources;

import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.time.Duration;

// Application that use only Reactor core code w/o Spring
// Inspired by https://github.com/reactor/projectreactor.io/blob/master/src/main/java/io/projectreactor/Application.java
public class ReactorCleanApplication {
    private static final int PORT = 8080;
    private static final String HOST = "localhost";

    private final HttpServer server = HttpServer.create(HOST, PORT);
    private final HttpClient client = HttpClient.create(opts -> opts.poolResources(PoolResources.elastic("proxy")));

    private final Mono<? extends NettyContext> context;

    public ReactorCleanApplication() {
        final byte[] hwb = "Hello, World!".getBytes(Charset.forName("UTF-8"));
        this.context = server.newRouter((r -> {
            r.get("/", (req, res) -> res.sendObject(Mono.just(Unpooled.copiedBuffer(hwb))))
             // just for usage client example
             .get("/reactor", (req, res) -> client.get("http://next.projectreactor.io")
                                                  .then(rg -> res.send(rg.receive().retain()).then()));
        }));
    }

    public static void main(String... args) throws Exception {
        ReactorCleanApplication app = new ReactorCleanApplication();
        app.startAndAwait();
    }

    public void startAndAwait() {
        context.doOnNext(this::startLog)
               .block()
               .onClose()
               .block();
    }

    private void startLog(NettyContext c) {
        System.out.printf("Server started in %d ms on: %s\n",
                          Duration.ofNanos(ManagementFactory.getThreadMXBean()
                                                            .getThreadCpuTime(Thread.currentThread().getId()))
                                  .toMillis(), c.address());
    }
}
