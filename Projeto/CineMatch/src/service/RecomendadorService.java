package service;

import model.Filme;
import model.Recomendacao;
import model.Usuario;
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
		List<Filme> catalogoCompleto;
		try {
			catalogoCompleto = catalogo.buscarTodos();
		} catch (Exception e) {
			return Collections.emptyList(); // Resiliência: não derruba o app
		}

		List<Filme> filmesValidos = filtro.filtrar(catalogoCompleto, usuario.getPerfil());

		List<Recomendacao> recomendacoes = new ArrayList<>();
		for (Filme filme : filmesValidos) {
			double score = calculadora.calcularScore(filme, usuario.getPerfil());
			recomendacoes.add(new Recomendacao(filme, score, "Recomendado com base no seu perfil!"));
		}

		recomendacoes.sort(Comparator.comparing(Recomendacao::getScore).reversed()
				.thenComparing(r -> r.getFilme().getPopularidade(), Comparator.reverseOrder())
				.thenComparing(r -> gerador.sortearInteiro(0, 100)));

		List<Recomendacao> topRecomendacoes = recomendacoes.stream().limit(topN).collect(Collectors.toList());

		if (!topRecomendacoes.isEmpty()) {
			historico.registrarRecomendacao(usuario, topRecomendacoes);
			if (usuario.isNotificacoesLigadas()) {
				notificador.enviar(usuario, "Sua lista do CineMatch chegou!");
			}
		}

		return topRecomendacoes;
	}
}