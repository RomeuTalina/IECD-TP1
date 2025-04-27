package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
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
    private static String sistemaOperativo = System.getProperty("os.name");

    public static void main(String args[]){
    	
    	System.out.println(sistemaOperativo);

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
        	System.err.println("Erro ao conectar ao servidor. Verifique o IP e a porta.");
            e.printStackTrace();
            System.exit(1);
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
        	        
        	        System.out.println("Deseja mudar a sua foto? (s/n): ");
        	        String mudarFoto = scan.nextLine();
        	        os.println(mudarFoto); 

        	        if (mudarFoto.equalsIgnoreCase("s")) {
        	            System.out.print("Novo caminho da foto: ");
        	            String caminhoFoto = scan.nextLine();

        	            Path caminho = Paths.get(caminhoFoto);
        	            byte[] raw = Files.readAllBytes(caminho);

        	            String fileName = caminho.getFileName().toString();
        	            String img64 = Base64.getEncoder().encodeToString(raw);

        	            os.println(fileName);
        	            os.println(img64.length());
        	            os.println(img64);
        	            os.flush();
        	        }

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
                    String validade = is.readLine();
                    if(validade.equals("false")) {
                    	System.out.println("Esse espaço está ocupado, por favor introduza uma jogada válida.");
                    }
                }
                
                tabuleiro.atualizar(is.readLine());
                
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
    
    public static void fazerRegisto(PrintWriter os, Scanner scan) throws IOException {
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
        
        Path caminho = Paths.get(caminhoFoto);
        byte[] raw = Files.readAllBytes(caminho);

        String fileName = caminho.getFileName().toString();
        String img64 = Base64.getEncoder().encodeToString(raw);

        os.println(fileName);
        os.println(img64.length()); 
        os.println(img64);                        
        os.flush();   
        
    }
}