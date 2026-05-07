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
    
}