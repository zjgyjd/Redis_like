package com.zjgyjd;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public void run(int port) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    logger.info("{}已连接", serverSocket.getInetAddress().getHostName());
                    while (true) {
                            InputStream is = socket.getInputStream();
                            OutputStream os = socket.getOutputStream();
                        try {
                            Command command = null;
                            try {
                                command = Protocol.readCommand(is);
                                if (command != null) {
                                    command.run(os);
                                } else {
                                    logger.info("{}已关闭,请重新连接", serverSocket.getInetAddress().getHostAddress());
                                    is.close();
                                    os.close();
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }

                    }
                }

            }

        }
    }

    public static void main(String[] args) throws IOException {
        new Server().run(6379);
    }
}
