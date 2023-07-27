package org.example.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Server {

    private static final Logger LOG = Logger.getLogger(Server.class.getName());

    private static final int POOL_SIZE = 2;
    private final int SERVER_PORT;

    private ZookeeperService service;

    public Server(int serverPort) throws IOException, InterruptedException, KeeperException {
        this.SERVER_PORT = serverPort;
        service = new ZookeeperService();
        service.addNodeToCluster();
    }

    public void startServer() throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
        server.createContext("/api/converter/dec-to-hex", new RequestHandler());
        Executor executor = Executors.newFixedThreadPool(POOL_SIZE);
        server.setExecutor(executor);
        server.start();
    }

    private class RequestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

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

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        int serverPort = Integer.parseInt(args[0]);
        Server server = new Server(serverPort);
        server.startServer();
    }
}
