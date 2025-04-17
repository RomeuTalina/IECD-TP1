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
    public String toString(){
        return Arrays.deepToString(espacos);
    }
}
