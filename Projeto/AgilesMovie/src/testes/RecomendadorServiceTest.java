package testes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import model.Filme;
import model.PerfilCinefilo;
import model.Recomendacao;
import model.Usuario;
import model.enums.ClassificacaoEtaria;
import model.enums.Genero;
import model.enums.Idioma;
import service.CalculadoraScore;
import service.CatalogoFilmesAPI;
import service.FiltroFilmes;
import service.HistoricoUsuarioRepository;
import service.NotificadorPush;
import service.RecomendadorService;
import util.GeradorAleatorio;

@ExtendWith(MockitoExtension.class)
class RecomendadorServiceTest {

	@Mock
	private CatalogoFilmesAPI catalogo;
	@Mock
	private HistoricoUsuarioRepository historico;
	@Mock
	private NotificadorPush notificador;
	@Mock
	private GeradorAleatorio gerador;

	@Spy
	private CalculadoraScore calculadora = new CalculadoraScore();
	@Spy
	private FiltroFilmes filtro = new FiltroFilmes();

	@InjectMocks
	private RecomendadorService service;

	private Usuario usuario;
	private Filme filmeMock;

	@BeforeEach
	void setup() {
		PerfilCinefilo perfil = new PerfilCinefilo();
		perfil.setFaixaDuracao(90, 150);
		perfil.setClassificacaoMaxima(ClassificacaoEtaria.DEZOITO);
		perfil.getIdiomasAceitos().add(Idioma.PT);
		perfil.setPeso(Genero.FICCAO_CIENTIFICA, 1.0);

		usuario = new Usuario("João", 25, perfil);

		filmeMock = new Filme("F01", "Matrix", 1999, 136, List.of(Genero.FICCAO_CIENTIFICA),
				ClassificacaoEtaria.QUATORZE, Idioma.PT, 95);
	}

	@Test
	@DisplayName("Deve garantir resiliência retornando lista vazia e não registrando histórico quando a API do catálogo falhar")
	void deve_RetornarListaVazia_Quando_CatalogoLancarExcecao() {
		when(catalogo.buscarTodos()).thenThrow(new RuntimeException("API Offline"));

		var resultado = service.recomendar(usuario, 5);
		assertTrue(resultado.isEmpty(), "A lista deve ser vazia em caso de falha da API.");
		verify(historico, never()).registrarRecomendacao(any(), any());
	}

	@Test
	@DisplayName("Deve registrar recomendação e enviar push quando notificações estão ligadas")
	void deve_ChamarNotificadorERepositorio_Quando_RecomendarComSucesso() {
		usuario.setNotificacoesLigadas(true);
		when(catalogo.buscarTodos()).thenReturn(List.of(filmeMock));

		service.recomendar(usuario, 1);

		verify(historico, times(1)).registrarRecomendacao(eq(usuario), anyList());
		verify(notificador, times(1)).enviar(eq(usuario), anyString());
	}

	@Test
	@DisplayName("Deve capturar e validar os dados enviados ao repositório")
	void deve_ValidarDadosCapturados_NoRegistroDoHistorico() {
		when(catalogo.buscarTodos()).thenReturn(List.of(filmeMock));
		ArgumentCaptor<List<Recomendacao>> captor = ArgumentCaptor.forClass(List.class);

		service.recomendar(usuario, 1);

		verify(historico).registrarRecomendacao(eq(usuario), captor.capture());
		List<Recomendacao> gravadas = captor.getValue();

		assertEquals(1, gravadas.size());
		assertEquals("Matrix", gravadas.get(0).getFilme().getTitulo());
	}

	@Test
	@DisplayName("Deve respeitar o limite máximo de recomendações (Top N)")
	void deve_RespeitarLimiteTopN_Quando_Solicitado() {
		Filme f2 = new Filme("F02", "Matrix 2", 2003, 136, List.of(Genero.FICCAO_CIENTIFICA),
				ClassificacaoEtaria.QUATORZE, Idioma.PT, 90);
		when(catalogo.buscarTodos()).thenReturn(List.of(filmeMock, f2));

		List<Recomendacao> resultado = service.recomendar(usuario, 1);
		assertEquals(1, resultado.size());
	}

	@Test
	@DisplayName("Deve usar popularidade como critério de desempate para scores iguais")
	void deve_DesempatarPorPopularidade_Quando_ScoresForemIguais() {
		Filme f1 = new Filme("F1", "Filme A", 2020, 136, List.of(Genero.FICCAO_CIENTIFICA), ClassificacaoEtaria.LIVRE,
				Idioma.PT, 50);
		Filme f2 = new Filme("F2", "Filme B", 2020, 136, List.of(Genero.FICCAO_CIENTIFICA), ClassificacaoEtaria.LIVRE,
				Idioma.PT, 90);

		when(catalogo.buscarTodos()).thenReturn(List.of(f1, f2));

		List<Recomendacao> resultado = service.recomendar(usuario, 2);
		assertEquals("F2", resultado.get(0).getFilme().getId());
	}

	@Test
	@DisplayName("Não deve enviar notificação push se o usuário tiver notificações desligadas")
	void deve_NaoEnviarNotificacao_Quando_UsuarioDesabilitar() {
		usuario.setNotificacoesLigadas(false);
		when(catalogo.buscarTodos()).thenReturn(List.of(filmeMock));

		service.recomendar(usuario, 5);

		verify(notificador, never()).enviar(any(), anyString());
	}

}