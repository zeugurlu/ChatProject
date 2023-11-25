/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Zehra
 */
public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers =  new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    String username;
    
    
    
    
    
    public ClientHandler(Socket socket) {
        try{
            this.socket = socket;
            //Karakter olarak yollamak ve efficent olması için bufferedWriter ile yolluyoruz
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = bufferedReader.readLine();
            clientHandlers.add(this);
            
            for(int i = 0; i<clientHandlers.size();i++){
                if(clientHandlers.get(i).socket == null){
                    clientHandlers.remove(i);
                }
            
            
            }
            
            
            //Client tarafı liste güncellemesi için işlemler
            String clientList = "101";
            for (ClientHandler client : clientHandlers){
                clientList += ","+client.username;
            }
            clientList +="-";
            
            
            
            //Bağlanma mesajı ile clientlerin isim listesini de yolluyoruz
            //101 client mesajı olma göstergesi
            // , client ayıracı
            //- mesaj liste ayıracı
            broadcastMessage(clientList + username + " has connected to the server>");
             
        }catch(Exception e){
            System.out.println("Something went wrong in client handler constructor...\nClosed");
            
            
            
            closeAttributes(socket , bufferedReader , bufferedWriter);
            
            
            
        }
        
        
    }

    
    @Override
    public void run() {
        String messageFromClient;
        while(socket.isConnected()){
            try{
                //clientten okuyor
                while(true){
                    messageFromClient = bufferedReader.readLine();
                    
                    
                    //İsim: 401 Serverdan çıkış yapacağını client haber veriyor
                    //Serverdan çıkış için gelen mesajı kullanarak buradan çıkış yapıyoruz

                    if(messageFromClient.split(" ")[1].equals("401")){
                        
                       
                        
                        removeFromList(messageFromClient.split(" ")[2]);
                        
                    }
                    System.out.println(messageFromClient);
                    broadcastMessage(messageFromClient);
                    
                    //20 saniyede bir cliente güncel liste yollanıyor.
                    new Thread(() -> {
                        while (socket.isConnected()) {
                            try {
                                String clientList = "101";
                                for (ClientHandler client : clientHandlers) {
                                    clientList += "," + client.username;
                                }
                                clientList += "-";
                                broadcastMessage(clientList +"/");
                                Thread.sleep(20000);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    
                    
                    
                }
            }catch(Exception e){
                System.out.println("Something went wrong while sending or recieving message");
                closeAttributes(socket , bufferedReader , bufferedWriter);
                //Client çıkarsa duruyor
                break;
            }
        }
        
        
    }
    
    public void removeFromList(String name){
        for(ClientHandler ch : clientHandlers ){
            if(ch.username.equals(name)){
                clientHandlers.remove(ch);
            }
        }
    }
    
    
    
    //Grup chat
    public void broadcastMessage (String message){
        
        for(ClientHandler clientHandler : clientHandlers){
            try{
                //Client listesindeki username bizim usernamemize eşit değilse ona mesaj atıyoruz(Kendimize de mesaj atmak istemiyoruz)
                //if(!clientHandler.username.equals(username)){
             
                    System.out.println(clientHandler.username);
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
       
                //}
            }catch(Exception e){
                System.out.println("Something went wrong to broadcast...\nClosed");
                
                
                
                closeAttributes(socket , bufferedReader , bufferedWriter);
            }
                
        }
       
    }
   
    
  
    public void removeClientHandler(){

        clientHandlers.remove(this);
        
        //Client tarafı liste güncellemesi için işlemler
        String clientList = "101";
        for (ClientHandler client : clientHandlers){
            clientList += ","+client.username;
        }
        clientList +="-";
            
        //Bağlanma mesajı ile clientlerin isim listesini de yolluyoruz
        //101 client mesajı olma göstergesi
        // , client ayıracı
        //- mesaj liste ayıracı
        
        System.out.println(clientList);
        broadcastMessage(clientList + this.username + " has left>");  
    }
    
   
    
    
    
    public void closeAttributes(Socket socket , BufferedReader bufferedReader , BufferedWriter bufferedWriter){
        removeClientHandler();
        //-----------------------------------------------Client listesini yollayacağız tekrardan(Ekleme yap) -----------------------------------------------------------
        try{
            if(socket != null){
                socket.close();
                socket =null;
            }
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
        }catch(Exception e){
            System.out.println("Something went wrong in closein client handler...");
        }
        
    }
    

    
    
}
