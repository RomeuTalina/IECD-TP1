package server;

//import java.util.Arrays;

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
    
    public boolean verificarVitoria(int linha, int coluna, Equipa equipa) {
    	 return conta(linha, coluna, 0, 1,  equipa) + conta(linha, coluna, 0, -1, equipa) - 1 >= 5 || // horizontal
    	           conta(linha, coluna, 1, 0,  equipa) + conta(linha, coluna, -1, 0, equipa) - 1 >= 5 || // vertical
    	           conta(linha, coluna, 1, 1,  equipa) + conta(linha, coluna, -1,-1, equipa) - 1 >= 5 || // diagonal para baixo e lado direit
    	           conta(linha, coluna, 1,-1,  equipa) + conta(linha, coluna, -1, 1, equipa) - 1 >= 5;   // diagonal para baixo e lado esquerdo
    }
    
    private int conta(int linha, int coluna, int direcaoLinha, int direcaoColuna, Equipa equipa) {
    	
    	char simbolo;
    	if(equipa == Equipa.PRETO) {
    		simbolo = 'P';
    	}else {
    		simbolo = 'B';
    	}
    	
    	int numPecas = 0;
    	while(linha >= 0 && linha < 15 && coluna >= 0 && coluna <15 && espacos[linha][coluna] == simbolo) {
    		numPecas++;
    		linha += direcaoLinha;
    		coluna += direcaoColuna;
    	}
    	return numPecas;
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
