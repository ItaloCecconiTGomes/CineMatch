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

    @Mock private CatalogoFilmesAPI catalogo;
    @Mock private HistoricoUsuarioRepository historico;
    @Mock private NotificadorPush notificador;
    @Mock private GeradorAleatorio gerador;

    @Spy private CalculadoraScore calculadora = new CalculadoraScore();
    @Spy private FiltroFilmes filtro = new FiltroFilmes();

    @InjectMocks private RecomendadorService service;

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
        
        filmeMock = new Filme("F01", "Matrix", 1999, 136, List.of(Genero.FICCAO_CIENTIFICA), ClassificacaoEtaria.QUATORZE, Idioma.PT, 95);
    }

    @Test
    void deve_RetornarListaVazia_Quando_CatalogoLancarExcecao() {
        when(catalogo.buscarTodos()).thenThrow(new RuntimeException("API Offline"));

        var resultado = service.recomendar(usuario, 5);
        assertTrue(resultado.isEmpty(), "A lista deve ser vazia em caso de falha da API.");
        verify(historico, never()).registrarRecomendacao(any(), any());
    }

    @Test
    void deve_NaoEnviarNotificacao_Quando_PushEstiverDesligado() {
        usuario.setNotificacoesLigadas(false);
        when(catalogo.buscarTodos()).thenReturn(List.of(filmeMock));
        when(gerador.sortearInteiro(anyInt(), anyInt())).thenReturn(50);

        service.recomendar(usuario, 5);
        verify(notificador, never()).enviar(eq(usuario), anyString());
    }

    @Test
    void deve_RegistrarNoHistorico_Quando_RecomendacaoGeradaComSucesso() {
        usuario.setNotificacoesLigadas(true);
        when(catalogo.buscarTodos()).thenReturn(List.of(filmeMock));
        when(gerador.sortearInteiro(anyInt(), anyInt())).thenReturn(50);

        service.recomendar(usuario, 5);
        verify(historico, times(1)).registrarRecomendacao(eq(usuario), anyList());
    }
    
    @Test
    @DisplayName("RS-09: Deve salvar no histórico exatamente o limite Top N solicitado pelo usuário")
    void deve_SalvarApenasTopNNoHistorico_Quando_CapturarArgumentos() {

        Filme filme1 = new Filme("F1", "Matrix", 1999, 136, List.of(Genero.FICCAO_CIENTIFICA), ClassificacaoEtaria.QUATORZE, Idioma.PT, 95);
        Filme filme2 = new Filme("F2", "Duna", 2021, 155, List.of(Genero.FICCAO_CIENTIFICA), ClassificacaoEtaria.QUATORZE, Idioma.PT, 90);
        Filme filme3 = new Filme("F3", "A Chegada", 2016, 116, List.of(Genero.FICCAO_CIENTIFICA), ClassificacaoEtaria.DOZE, Idioma.PT, 85);
        
        when(catalogo.buscarTodos()).thenReturn(List.of(filme1, filme2, filme3));
        usuario.setNotificacoesLigadas(false);

        ArgumentCaptor<List<Recomendacao>> captorDeLista = ArgumentCaptor.forClass(List.class);

        service.recomendar(usuario, 2);

        verify(historico).registrarRecomendacao(eq(usuario), captorDeLista.capture());

        List<Recomendacao> listaQueIaParaOBanco = captorDeLista.getValue();

        assertEquals(2, listaQueIaParaOBanco.size(), "O sistema só pode mandar salvar 2 filmes no histórico.");
        assertEquals("Matrix", listaQueIaParaOBanco.get(0).getFilme().getTitulo(), "Matrix deve ser o primeiro pelo score de popularidade.");
        assertEquals("Duna", listaQueIaParaOBanco.get(1).getFilme().getTitulo(), "Duna deve ser o segundo.");
    }
    
}