package server;

import framework.DIEngine;
import framework.DependencyContainer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

public class Server {

    public static final int TCP_PORT = 8080;

    public static void main(String[] args) throws IOException {

        DIEngine diEngine = new DIEngine();
        Set<Class> allClasses = diEngine.findAllClassesUsingClassLoader("clientCode");
        try {
            diEngine.initializeControllers(allClasses);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        try {
            ServerSocket serverSocket = new ServerSocket(TCP_PORT);
            System.out.println("Server is running at http://localhost:"+TCP_PORT);
            while(true){
                Socket socket = serverSocket.accept();
                new Thread(new ServerThread(socket, diEngine)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
