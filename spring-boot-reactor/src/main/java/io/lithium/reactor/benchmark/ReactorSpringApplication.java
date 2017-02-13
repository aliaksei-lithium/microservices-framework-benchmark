package io.lithium.reactor.benchmark;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.http.server.reactive.UndertowHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.xnio.Options;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.server.HttpServer;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RouterFunctions.toHttpHandler;

public class ReactorSpringApplication {

    private static final Logger _logger = LoggerFactory.getLogger(ReactorSpringApplication.class);

    private static final String HOST = "localhost";
    private static final int PORT = 8080;


    /*public static void main(String[] args) throws Exception {
        ReactorSpringApplication server = new ReactorSpringApplication();
        server.startReactorServer();
//        server.startUndertow();

        _logger.info("Server started...");
        Thread.currentThread().join();
    }*/

    public RouterFunction<?> routingFunction() {
        return route(
                GET("/"), (req) ->{
//                    _logger.debug(Thread.currentThread().getName());
                    return ServerResponse.ok().body(Mono.just("Hello, World!"), String.class);
                });
    }


    private void startReactorServer() {
        RouterFunction<?> route = routingFunction();
        HttpHandler httpHandler = toHttpHandler(route);

        ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
        HttpServer server = HttpServer.create(HOST, PORT);
        server.newHandler(adapter).block();
    }

    private void startUndertow() {
        RouterFunction<?> route = routingFunction();
        HttpHandler httpHandler = toHttpHandler(route);

        UndertowHttpHandlerAdapter adapter = new UndertowHttpHandlerAdapter(httpHandler);
        // From Light-Java
        Undertow server = Undertow.builder().addHttpListener(PORT, HOST).setBufferSize(16384).setIoThreads(Runtime.getRuntime().availableProcessors() * 2).setSocketOption(Options.BACKLOG, Integer.valueOf(10000)).setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, Boolean.valueOf(false)).setServerOption(UndertowOptions.ALWAYS_SET_DATE, Boolean.valueOf(true)).setServerOption(UndertowOptions.RECORD_REQUEST_START_TIME, Boolean.valueOf(false)).setHandler(adapter).setWorkerThreads(200).build();
        server.start();
    }
}
