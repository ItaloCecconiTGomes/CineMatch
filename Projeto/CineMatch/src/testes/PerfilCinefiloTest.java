package testes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exception.PesoInvalidoException;
import model.PerfilCinefilo;
import model.enums.Genero;

public class PerfilCinefiloTest {

	private PerfilCinefilo perfil;

	@BeforeEach
	void setUp() {
		perfil = new PerfilCinefilo();
	}

	@Test
	void deve_LancarExcecao_Quando_PesoInferiorAZero() {
		Genero generoEscolhido = Genero.COMEDIA;
		double pesoInvalido = -0.1;

		assertThrows(PesoInvalidoException.class, () -> {
			perfil.setPeso(generoEscolhido, pesoInvalido);
		}, "Não é permitido atribuições de pesos negativos");
	}

	@Test
	void deve_AdicionarFilmeAoHistorico_Quando_MarcadoComoAssistido() {
		String idFilmeAssistido = "F02";

		perfil.getHistoricoAssistidos().add(idFilmeAssistido);
		assertTrue(perfil.getHistoricoAssistidos().contains(idFilmeAssistido),
				"O histórico deve conter o ID do filme que acabou de ser assistido.");

		assertEquals(1, perfil.getHistoricoAssistidos().size(),
				"O tamanho do histórico de assistidos deve ser exatamente 1 após a inserção.");

	}
}
