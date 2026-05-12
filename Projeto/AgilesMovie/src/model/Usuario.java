package model;

import exception.PerfilIncompletoException;

public class Usuario {
	private String nome;
	private int idade;
	private PerfilCinefilo perfil;
	private boolean notificacoesLigadas;

	public Usuario(String nome, int idade, PerfilCinefilo perfil) {
		this.nome = nome;
		this.idade = idade;
		this.perfil = perfil;
	}

	public PerfilCinefilo getPerfil() {
		if (perfil == null)
			throw new PerfilIncompletoException("O usuário não possui um perfil definido.");
		return perfil;
	}

	public boolean isNotificacoesLigadas() {
		return notificacoesLigadas;
	}

	public void setNotificacoesLigadas(boolean notificacoesLigadas) {
		this.notificacoesLigadas = notificacoesLigadas;
	}

	public String getNome() {
		return nome;
	}
}