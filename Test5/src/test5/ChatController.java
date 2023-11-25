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
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javax.swing.event.ChangeListener;

/**
 * FXML Controller class
 *
 * @author ErenK
 */
public class ChatController implements Initializable {

    
    @FXML
    private BorderPane chatBorderPane;
    @FXML
    private TextArea textAreaMsg;
    @FXML
    private TextField textFieldMsg;
    @FXML
    private Button sentBtnMsg;
    @FXML
    private ListView<String> listViewMsg;
    @FXML
    private Label nameLabel;
  
    
    
    
    
    @FXML
    private VBox chatScreen;
    @FXML
    private AnchorPane loginScreen;
    
    @FXML
    private TextField portField;

    @FXML
    private TextField usernameField;
    
    
    
    
    
    
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    String username;

    String selectedName ;
    
    
    
    
       @FXML
    void loginOnAction(ActionEvent event) {
        try{
            if(!portField.getText().equals("") && !usernameField.getText().equals("")){
                this.username = usernameField.getText();
                nameLabel.setText(username);
                int port = Integer.parseInt(portField.getText());
                this.socket = new Socket("localhost" , port);
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                bufferedWriter.write(this.username);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                listenForMessage();
                loginScreen.setVisible(false);
                chatScreen.setVisible(true);
            }
            
        }catch(Exception e){
            System.out.println(":(");
        }
    }
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // TODO
            chatScreen.setVisible(false);
            selectedName = "Group";
            
