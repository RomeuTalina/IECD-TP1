package server;

import java.time.Duration;
import java.util.ArrayList;

public class RegistoJogador {
	
	 private String nickname;
	 private String password; 
	 private String nacionalidade;
	 private int idade;
	 //private byte[] foto; 
	    
	 private int vitorias;
	 private int derrotas;
	 private ArrayList<Duration> temposPorJogo;
	    
	 public RegistoJogador(String nickname, String password, String nacionalidade, int idade) { //Acrescentar um argumento para a foto 
	    	
		 this.nickname = nickname;
		 this.password = password;
		 this.nacionalidade = nacionalidade;
		 this.idade = idade;
		 //this.foto = foto;
	    	
		 this.vitorias = 0;
		 this.derrotas = 0;
		 this.temposPorJogo = new ArrayList<>();
		 
	 }
	 
	 public void registarVitoria() {
		 vitorias++;
	 }
	 
	 public void registarDerrota() {
		 derrotas++;
	 }

	 public void adicionarTempo(Duration tempo) {
		 temposPorJogo.add(tempo);
	 }

	 public Duration getTempoTotal() {
		 Duration total = Duration.ZERO; 
		 for (Duration d : temposPorJogo) {   
			 total = total.plus(d); 
		 }
		 return total;
	 }
	 
	 public ArrayList<Duration> getTemposPorJogo() { 
		 return temposPorJogo;
	 }
}
