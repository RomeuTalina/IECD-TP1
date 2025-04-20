package server;

import java.util.Arrays;

public class Tabuleiro {

    private char[][] espacos;

    //O construtor recebe uma string pq dps tem de receber do servidor entao assim 
    //da para passar a info
    public Tabuleiro(){
        espacos = new char[15][15];
    } 
    
    public void colocarPeca(int linha, int coluna, Equipa equipa) {
    	if(equipa == Equipa.PRETO) {
    		espacos[linha][coluna] = 'P';
    	}
    	else if(equipa == Equipa.BRANCO) {
    		espacos[linha][coluna] = 'B';
    	}
    }

    public char[][] getEspacos(){
    	return espacos;
    }
    
    public String serializar() {
        StringBuilder sb = new StringBuilder(225);
        for (char[] linha : espacos) sb.append(linha);
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("    "); 
        for (int col = 0; col < 15; col++) {
            sb.append('|').append(String.format("%2d", col));
        }
        sb.append("|\n");

        for (int row = 0; row < 15; row++) {
            sb.append(String.format("%2d ", row));
            for (int col = 0; col < 15; col++) {
                char c = espacos[row][col];
                if (c == '\0') c = ' ';
                sb.append('|').append(' ').append(c);
            }
            sb.append("|\n");
        }
        return sb.toString();
    }
}
