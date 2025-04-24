package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ClientHandler extends Thread{
	
	private Socket clientSocket;
	private BufferedReader is;
	private PrintWriter os;
	private RegistoJogador jogador;
	private Equipa equipaDoJogador; 
	private String caminhoFoto;
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
	            
	            // Corrigir a ordem da leitura: primeiro a idade (como int)
	            String idadeStr = is.readLine();
	            int idade = Integer.parseInt(idadeStr); // A idade deve ser lida separadamente como um número

	            // Depois, lemos o caminho da foto como string
	            String caminhoFoto = is.readLine();  

	            if (Main.getJogador(nick) != null) {
	                enviar("ERRO: Jogador já existe.");
	                return;
	            }

	            // Verificar se a foto existe no caminho fornecido
	            File fotoOriginal = new File(caminhoFoto);
	            if (!fotoOriginal.exists()) {
	                enviar("ERRO: Foto não encontrada no caminho fornecido.");
	                return;
	            }

	            // Mover a foto para um diretório centralizado no servidor
	            String diretórioFotos = "./fotos/";  // Diretório onde as fotos serão armazenadas
	            File pastaFotos = new File(diretórioFotos);
	            if (!pastaFotos.exists()) {
	                pastaFotos.mkdir();  // Cria o diretório caso não exista
	            }

	            // Copiar a foto para o diretório 'fotos' do servidor
	            File fotoDestino = new File(diretórioFotos + fotoOriginal.getName()); // Nome da foto será mantido
	            try {
	                Files.copy(fotoOriginal.toPath(), fotoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
	                System.out.println("Foto movida para: " + fotoDestino.getPath());
	            } catch (IOException e) {
	                enviar("ERRO: Não foi possível mover a foto para o diretório do servidor.");
	                e.printStackTrace();
	                return;
	            }

	            // Criar o jogador com o novo caminho da foto
	            jogador = new RegistoJogador(nick, pass, nac, idade, fotoDestino.getPath());  // Salva o novo caminho da foto

	            // Registra o jogador no servidor
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

	        // Enviar equipe para o jogador
	        if (equipaDoJogador == Equipa.PRETO) {
	            enviar("preto");
	        } else if (equipaDoJogador == Equipa.BRANCO) {
	            enviar("branco");
	        }

	        // Enviar o tabuleiro para o jogador
	        Main.enviarTabuleiroPara(this);

	    } catch(IOException e) {
	        System.err.println("ERRO no handler " + this);  // Mostra o cliente que gerou o erro
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