            /*
            String[] x = new String[10];
            x[0] = "Eren";
            x[1] = "Ahmet";
            x[2] = "Mehmet";
            x[3] = "Ali";
            x[4] = "Veli";
            x[5] = "Zehra";
            x[6] = "Fatma";
            x[7] = "İsmet";
            x[8] = "Kemal";
            x[9] = "Hilmi";
            Random random = new Random();
            
            this.username = x[random.nextInt(10)];
            nameLabel.setText(username);
            this.socket = new Socket("localhost" , 8000);
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter.write(this.username);
            bufferedWriter.newLine();
            bufferedWriter.flush(); 
            
            
            listenForMessage();
            */
            
            
            
            
        } catch (Exception ex) {
            System.out.println("Exception in initilaze");
            closeAttributes(socket ,bufferedReader , bufferedWriter);
        }
        
    }    

    @FXML
    private void sendOnAction(ActionEvent event) {
        sendMessage(textFieldMsg.getText());
        //textAreaMsg.appendText("You: "+textFieldMsg.getText() + "\n");
        textFieldMsg.clear();
        textFieldMsg.setText("");
        
    }
    
    @FXML
    public void lwMouseClickHandle(MouseEvent event) {
        
        this.selectedName = listViewMsg.getSelectionModel().getSelectedItem().toString();
        
        textAreaMsg.setText("");
        
        
    }
    
    
      @FXML
    void closeOnAction(ActionEvent event) {
       
        sendMessage("401 " + this.username + " left the server");
        javafx.application.Platform.exit();
    
    }
    
    
    
    
    
    public void sendMessage(String message){
        
        try {
    
                
            bufferedWriter.write(username + ": " + message + ">" + this.selectedName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (Exception e) {
            System.out.println("Something went wrong...\nClosing");
            closeAttributes(socket ,bufferedReader , bufferedWriter);
        }  
    }
    
    
    
    
    //Gelen mesajı thrad ile sürekli dinliyoruz
public void listenForMessage() {
    new Thread(new Runnable() {
        String messageReceived;
        @Override
        public void run() {
     
            
            while (true) {
                try {
                    messageReceived = bufferedReader.readLine();
                    if (messageReceived == null) {
                        // Handle the case when the socket is closed
                        break;
                    }
                    System.out.println(messageReceived);

                    // Mesajı kendiniz attıysanız----------------------------------------
                    if (messageReceived.split(":")[0].equals(username)) {
                        
                    }
                    
                    
                    //101 kodu kullanıcının bağlandığında yollanılan liste elemanları
                    // Kullanıcı bağlanma mesajı geldi
                    // Ayrıca kullanıcı listesini de bize yolladı
                    if (messageReceived.split(",")[0].equals("101")) {
                        String[] twoPiece = messageReceived.split("-");
                        
                    //- den sonrası asıl mesaj
                        
                        
                        //eğer - den sonra / varsa bu client handlerın 2 saniyede bir yolladığı güncellemedir yoksa kullanıcı mesajıdır
                        if (messageReceived.split("-")[1].equals("/")) {
                            String message = twoPiece[0];
                            String[] namesWith101 = message.split(",");
                            
                            //@FXML yani o componentin kendi fonksiyonu olmadığı için bu şekilde yapıyoruz
                            //Gelen list view bilgilerini güncelliyoruz
                            Platform.runLater(() -> {
                                listViewMsg.getItems().setAll();
                                for (int i = 1; i < namesWith101.length; i++) {
                                    listViewMsg.getItems().add(namesWith101[i]);
                                }
                                listViewMsg.getItems().add("Group");
                            });
                            
                            //Mesaj yazdırmayacağımız için böyle yaptık bu da aşşağıda equals ile kontrol edildi
                            messageReceived = " ";
                        } else {
                            String message = twoPiece[0];
                            String[] namesWith101 = message.split(",");
                            
                            //@FXML
                            Platform.runLater(() -> {
                                listViewMsg.getItems().setAll();
                                for (int i = 1; i < namesWith101.length; i++) {
                                    listViewMsg.getItems().add(namesWith101[i]);
                                }
                                listViewMsg.getItems().add("Group");
                            });
                            
                            //Gelen mesaj
                            messageReceived = twoPiece[1];
                        }
                    }

                    
                    
                    //Gelecek olan mesaj belirlendi kullanıcıya dağıtılacak
                    //mesaj>hedef
                    if(messageReceived.equals(" ")){
                        continue;
                    }
                    else{
                        
                        
                        
                        
                        /*
                        -Eğer stringde > bulunuyorsa yani mesaj göndermek için işlem yapılıyorsa mesaj ve target client ayrılıyor
                        
                        -If mesaj formu messaj>client -> ayır
                        -Else mesajı direkt mesaj olarak sabitle
                        
                        !!!!!!!!!!!!!!!!!!!!!!!!!!!! Sıkıntı çıkarsa target clienti else durumunda "Group" a eşitleyebiliriz zehh
                        */
                        
                        String message = "" ;
                        //Us
                        String targetClient = "";
                        
                        if(messageReceived.contains(">")){
                            String[] totalMessage = messageReceived.split(">");
                            if(totalMessage.length == 1){
                                message = totalMessage[0];
                                targetClient = "Group";
                            }
                            else{
                                message = totalMessage[0];
                                targetClient = totalMessage[1];
                            }
                             
                            
                        
                        }
                        System.out.println("Mesajla hedefi ayırdık mı?:");
                        System.out.println("Gözlem 1: " + message  );
                        System.out.println("Gözlem 2: " + targetClient  );
                        System.out.println(messageReceived);
                        
                        
                        
                        
                        
                        if(username.equals(targetClient)   ||   username.equals(username) ){
                                
                                messageReceived = message;
                                String[] x = message.split(":");
                               /*
                                for(int i = 0; i<x.length;i++){
                                
                                    System.out.println(x[i]);
                                }
                                */
                               
                                if(x[0].equals(username) && x.length == 2){
                                    messageReceived = "You: " + x[1];
                                }
                            
                                //@FXML
                                Platform.runLater(() ->
                                    textAreaMsg.appendText(messageReceived  +"\n")

                                ); 
                            
                            
                        }
                            
                            
                        
                        
                        
                        
                        
                        
                    }

                } catch (Exception e) {
                    System.out.println("Message has come in broken\nClosed");
                    e.printStackTrace();
                    closeAttributes(socket, bufferedReader, bufferedWriter);
                    break;
                }
            }
        }

        
      
    }).start();           
}
/*
    //Örnek beklenen input 101,Eren,Zehra,Ahmet,Mehmet
    public void updateListView(String message){
        
        String[] namesWith101 = message.split(",");
        listViewMsg.getItems().setAll();
        for(int i = 1; i<namesWith101.length;i++){
            listViewMsg.getItems().add(namesWith101[i]);
        }
        listViewMsg.getItems().add("Group");
        
        
        
    }*/
    
    
    
    public void closeAttributes(Socket socket , BufferedReader bufferedReader , BufferedWriter bufferedWriter){
         try{
            if(socket != null){
                socket.close();
            }
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
        }catch(Exception e){
            System.out.println("Something went wrong in closein client...");
            
        }
    }
    
    
    
    
    
    
    
}
