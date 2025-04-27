package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;

public class ClientHandler extends Thread {

    private final Socket          clientSocket;
    private final BufferedReader  is;
    private final PrintWriter     os;

    private RegistoJogador jogador;
    private final Equipa   equipaDoJogador;
    private boolean pronto = false;

    /* -------------------------------------------------- */
    public ClientHandler(Socket clientSocket, int numero) throws IOException {

        this.equipaDoJogador = (numero == 0 ? Equipa.PRETO : Equipa.BRANCO);
        this.clientSocket    = clientSocket;
        this.is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.os = new PrintWriter   (clientSocket.getOutputStream(), true);

        this.start();
    }

    /* -------------------------------------------------- */
    @Override
    public void run() {
        try {
            String comando = is.readLine();

            /* ============ REGISTO ============ */
            if ("REGISTO".equalsIgnoreCase(comando)) {

                String nick  = is.readLine();
                if (Main.getJogador(nick) != null) {
                    enviar("ERRO: Jogador já existe.");
                    return;
                }

                String pass  = is.readLine();
                String nac   = is.readLine();
                int    idade = Integer.parseInt(is.readLine());

                /* --------- foto em Base-64 --------- */
                String fileName = is.readLine();
                int    tam64    = Integer.parseInt(is.readLine());

                char[] buf = new char[tam64];
                int l = 0;
                while (l < tam64) {
                    int n = is.read(buf, l, tam64 - l);
                    if (n == -1) throw new IOException("Fim prematuro da imagem");
                    l += n;
                }
                String img64 = new String(buf);
                is.readLine();

                byte[] dados = Base64.getDecoder().decode(img64);

                File pastaFotos = new File("./fotos");
                pastaFotos.mkdir();
                File fotoDestino = new File(pastaFotos, fileName);
                try (FileOutputStream fos = new FileOutputStream(fotoDestino)) {
                    fos.write(dados);
                }
                System.out.println("Foto recebida em: " + fotoDestino.getPath());

                /* --------- cria o registo --------- */
                jogador = new RegistoJogador(nick, pass, nac, idade, fotoDestino.getPath());
                Main.registarJogador(nick, jogador);

                enviar("REGISTO_OK");
                enviarCor();              
                pronto = true;               
            }

            /* ============ LOGIN ============ */
            else if ("LOGIN".equalsIgnoreCase(comando)) {

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
                
                String mudarFoto = is.readLine();
                if (mudarFoto.equalsIgnoreCase("s")) {
                    try {
                        String novoNomeFoto = is.readLine();
                        int novoTam64 = Integer.parseInt(is.readLine());
                        char[] buf = new char[novoTam64];
                        int l = 0;
                        while (l < novoTam64) {
                            int n = is.read(buf, l, novoTam64 - l);
                            if (n == -1) throw new IOException("Fim prematuro da imagem");
                            l += n;
                        }
                        String novoImg64 = new String(buf);
                        is.readLine(); 

                        byte[] dados = Base64.getDecoder().decode(novoImg64);

                        File pastaFotos = new File("./fotos");
                        pastaFotos.mkdir();
                        File novaFoto = new File(pastaFotos, novoNomeFoto);
                        try (FileOutputStream fos = new FileOutputStream(novaFoto)) {
                            fos.write(dados);
                        }


                        jogador.setCaminhoFoto(novaFoto.getPath());

                        Main.registarJogador(jogador.getNickname(), jogador);

                        System.out.println("Foto do jogador " + jogador.getNickname() + " foi atualizada.");

                    } catch (Exception e) {
                        System.err.println("Erro ao atualizar a foto do jogador.");
                        e.printStackTrace();
                    }
                }
                
                enviarCor();                  
                pronto = true;                
            }

        } catch (IOException e) {
            System.err.println("ERRO no handler " + this);
            e.printStackTrace();
        }
    }

    /* -------------------------------------------------- */
    private void enviarCor() throws IOException {
        enviar(equipaDoJogador == Equipa.PRETO ? "preto" : "branco");
    }

    public String  ler() throws IOException        { return is.readLine();   }
    public void    enviar(String s)                { os.println(s);          }
    public boolean isPronto()                      { return pronto;          }
    public Equipa  getEquipa()                     { return equipaDoJogador; }
    public RegistoJogador getJogador()             { return jogador;     	 }

    @Override 
    public String toString(){
    	return clientSocket.getInetAddress().getHostAddress();
    }

    public void close() throws IOException {
        os.close(); 
        is.close(); 
        clientSocket.close();
    }
}