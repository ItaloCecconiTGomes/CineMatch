package testes;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import model.Filme;
import model.enums.ClassificacaoEtaria;
import model.enums.Genero;
import model.enums.Idioma;

public class FilmeTest {
	
	@Test
	@DisplayName("Deve criar filme com todos os atributos preenchidos corretamente")
	void deve_CriarFilmeComTodosOsAtributos_Quando_DadosSaoValidos() {
		Filme filme = new Filme("F01", "Duna", 2024, 155, List.of(Genero.FICCAO_CIENTIFICA),
				ClassificacaoEtaria.QUATORZE, Idioma.EN, 90);
		assertAll(() -> assertEquals("F01", filme.getId()), () -> assertEquals("Duna", filme.getTitulo()),
				() -> assertEquals(155, filme.getDuracao()), () -> assertEquals(Idioma.EN, filme.getIdioma()),
				() -> assertEquals(90, filme.getPopularidade()));
	}

	@Test
	@DisplayName("Dois filmes com mesmo ID devem ser considerados iguais")
	void deve_ConsiderarFilmesIguais_Quando_PossuemMesmoId() {
		Filme f1 = new Filme("ID_IGUAL", "Filme A", 2020, 100, List.of(Genero.DRAMA), ClassificacaoEtaria.LIVRE,
				Idioma.PT, 50);
		Filme f2 = new Filme("ID_IGUAL", "Filme B", 2021, 120, List.of(Genero.ACAO), ClassificacaoEtaria.DEZOITO,
				Idioma.EN, 80);

		assertEquals(f1, f2);
		assertEquals(f1.hashCode(), f2.hashCode());
	}
}
