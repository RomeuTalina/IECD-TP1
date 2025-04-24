package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread{
	
	private Socket clientSocket;
	private BufferedReader is;
	private PrintWriter os;
	private RegistoJogador jogador;
	private Equipa equipaDoJogador; 
	private boolean pronto = false;
	
	public ClientHandler(Socket clientSocket, int numero) throws IOException {
		if(numero == 0) { 
			this.equipaDoJogador = Equipa.PRETO;
		}
		else if(numero == 1) { 
			this.equipaDoJogador = Equipa.BRANCO;
		}
		this.clientSocket = clientSocket; 
		this.is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		this.os = new PrintWriter(clientSocket.getOutputStream(), true);
		this.start();
	} 
	
	public void run() {
	    try {
	        String comando = is.readLine();

	        if (comando != null && comando.equalsIgnoreCase("REGISTO")) {
	            String nick = is.readLine();
	            String pass = is.readLine();
	            String nac = is.readLine();
	            int idade = Integer.parseInt(is.readLine());

	            if (Main.getJogador(nick) != null) {
	                enviar("ERRO: Jogador já existe.");
	                return;
	            }

	            jogador = new RegistoJogador(nick, pass, nac, idade);
	            Main.registarJogador(nick, jogador);
	            enviar("REGISTO_OK");
	            pronto = true;

	        } else if (comando != null && comando.equalsIgnoreCase("LOGIN")) {
	            String nick = is.readLine();
	            String pass = is.readLine();

	            RegistoJogador existente = Main.getJogador(nick);
	            if (existente == null) {
	                enviar("ERRO: Jogador não encontrado.");
	                return;
	            }

	            if (!existente.getPassword().equals(pass)) {
	                enviar("ERRO: Password incorreta.");
	                return;
	            }

	            jogador = existente;
	            enviar("LOGIN_OK");
	            pronto = true;
	        }

	        if (equipaDoJogador == Equipa.PRETO) {
	            enviar("preto");
	        } else if (equipaDoJogador == Equipa.BRANCO) {
	            enviar("branco");
	        }

	        Main.enviarTabuleiroPara(this);

	    } catch(IOException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Lê uma linha da stream da socket do cliente.
	 * @return Retorna a linha lida.
	 * @throws IOException
	 */
	public String ler() throws IOException {
		return is.readLine();
	}
	
	/**
	 * Envia uma string para a socket do cliente.
	 * @param S é a string a enviar.
	 * @throws IOException
	 */
	public void enviar(String s) throws IOException {
		os.println(s);
	}
	
	public String toString() {
		return clientSocket.getInetAddress().getHostAddress();
	}
	
	public Equipa getEquipa() {
		return equipaDoJogador;
	}
	
	public void close() throws IOException {
		os.close();
		is.close();
		clientSocket.close();
	}
	
	public boolean isPronto() {
		return pronto;
	}
	
	
	
	
}
