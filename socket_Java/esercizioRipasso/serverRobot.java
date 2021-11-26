package esercizioRipasso;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Random;

    /*  
        -chiedere il nickname dell'utente
        -risponde al messaggio ricevuto con stringhe casuali
        -la connessione si chiude se il server riceve la stringa "bye"
        [..] quel punto serverRobot saluta l'utente dicendo il nome
    */

public class serverRobot {
    static public String clientNickName;
    static public String endConnection = "bye";
    static public int PORT = 3999;
    static public boolean run = true;

    public static String getRandomString(){
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
    
        String generatedString = random.ints(leftLimit, rightLimit + 1)
          .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
          .limit(targetStringLength)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString();
    
        return generatedString;
    }

    public static void main(String[] args) throws IOException{
        //creiamo la socket del server
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("ServerSocket initilized! " + serverSocket);

        //creiamo la socket del client
        Socket clientSocket;
        //System.out.println("ServerSocket initilized! ");

        PrintWriter printWriter = null;
        BufferedReader buffReader = null;

            try{
                //accettiamo la connessione, il metodo accept() è bloccante
                clientSocket = serverSocket.accept();
                
                InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
                buffReader = new BufferedReader(isr);

                OutputStreamWriter osw = new OutputStreamWriter(clientSocket.getOutputStream());
                BufferedWriter buffWriter = new BufferedWriter(osw);
                printWriter = new PrintWriter(buffWriter, true);
                
                printWriter.println("Ciao cono serverRobot\nInserisci il tuo nickname! ... \t");
                clientNickName = buffReader.readLine();


                while(run == true){
                    printWriter.println("Ciao " + clientNickName + "! Digita quello che vuoi! ['bye' per uscire] :");
                    printWriter.print("\t\t\t\t");
                    String op = buffReader.readLine();

                    if(Objects.equals(op , endConnection)){
                        printWriter.println("| serverRobot: " +" Alla prossima " + clientNickName + "!");
                        run = !run;
                    }else{
                        //metodo che genera la stringa casuale
                        printWriter.println("| serverRobot[stringa casuale]: " + getRandomString());
                    }
                }

            }catch(IOException e){
                System.out.print("Error on accept(): \n");
                serverSocket.close();
                return;
            }

        serverSocket.close();
    }
}
