package server; 
import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
import java.net.ServerSocket;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
//import java.net.Socket;
//import java.util.ArrayList;

public class Main{
	
	private static ClientHandler[] clients;

    private static ServerSocket serverSocket;

    private static int port = 5025;
    
    private static Tabuleiro tabuleiro; 
    
    private static int turno = 0;
    
    private static int nJogadores = 0;
    
    private static boolean waiting = true;
    
    private static boolean terminar = false;
    
    private static Map<String, RegistoJogador> jogadores = Collections.synchronizedMap(XMLDom.carregar());
    
    

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
            	if(nJogadores == 2) {
            		waiting = false;
            		
            	}

            }
            
            while (!clientsProntos()) {
                Thread.sleep(1000);
            }
            System.out.println("Ambos jogadores autenticados. Iniciando o jogo!");
            Instant inicioPartida = Instant.now();   
            enviarTabuleiro();
        	
        	
            
            while(!terminar) {
            	//apaguei o tabuleiro do fim e mudei para aqui
            	enviarTabuleiro();
            	enviar(String.valueOf(turno));
            	int linha = Integer.parseInt(clients[turno % 2].ler());
            	int coluna = Integer.parseInt(clients[turno % 2].ler());
            	if(linha == -1 || coluna == -1) {
            		terminar = true;
            		break;
            	}
            	Equipa equipaDaVez = clients[turno % 2].getEquipa();

            	tabuleiro.colocarPeca(linha, coluna, equipaDaVez);

            	if (tabuleiro.verificarVitoria(linha, coluna, equipaDaVez)) {
            	    ClientHandler vencedorCH  = clients[turno % 2];
            	    ClientHandler perdedorCH  = clients[(turno + 1) % 2];

            	    RegistoJogador vencedor = vencedorCH.getJogador();
            	    RegistoJogador perdedor = perdedorCH.getJogador();

            	    actualizarEstatisticas(vencedor, true);   // vitória +1
            	    actualizarEstatisticas(perdedor, false);  // derrota +1

            	    Duration d = Duration.between(inicioPartida, Instant.now());
            	    vencedor.adicionarTempo(d);
            	    perdedor.adicionarTempo(d);

            	    XMLDom.guardar(jogadores);
            	    enviarTabuleiro();
            	    enviar("FIM|" + equipaDaVez);
            	    terminar = true;
            	} else {
            	    turno++;
            	}
            	
            	
            	
            	
            }
            
            Duration duracaoJogo = Duration.between(inicioPartida, Instant.now());

            for (ClientHandler ch : clients) {
                RegistoJogador r = ch.getJogador();
                if (r != null) {
                    r.adicionarTempo(duracaoJogo);          
                }
            }


            XMLDom.guardar(jogadores);
            
            fecharClientes();
        }catch(Exception e){
            e.printStackTrace();  
        } 
    }
    
    private static void enviar(String s) throws IOException {
    	for(ClientHandler client : clients) {
    		client.enviar(s);
    	}
    }
    
    public static void enviarTabuleiro() throws IOException {
    	String tabuleiroString = tabuleiro.serializar();
    	for(ClientHandler client : clients) {
    		client.enviar(tabuleiroString);
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
    
    public static void registarJogador(String nickname, RegistoJogador jogador) {
        jogadores.put(nickname, jogador);
        XMLDom.guardar(jogadores);
    }
    
    public static RegistoJogador getJogador(String nick) {
        return jogadores.get(nick);
        
    }
    
    public static void enviarTabuleiroPara(ClientHandler client) throws IOException {
        String tabuleiroString = tabuleiro.serializar();
        client.enviar(tabuleiroString);
    }
    
    private static boolean clientsProntos() {
        return clients[0] != null && clients[1] != null &&
               clients[0].isPronto() && clients[1].isPronto();
    }
    
    private static void actualizarEstatisticas(RegistoJogador j, boolean vitoria) {
    	if (vitoria) {
            j.registarVitoria(); 
        } else {
            j.registarDerrota(); 
        }
        XMLDom.guardar(jogadores);  
    }
}
