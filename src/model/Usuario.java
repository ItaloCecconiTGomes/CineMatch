package model;

public class Usuario {
	
	private final String NOME;
	private final int IDADE;
	private final PerfilCinefilo PERFIL;
	
	public Usuario(String nome, int idade, PerfilCinefilo perfil) {
		
		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
		}
		
		if (idade < 0) {
			throw new IllegalArgumentException("Idade não pode ser negativa");
		}
		
		if (perfil == null) {
			throw new IllegalArgumentException("Perfil não pode ser nulo");
		}
		
		NOME = nome;
		IDADE = idade;
		PERFIL = perfil;
	}

	public String getNOME() {
		return NOME;
	}

	public int getIDADE() {
		return IDADE;
	}

	public PerfilCinefilo getPERFIL() {
		return PERFIL;
	}

	@Override
	public String toString() {
		return "Usuario [NOME=" + NOME + ", IDADE=" + IDADE + "]";
	}
	
	
	
}
