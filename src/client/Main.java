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
        scan.nextLine();
        
        try{
            socket = new Socket(host, port);
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os = new PrintWriter(socket.getOutputStream(), true);
        }catch(Exception e){
            e.printStackTrace();
        }

        try{
        	
        	System.out.println("Já tem conta? (s/n): ");
        	String temConta = scan.nextLine();

        	if (temConta.equalsIgnoreCase("s")) {
        	    os.println("LOGIN");
        	    
        	    System.out.print("Nickname: ");
        	    String nick = scan.nextLine();
        	    os.println(nick);
        	    
        	    System.out.print("Password: ");
        	    String pass = scan.nextLine();
        	    os.println(pass);
        	    
        	    

        	    String resposta = is.readLine();
        	    if (resposta.equals("LOGIN_OK")) {
        	        System.out.println("Login efetuado com sucesso.");
        	    } else {
        	        System.out.println("Login falhou: " + resposta);
        	        socket.close();
        	        return;
        	    }

        	} else {
        	    os.println("REGISTO");
        	    fazerRegisto(os, scan);
        	    
        	    String resposta = is.readLine();
        	    if (!resposta.equals("REGISTO_OK")) {
        	        System.out.println("Registo falhou: " + resposta);
        	        socket.close();
        	        return;
        	    }
        	}
        	
        	equipaString = is.readLine();
            tabuleiro = new Tabuleiro(is.readLine());
            jogador = new Jogador(equipaString);

            while (!terminar) {
            	tabuleiro.atualizar(is.readLine());
            	mostrarTabuleiro();

            	String turnoStr = is.readLine();
            	if (turnoStr == null || turnoStr.trim().isEmpty()) {
            	    System.err.println("Erro: turno inválido recebido!");
            	    socket.close();
            	    return;
            	}
            	if (turnoStr.startsWith("FIM|")) {
            	    System.out.println("Vitória do " + turnoStr.substring(4));
            	    terminar = true;
            	    break;
            	}
            	turno = Integer.parseInt(turnoStr.trim());

                if ((turno % 2 == 0 && jogador.equipa == Equipa.PRETO) ||
                    (turno % 2 == 1 && jogador.equipa == Equipa.BRANCO)) {
                    int[] jogada = jogador.jogar();
                    enviarJogada(jogada);
                }
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
    
    public static void fazerRegisto(PrintWriter os, Scanner scan) {
        System.out.println("===== Registo de Jogador =====");
        System.out.print("Nickname: ");
        String nick = scan.nextLine();

        System.out.print("Password: ");
        String pass = scan.nextLine();

        System.out.print("Nacionalidade: ");
        String nac = scan.nextLine();

        System.out.print("Idade: ");
        int idade = Integer.parseInt(scan.nextLine());
        
        System.out.print("Caminho da foto: ");
        String caminhoFoto = scan.nextLine();
        

        os.println(nick);
        os.println(pass);
        os.println(nac);
        os.println(idade);
        os.println(caminhoFoto);
    }
}