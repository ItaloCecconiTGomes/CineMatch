package service;

import model.Filme;
import model.PerfilCinefilo;
import model.enums.Genero;

public class CalculadoraScore {
	private static final double PESO_GENERO = 0.50;
	private static final double PESO_DURACAO = 0.20;
	private static final double PESO_POPULARIDADE = 0.15;
	private static final double PESO_AFINIDADE = 0.15;

	public double calcularScore(Filme filme, PerfilCinefilo perfil) {
		double scoreGenero = calcularScoreGenero(filme, perfil);
		double scoreDuracao = calcularScoreDuracao(filme.getDuracao(), perfil.getDuracaoMinima(),
				perfil.getDuracaoMaxima());
		double scorePopularidade = filme.getPopularidade();
		double scoreAfinidade = 50.0; // Bônus base fixo para o escopo atual

		double scoreFinal = (scoreGenero * PESO_GENERO) + (scoreDuracao * PESO_DURACAO)
				+ (scorePopularidade * PESO_POPULARIDADE) + (scoreAfinidade * PESO_AFINIDADE);

		return Math.min(100.0, Math.max(0.0, scoreFinal));
	}

	private double calcularScoreGenero(Filme filme, PerfilCinefilo perfil) {
		double somaPesos = 0;
		for (Genero genero : filme.getGeneros()) {
			somaPesos += perfil.getPesosGeneros().getOrDefault(genero, 0.0);
		}
		return filme.getGeneros().isEmpty() ? 0 : (somaPesos / filme.getGeneros().size()) * 100;
	}

	private double calcularScoreDuracao(int duracao, int minima, int maxima) {
		if (duracao >= minima && duracao <= maxima) {
			return 100.0;
		}
		int diferenca = duracao < minima ? minima - duracao : duracao - maxima;
		return Math.max(0.0, 100.0 - diferenca);
	}
}
