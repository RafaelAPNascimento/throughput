package org.example.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Server {

    private static final Logger LOG = Logger.getLogger(Server.class.getName());

    private static final int POOL_SIZE = 2;

    private ZookeeperService service;

    public Server() throws IOException, InterruptedException, KeeperException {
        service = new ZookeeperService();
        service.addNodeToCluster();
    }

    public void startServer() throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8585), 0);
        server.createContext("/api/converter/dec-to-hex", new RequestHandler());
        Executor executor = Executors.newFixedThreadPool(POOL_SIZE);
        server.setExecutor(executor);
        server.start();
    }

    private class RequestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            goingToCrash();
            String query = exchange.getRequestURI().getQuery();
            LOG.info(query);
            String[] keyValue = query.split("=");

            String hexaDecimalValue = Integer.toHexString(Integer.valueOf(keyValue[1]));

            byte[] response = hexaDecimalValue.getBytes();
            exchange.sendResponseHeaders(200, response.length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response);
            outputStream.close();
        }
    }

    public void goingToCrash() {
        Runnable run = () -> {
            while (true) {
                Random r = new Random();
                int n = r.nextInt(3);
                if (n == 2) {
                    LOG.severe("Application crashed. Exiting...");
                    System.exit(1);
                }
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        Executor executor = Executors.newFixedThreadPool(2);
        executor.execute(run);
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        Server server = new Server();
        server.startServer();
    }
}
