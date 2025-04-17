package server; 
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main{
	
	private static ClientHandler[] clients;

    private static ServerSocket serverSocket;

    private static int port = 5025;
    
    private static Tabuleiro tabuleiro; 
    
    private static int turno = 0;
    
    private static int nJogadores = 0;
    
    private static boolean waiting = true;
    
    private static boolean terminar = false;

    public static void main(String[] args) {
    	
    	System.out.println("pila");

        tabuleiro = new Tabuleiro();
        
        clients = new ClientHandler[2];

        try{
            serverSocket = new ServerSocket(port); 
            while(waiting) { 
            	clients[nJogadores] = (new ClientHandler(serverSocket.accept(), nJogadores)); 
            	nJogadores++;
            	System.out.println("Jogador " + nJogadores + " conectou-se.");
            	if(nJogadores == 2) waiting = false;
            }
            enviarTabuleiro();
            while(!terminar) {
            	enviar(String.valueOf(turno));
            	int linha = Integer.parseInt(clients[turno % 2].ler());
            	int coluna = Integer.parseInt(clients[turno % 2].ler());
            	if(linha == -1 || coluna == -1) {
            		terminar = true;
            		break;
            	}
            	tabuleiro.colocarPeca(linha, coluna, clients[turno % 2].getEquipa());
            	enviarTabuleiro();
            }
            
            fecharClientes();
        }catch(Exception e){
            e.printStackTrace();  
        } 
    }
    
    private static void enviar(String s) {
    	for(ClientHandler client : clients) {
    		try {
    			client.enviar(s);
    		}catch(IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    public static void enviarTabuleiro() {
    	String tabuleiroString = "";
    	for(int i = 0; i < tabuleiro.getEspacos().length; i++) {
    		tabuleiroString += String.valueOf(tabuleiro.getEspacos()[i]);
    	}
    	for(ClientHandler client : clients) {
    		try { 
    			client.enviar(tabuleiroString);
    		}catch(IOException e) {
    			System.err.println("Erro ao enviar o estado do jogo para o cliente " + client.toString() + ".");
    		}
    	}
    }
    
    private static void fecharClientes() {
    	for(ClientHandler client : clients) {
    		try {
    			client.close();
    		}catch(IOException e) {
    			System.err.println("nsei bro");
    		}
    	}
    }
}
