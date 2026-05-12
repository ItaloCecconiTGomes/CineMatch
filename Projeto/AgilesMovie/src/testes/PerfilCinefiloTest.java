package testes;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import exception.DuracaoInvalidaException;
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
	@DisplayName("Deve lançar PesoInvalidoException quando o peso do gênero for menor que 0.0")
	void deve_LancarExcecao_Quando_PesoInferiorAZero() {
		Genero generoEscolhido = Genero.COMEDIA;
		double pesoInvalido = -0.1;

		assertThrows(PesoInvalidoException.class, () -> {
			perfil.setPeso(generoEscolhido, pesoInvalido);
		}, "Não é permitido atribuições de pesos negativos");
	}

	@Test
	@DisplayName("Deve garantir que o ID do filme seja adicionado corretamente ao histórico de assistidos")
	void deve_AdicionarFilmeAoHistorico_Quando_MarcadoComoAssistido() {
		String idFilmeAssistido = "F02";

		perfil.getHistoricoAssistidos().add(idFilmeAssistido);
		assertTrue(perfil.getHistoricoAssistidos().contains(idFilmeAssistido),
				"O histórico deve conter o ID do filme que acabou de ser assistido.");

		assertEquals(1, perfil.getHistoricoAssistidos().size(),
				"O tamanho do histórico de assistidos deve ser exatamente 1 após a inserção.");

	}
	
	@Test
    @DisplayName("Deve lançar exceção quando o peso for maior que 1.0")
    void deve_LancarExcecao_Quando_PesoMaiorQueUm() {
        assertThrows(PesoInvalidoException.class, () -> perfil.setPeso(Genero.ACAO, 1.1));
    }

    @Test
    @DisplayName("Deve lançar exceção quando duração mínima for maior que a máxima")
    void deve_LancarExcecao_Quando_DuracaoMinimaMaiorQueMaxima() {
        assertThrows(DuracaoInvalidaException.class, () -> perfil.setFaixaDuracao(150, 100));
    }

    @Test
    @DisplayName("Deve lançar exceção para nota fora do intervalo [1,5]")
    void deve_LancarExcecao_Quando_NotaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> perfil.adicionarNota("F01", 6));
    }
}
