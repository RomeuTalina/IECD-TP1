package server;

import java.time.Duration;
import java.util.ArrayList;

public class RegistoJogador {
	
	 private String nickname;
	 private String password; 
	 private String nacionalidade;
	 private int idade;
	 private String caminhoFoto;
	 private int vitorias;
	 private int derrotas;
	 private ArrayList<Duration> temposPorJogo;
	    
	 public RegistoJogador(String nickname, String password, String nacionalidade, int idade, String caminhoFoto) { //Acrescentar um argumento para a foto 
	    	
		 this.nickname = nickname;
		 this.password = password;
		 this.nacionalidade = nacionalidade;
		 this.idade = idade;
		 this.caminhoFoto = caminhoFoto;
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

	public String getPassword() {
		return password;
	}
	
	public String getNickname()      {
		return nickname; 
	}
	
	public String getNacionalidade() { 
		return nacionalidade; 
	}
	
	public int    getIdade(){
		return idade; 
	}
	public int    getVitorias(){
		return vitorias; 
	}
	
	public int    getDerrotas(){
		return derrotas; 
	}
	
	public String getCaminhoFoto() {
	    return caminhoFoto;
	}

	public void setCaminhoFoto(String caminhoFoto) {
	    this.caminhoFoto = caminhoFoto;
	}


}
