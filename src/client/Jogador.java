package client;

import java.util.Scanner;

import server.Tabuleiro;

public class Jogador {

	public Equipa equipa;
	private Scanner scan = new Scanner(System.in);

    public Jogador(String equipaString){
    	if(equipaString.equals("branco")) {
    		this.equipa = Equipa.BRANCO;
    	}
    	else if(equipaString.equals("preto")) {
    		this.equipa = Equipa.PRETO;
    	}
    }

    /**
     * O cliente pede ao utilizador as coordenadas da peça que quer colocar, e adiciona
     * o caracter correspondente à sua equipa ao array do tabuleiro. 
     * @param tabuleiro
     */
	public int[] jogar() {
		System.out.println("Em que linha pretende colocar a peça?\n");
		int linha = Integer.parseInt(scan.nextLine());
		System.out.println("Em que coluna pretende colocar a peça?\n");
		int coluna = Integer.parseInt(scan.nextLine());
		return new int[]{linha, coluna};
	}
	
	/**
	 * O cliente verifica se é o turno do seu jogador. Caso seja, inicia a jogada.
	 * @param turno
	 */
	public int[] verificarTurno(int turno) { 
		if(equipa == Equipa.PRETO) {
           	if(turno % 2 == 0) {
           		return jogar();
            }
        }
    	else if(equipa == Equipa.BRANCO){
    		if(turno % 2 == 1) {
    			return jogar();
    		}
    	}
		return null;
	}
}