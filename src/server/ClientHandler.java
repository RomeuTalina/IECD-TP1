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
	
	private Equipa equipaDoJogador; 
	
	public ClientHandler(Socket clientSocket, int numero) throws IOException {
		if(numero == 0) { 
			this.equipaDoJogador = Equipa.PRETO;
		}
		else if(numero == 1) { 
			this.equipaDoJogador = Equipa.BRANCO;
		}
		this.clientSocket = clientSocket; 
		this.is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		this.os = new PrintWriter(clientSocket.getOutputStream());
		this.start();
	} 
	
	public void run() {
		try {
			if(equipaDoJogador == Equipa.PRETO) {
				enviar("preto");
			}
			else if(equipaDoJogador == Equipa.BRANCO) {
				enviar("branco");
			}
		}catch(IOException e) {
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
}
