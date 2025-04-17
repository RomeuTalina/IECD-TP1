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
            espacos[i] = Arrays.copyOfRange(temp, i, i*15);
        }
    } 
    
    public void atualizar(String tabuleiroString) { 
        char[] temp = tabuleiroString.toCharArray();
        for(int i = 0; i < 15; i ++){
            espacos[i] = Arrays.copyOfRange(temp, i, i*15);
        }
    }

    public String toString(){
        return Arrays.deepToString(espacos);
    }
}
