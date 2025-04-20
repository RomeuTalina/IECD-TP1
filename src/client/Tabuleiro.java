package client;

import java.util.Arrays;

public class Tabuleiro {

    private char[][] espacos;

    //O construtor recebe uma string pq dps tem de receber do servidor entao assim 
    //da para passar a info
    public Tabuleiro(String tabuleiroString){
        espacos = new char[15][15];
        char[] temp = tabuleiroString.toCharArray();
        for(int i = 0; i < 15; i ++){
            espacos[i] = Arrays.copyOfRange(temp, i*15, (i + 1)*15);
        }
    } 
    
    public void atualizar(String tabuleiroString) { 
        char[] temp = tabuleiroString.toCharArray();
        for(int i = 0; i < 15; i ++){
            espacos[i] = Arrays.copyOfRange(temp, i*15, (i + 1)*15);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("   "); 
        for (int col = 0; col < 15; col++) {
            sb.append(' ').append(String.format("%2d", col));
        }
        sb.append("\n");
        
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
