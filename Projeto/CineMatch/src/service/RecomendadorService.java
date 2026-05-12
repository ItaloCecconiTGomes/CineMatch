package service;

import model.Filme;
import model.PerfilCinefilo;
import model.Recomendacao;
import model.Usuario;
import model.enums.Genero;
import model.enums.Idioma;
import util.GeradorAleatorio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RecomendadorService {

	private final CatalogoFilmesAPI catalogo;
	private final HistoricoUsuarioRepository historico;
	private final NotificadorPush notificador;
	private final GeradorAleatorio gerador;
	private final CalculadoraScore calculadora;
	private final FiltroFilmes filtro;

	public RecomendadorService(CatalogoFilmesAPI catalogo, HistoricoUsuarioRepository historico,
			NotificadorPush notificador, GeradorAleatorio gerador, CalculadoraScore calculadora, FiltroFilmes filtro) {
		this.catalogo = catalogo;
		this.historico = historico;
		this.notificador = notificador;
		this.gerador = gerador;
		this.calculadora = calculadora;
		this.filtro = filtro;
	}

	public List<Recomendacao> recomendar(Usuario usuario, int topN) {
		List<Recomendacao> topRecomendacoes = gerarRecomendacoesBase(usuario.getPerfil(), topN,
				"Recomendado com base no seu perfil!");

		if (!topRecomendacoes.isEmpty()) {
			historico.registrarRecomendacao(usuario, topRecomendacoes);
			if (usuario.isNotificacoesLigadas()) {
				notificador.enviar(usuario, "Sua lista do AgileMovies chegou!");
			}
		}
		return topRecomendacoes;
	}

	private List<Recomendacao> gerarRecomendacoesBase(PerfilCinefilo perfil, int topN, String justificativa) {
		List<Filme> catalogoCompleto;
		try {
			catalogoCompleto = catalogo.buscarTodos();
		} catch (Exception e) {
			return Collections.emptyList();
		}
		List<Filme> filmesValidos = filtro.filtrar(catalogoCompleto, perfil);
		List<Recomendacao> recomendacoes = new ArrayList<>();

		for (Filme filme : filmesValidos) {
			double score = calculadora.calcularScore(filme, perfil);
			recomendacoes.add(new Recomendacao(filme, score, "Recomendado com base no seu perfil!"));
		}
		recomendacoes.sort(Comparator.comparing(Recomendacao::getScore).reversed()
				.thenComparing(r -> r.getFilme().getPopularidade(), Comparator.reverseOrder())
				.thenComparing(r -> gerador.sortearInteiro(0, 100)));

		return recomendacoes.stream().limit(topN).collect(Collectors.toList());
	}

	public List<Recomendacao> recomendarParaDupla(Usuario usuario1, Usuario usuario2, int topN) {
		PerfilCinefilo perfilDupla = mesclarPerfis(usuario1.getPerfil(), usuario2.getPerfil());
		List<Recomendacao> topRecomendacoes = gerarRecomendacoesBase(perfilDupla, topN,
				"Recomendado com base no perfil da dupla");

		if (!topRecomendacoes.isEmpty()) {
			historico.registrarRecomendacao(usuario1, topRecomendacoes);
			historico.registrarRecomendacao(usuario2, topRecomendacoes);

			if (usuario1.isNotificacoesLigadas()) {
				notificador.enviar(usuario1, "Sua lista do AgileMovies para dupla chegou!");
			}
			if (usuario2.isNotificacoesLigadas()) {
				notificador.enviar(usuario2, "Sua lista do AgileMovies para dupla chegou!");
			}
		}
		return topRecomendacoes;
	}

	private PerfilCinefilo mesclarPerfis(PerfilCinefilo perfil1, PerfilCinefilo perfil2) {
		PerfilCinefilo perfilDupla = new PerfilCinefilo();

		for (Genero genero : Genero.values()) {
			double peso1 = perfil1.getPesosGeneros().getOrDefault(genero, 0.0);
			double peso2 = perfil2.getPesosGeneros().getOrDefault(genero, 0.0);
			double mediaPeso = (peso1 + peso2) / 2.0;
			if (mediaPeso > 0.0) {
				perfilDupla.setPeso(genero, mediaPeso);
			}
		}

		List<Idioma> idiomasComuns = perfil1.getIdiomasAceitos().stream().filter(perfil2.getIdiomasAceitos()::contains)
				.collect(Collectors.toList());
		perfilDupla.getIdiomasAceitos().addAll(idiomasComuns);

		int idadeMax1 = perfil1.getClassificacaoMaxima() != null ? perfil1.getClassificacaoMaxima().getIdadeMaxima()
				: 10;
		int idadeMax2 = perfil2.getClassificacaoMaxima() != null ? perfil2.getClassificacaoMaxima().getIdadeMaxima()
				: 10;
		perfilDupla.setClassificacaoMaxima(
				idadeMax1 < idadeMax2 ? perfil1.getClassificacaoMaxima() : perfil2.getClassificacaoMaxima());

		perfilDupla.getHistoricoAssistidos().addAll(perfil1.getHistoricoAssistidos());
		perfilDupla.getHistoricoAssistidos().addAll(perfil2.getHistoricoAssistidos());

		int minDuracao = Math.max(perfil1.getDuracaoMinima(), perfil2.getDuracaoMinima());
		int maxDuracao = Math.min(perfil1.getDuracaoMaxima(), perfil2.getDuracaoMaxima());

		if (minDuracao <= maxDuracao) {
			perfilDupla.setFaixaDuracao(minDuracao, maxDuracao);
		} else {
			perfilDupla.setFaixaDuracao(Math.min(perfil1.getDuracaoMinima(), perfil2.getDuracaoMinima()),
					Math.max(perfil1.getDuracaoMaxima(), perfil2.getDuracaoMaxima()));
		}
		return perfilDupla;
	}
}