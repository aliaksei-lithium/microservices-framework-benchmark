package io.lithium.reactor.benchmark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.server.HttpServer;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RouterFunctions.toHttpHandler;

public class ReactorApplication {

    private static final Logger _logger = LoggerFactory.getLogger(ReactorApplication.class);

    private static final String HOST = "localhost";
    private static final int PORT = 8080;


    public static void main(String[] args) throws Exception {
        ReactorApplication server = new ReactorApplication();
        server.startReactorServer();

        _logger.info("Server started...");
        System.in.read();
    }

    public RouterFunction<?> routingFunction() {
        return route(
                GET("/hello"), (req) ->{
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
}
