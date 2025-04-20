package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Main{

    // --- Jogo ---
    private static Tabuleiro tabuleiro;
    private static String equipaString;
    private static Jogador jogador;
    private static boolean terminar = false;
    private static int turno;

    // --- Comunicação ---
    private static String host;
    private static int port;

    private static Socket socket;
    
    private static BufferedReader is;
    private static PrintWriter os;

    // --- Misc ---
    private static Scanner scan;

    public static void main(String args[]){

        scan = new Scanner(System.in);

        System.out.println("Introduza o endereço IP do servidor (localhost):\n");
        host = scan.nextLine();

        System.out.println("Introduza a porta:");
        port = scan.nextInt();
        
        try{
            socket = new Socket(host, port);
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os = new PrintWriter(socket.getOutputStream(), true);
        }catch(Exception e){
            e.printStackTrace();
        }

        try{

        	equipaString = is.readLine();
            tabuleiro = new Tabuleiro(is.readLine());
            jogador = new Jogador(equipaString);

            while(!terminar) {
            	turno = Integer.parseInt(is.readLine());
            	int[] jogada = jogador.verificarTurno(turno);
            	enviarJogada(jogada);
            	tabuleiro.atualizar(is.readLine());
            	mostrarTabuleiro();
            }
            
            os.close();
            is.close();
            socket.close();

        }catch(IOException e){
            e.printStackTrace();
        } 
        
    }
    
    private static void enviarJogada(int[] jogada) {
    	if(jogada != null) {
    		for(int n : jogada) { 
    			os.println(String.valueOf(n));
    		}
    	}
    }
    
    private static void mostrarTabuleiro() {
    	System.out.println(tabuleiro);
    }
}