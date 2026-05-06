package model.enums;

public enum ClassificacaoEtaria {
	LIVRE(0), DEZ(10), DOZE(12), QUATORZE(14), DEZESSEIS(16), DEZOITO(18);

	private final int idadeMaxima;

	ClassificacaoEtaria(int idadeMaxima) {
		this.idadeMaxima = idadeMaxima;
	}

	public int getIdadeMaxima() {
		return idadeMaxima;
	}
}