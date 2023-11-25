/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test5;

import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Zehra
 */
public class Server {
    private ServerSocket serverSocket;

    
    
    
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    
    
    
    
    
    
    
    
    public void startServer(){
        try{
            System.out.println("Server has started");
            while(!serverSocket.isClosed()){
                //Clientin bağlanmasını bekliyoruz
                Socket socket = serverSocket.accept();
                System.out.println("New client has connectred");
                
                //Thred kullanarak clientlerin aktivitelerini ypmalarını sağlayacak
                ClientHandler clientHandler = new ClientHandler(socket);
                
                System.out.println("Hi " + clientHandler.username);
                
                
                Thread thread = new Thread(clientHandler);
                thread.start();
                
                
                
                
                
            }
        }catch(Exception e){
            System.out.println("Somethin went wrong in starting server or server has closed");
            closeServerSocket();
        }
    }
    
    
    
    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch(Exception e){
            System.out.println("A problem has occured: ");
            e.printStackTrace();
            System.out.println(" ");
            
        }
    }
    
    
    //bunu fonksiyon haline getirebiliriz ileride
    public static void main(String[] args) {
        try{
            
            ServerSocket serverSocket = new ServerSocket(8000);
            Server server = new Server(serverSocket);
            server.startServer();
        }catch(Exception e){
            System.out.println("Something went wrong...");
            
        }
    }
    
}

