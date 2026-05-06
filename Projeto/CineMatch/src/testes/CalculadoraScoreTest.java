package testes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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
	void deve_RetornarScoreMaximoDeGenero_Quando_AfinidadeTotal() {
		perfil.setPeso(Genero.ACAO, 1.0);
		perfil.setFaixaDuracao(100, 180);

		Filme filme = new Filme("F01", "O Lobo de Wall Street", 2013, 180, List.of(Genero.COMEDIA, Genero.DRAMA),
				ClassificacaoEtaria.DEZOITO, Idioma.PT, 100);
		double scoreFinal = calculadora.calcularScore(filme, perfil);

		assertEquals(42.5, scoreFinal, 0.01, "Score final deve ser 100% do peso de gênero");
	}

}
