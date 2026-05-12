package testes;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.Filme;
import model.PerfilCinefilo;
import model.enums.ClassificacaoEtaria;
import model.enums.Genero;
import model.enums.Idioma;
import service.CalculadoraScore;

public class CalculadoraScoreTest {

	private CalculadoraScore calculadora;
	private PerfilCinefilo perfil;

	@BeforeEach
	void setUp() {
		calculadora = new CalculadoraScore();
		perfil = new PerfilCinefilo();
	}

	@Test
	@DisplayName("Deve calcular o score máximo para o componente de gênero quando o perfil tem peso 1.0 nos gêneros do filme")
	void deve_RetornarScoreMaximoDeGenero_Quando_AfinidadeTotal() {
		perfil.setPeso(Genero.ACAO, 1.0);
		perfil.setFaixaDuracao(100, 180);

		Filme filme = new Filme("F01", "O Lobo de Wall Street", 2013, 180, List.of(Genero.COMEDIA, Genero.DRAMA),
				ClassificacaoEtaria.DEZOITO, Idioma.PT, 100);
		double scoreFinal = calculadora.calcularScore(filme, perfil);

		assertEquals(42.5, scoreFinal, 0.01, "Score final deve ser 100% do peso de gênero");
	}

	@Test
	@DisplayName("Deve reduzir score proporcionalmente quando duração está fora da faixa")
	void deve_ReduzirScore_Quando_DuracaoForaDaFaixa() {
		perfil.setFaixaDuracao(90, 120);
		perfil.setPeso(Genero.ACAO, 1.0);
		Filme filmeLonguinho = new Filme("F1", "Ação 1", 2020, 130, List.of(Genero.ACAO), ClassificacaoEtaria.LIVRE,
				Idioma.PT, 80);

		double score = calculadora.calcularScore(filmeLonguinho, perfil);
		assertEquals(87.5, score, 0.1);
	}

	@Test
	@DisplayName("O score nunca deve ser superior a 100")
	void deve_LimitarScoreEmCem_Quando_CalculoUltrapassar() {
		perfil.setPeso(Genero.FICCAO_CIENTIFICA, 1.0);
		perfil.setFaixaDuracao(100, 150);
		Filme filmePerfeito = new Filme("F1", "Perfeito", 2024, 120, List.of(Genero.FICCAO_CIENTIFICA),
				ClassificacaoEtaria.LIVRE, Idioma.PT, 100);

		double score = calculadora.calcularScore(filmePerfeito, perfil);
		assertTrue(score <= 100.0);
	}

	@Test
	@DisplayName("Deve retornar score baixo quando não há compatibilidade de gênero")
	void deve_RetornarScoreBaixo_Quando_NaoHaCompatibilidade() {
		perfil.setPeso(Genero.ROMANCE, 1.0);
		Filme filmeAcao = new Filme("F1", "Ação", 2024, 120, List.of(Genero.ACAO), ClassificacaoEtaria.LIVRE, Idioma.PT,
				10);

		double score = calculadora.calcularScore(filmeAcao, perfil);
		assertTrue(score < 40.0);
	}
}
