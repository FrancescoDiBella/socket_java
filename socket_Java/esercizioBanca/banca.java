package esercizioBanca;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class banca {
    static public enum stati { SOMMA, VERSARE, PRELEVARE, UTENZA, ERRORE, CHIUSURA};
    static public int indexConto = -1;
    static public int [] conto = new int[10];
    static public stati state = stati.ERRORE;
    static public boolean OK = true;
    static public boolean CONTINUE = true;
    static public final int PORT = 3999;
    static public int TIMER = 0;
    static public final int MAX_TIMER = 20;

    static public stati convalideString(String str){
        if(str.length()<=5){
            if(str.length() == 1 && (Character.compare(str.charAt(0), 'S') == 0)){
                if(indexConto==-1) return stati.ERRORE;
                return stati.SOMMA;
            }
            else if(str.length() == 2 && (Character.compare(str.charAt(0), 'U') == 0)){
                if((Character.compare(str.charAt(1), '0')>= 0) && (Character.compare(str.charAt(1), '9') <=0)){
                    return stati.UTENZA;
                }else return stati.ERRORE;
            } 
            else if(str.length() == 5){
                if((Character.compare(str.charAt(0), 'P') == 0) || (Character.compare(str.charAt(0), 'V') == 0)){
                    stati ok = stati.VERSARE;
                    for(int i = 1; i < str.length()-1; i++){
                        if((Character.compare(str.charAt(i), '0')< 0) || (Character.compare(str.charAt(i), '9') >0)){
                            ok =  stati.ERRORE;
                        }
                    }
                    if(ok != stati.ERRORE){
                        if(Character.compare(str.charAt(0), 'P') == 0){
                            ok = stati.PRELEVARE;
                        }
                    }
                    return ok;
                }else return stati.ERRORE;
            }else return stati.ERRORE;
        } else if(str.length() == 6){
            if(Objects.equals( str, "logout")){
                return stati.CHIUSURA;
            }else return stati.ERRORE;
        } else return stati.ERRORE;
    }

    static public void setIndexConto(String str){
        int convertedNumb = Integer.parseInt(str.substring(1));
        indexConto = convertedNumb;
        return;
    }

    static public void prelevaConto(String str){
        if(indexConto==-1) return;
        int convertedNumb = Integer.parseInt(str.substring(1));
        conto[indexConto] = conto[indexConto] - convertedNumb;
        return;
    }

    static public void versaConto(String str){
        if(indexConto==-1) return;
        int convertedNumb = Integer.parseInt(str.substring(1));
        System.out.println("VALORE DI index " + indexConto);
        conto[indexConto] += convertedNumb;
        System.out.println("VALORE DI index DOPO VERSA CONTO " + indexConto);

        return;
    }

    static public String getConto(){
        String msg = "Il saldo del conto numero " + indexConto + " è : $" + conto[indexConto] + "\n";
        return msg;
    }

    static public void callOperation(String str){
        state = convalideString(str);
        if(state == stati.UTENZA) setIndexConto(str);
        else if(state == stati.PRELEVARE) prelevaConto(str);
        else if( state == stati.VERSARE) versaConto(str);
        else if( state == stati.CHIUSURA) setOK();
        else return;
    }

    static public String getMsgToClient(String string){
        switch(state){
            case SOMMA:
                return getConto();
            case ERRORE:
                return "ERROR\n";
            case VERSARE:
                return "Somma versata correttamente sul conto numero: " + indexConto + "\n";
            case PRELEVARE:
                return "Somma prelevata correttamente dal conto numero: " + indexConto + "\n";
            case UTENZA:
                return "Utenza cambiata correttamente! Stai operando sul conto numero: " + indexConto + "\n";
            case CHIUSURA:
                return "Chiusura sessione in corso...\nEND SESSION\n";
            default:
                return "ERROR\n";
        }
    }

    static public void setOK(){
        OK = !OK;
    }


    //codice lato server
    public static void main(String[] args)  throws IOException{
        //Socket del server per la comunicazione con il client
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("BANKSERVER: STARTED ");
        System.out.println("Server socket: " + serverSocket);

        Socket clientSocket = null;

        BufferedReader bufIN = null;
        PrintWriter prtWR = null;
        
        while(TIMER  < MAX_TIMER){
            try{
                //bloccante fino a quando non avverrà la connessione tra client e server
                clientSocket = serverSocket.accept();
                System.out.println("Connection enstablished with client socket: " + clientSocket);
    
                //creiamo stream input da client
                InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
                bufIN = new BufferedReader(isr);
                
                //creiamo lo stream output per il client
                OutputStreamWriter osw = new OutputStreamWriter(clientSocket.getOutputStream());
                BufferedWriter bufWR = new BufferedWriter(osw);
                prtWR = new PrintWriter(bufWR, true);
    
                while(OK){
                    prtWR.println("Digitare il comando [ digitare logout per terminare sessione]: ");
                    String op = bufIN.readLine();
                    callOperation(op);
                    String msg = getMsgToClient(op);
                    System.out.println(msg);
                    prtWR.print(msg);
                    if(OK==false){
                        prtWR.close();
                        clientSocket.close();
                    }
                }
                setOK();
    
            }catch(IOException e){
                System.err.println("Accept Failed...");
                System.exit(1);
            }
            TIMER++;

        }

        bufIN.close();
        serverSocket.close();
    }
}
