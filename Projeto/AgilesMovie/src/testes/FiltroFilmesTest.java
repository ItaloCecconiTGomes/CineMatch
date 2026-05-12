package testes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import model.Filme;
import model.PerfilCinefilo;
import model.enums.ClassificacaoEtaria;
import model.enums.Genero;
import model.enums.Idioma;
import service.FiltroFilmes;

public class FiltroFilmesTest {
	private FiltroFilmes filtro;
	private PerfilCinefilo perfil;

	@BeforeEach
	void setup() {
		filtro = new FiltroFilmes();
		perfil = new PerfilCinefilo();
		perfil.setClassificacaoMaxima(ClassificacaoEtaria.DOZE);
		perfil.getIdiomasAceitos().add(Idioma.PT);
		perfil.setPeso(Genero.ACAO, 1.0);
	}

	@Test
	@DisplayName("Deve remover filme que o usuário já assistiu")
	void deve_RemoverFilme_Quando_JaFoiAssistido() {
		Filme f1 = new Filme("F01", "Matrix", 1999, 136, List.of(Genero.ACAO), ClassificacaoEtaria.LIVRE, Idioma.PT,
				90);
		perfil.getHistoricoAssistidos().add("F01");

		List<Filme> resultado = filtro.filtrar(List.of(f1), perfil);
		assertTrue(resultado.isEmpty());
	}

	@Test
	@DisplayName("Deve remover filme com classificação acima do permitido")
	void deve_RemoverFilme_Quando_ClassificacaoSuperior() {
		Filme f1 = new Filme("F01", "Deadpool", 2016, 108, List.of(Genero.ACAO), ClassificacaoEtaria.DEZOITO, Idioma.PT,
				90);

		List<Filme> resultado = filtro.filtrar(List.of(f1), perfil);
		assertFalse(resultado.contains(f1));
	}

	@Test
	@DisplayName("Deve remover filme em idioma não aceito pelo perfil")
	void deve_RemoverFilme_Quando_IdiomaNaoEhAceito() {
		Filme f1 = new Filme("F01", "The Movie", 2023, 120, List.of(Genero.ACAO), ClassificacaoEtaria.LIVRE, Idioma.EN,
				90);
		List<Filme> resultado = filtro.filtrar(List.of(f1), perfil);
		assertFalse(resultado.contains(f1));
	}

	@Test
	@DisplayName("Deve remover filme quando o único gênero do filme tem peso 0.0 no perfil")
	void deve_RemoverFilme_Quando_GeneroTemPesoZero() {
		perfil.setPeso(Genero.TERROR, 0.0);
		Filme f1 = new Filme("F01", "O Exorcista", 1973, 122, List.of(Genero.TERROR), ClassificacaoEtaria.DEZOITO,
				Idioma.PT, 80);

		List<Filme> resultado = filtro.filtrar(List.of(f1), perfil);
		assertTrue(resultado.isEmpty());
	}
}
