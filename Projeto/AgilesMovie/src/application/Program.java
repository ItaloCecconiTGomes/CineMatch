package application;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

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

public class Program {

	public static void main(String[] args) {
		CalculadoraScore calculadora = new CalculadoraScore();
		FiltroFilmes filtro = new FiltroFilmes();

		CatalogoFilmesAPI catalogoAPI = () -> Arrays.asList(
				new Filme("F01", "Duna: Parte Dois", 2024, 166, List.of(Genero.FICCAO_CIENTIFICA, Genero.DRAMA),
						ClassificacaoEtaria.QUATORZE, Idioma.EN, 92),
				new Filme("F02", "Ela (Her)", 2013, 126,
						List.of(Genero.FICCAO_CIENTIFICA, Genero.DRAMA, Genero.ROMANCE), ClassificacaoEtaria.DEZESSEIS,
						Idioma.EN, 78),
				new Filme("F03", "O Iluminado", 1980, 146, List.of(Genero.TERROR), ClassificacaoEtaria.DEZOITO,
						Idioma.EN, 88),
				new Filme("F04", "Interestelar", 2014, 169, List.of(Genero.FICCAO_CIENTIFICA, Genero.DRAMA),
						ClassificacaoEtaria.DOZE, Idioma.EN, 95),
				new Filme("F05", "Tropa de Elite", 2007, 115, List.of(Genero.ACAO, Genero.DRAMA),
						ClassificacaoEtaria.DEZOITO, Idioma.PT, 80),
				new Filme("F06", "Click", 2006, 107, List.of(Genero.COMEDIA, Genero.DRAMA), ClassificacaoEtaria.DOZE,
						Idioma.EN, 65),
				new Filme("F07", "A Chegada", 2016, 116, List.of(Genero.FICCAO_CIENTIFICA, Genero.DRAMA),
						ClassificacaoEtaria.DOZE, Idioma.EN, 84),
				new Filme("F08", "Top Gun: Maverick", 2022, 130, List.of(Genero.ACAO), ClassificacaoEtaria.DOZE,
						Idioma.PT, 94),
				new Filme("F09", "Vingadores", 2012, 143, List.of(Genero.ACAO, Genero.FICCAO_CIENTIFICA),
						ClassificacaoEtaria.DOZE, Idioma.PT, 99),
				new Filme("F10", "Questão de Tempo", 2013, 123, List.of(Genero.ROMANCE, Genero.DRAMA),
						ClassificacaoEtaria.DOZE, Idioma.PT, 87));

		HistoricoUsuarioRepository repository = (usuario, recomendacoes) -> System.out
				.println("[DATABASE] Sucesso: Histórico registrado para o usuário: " + usuario.getNome());

		NotificadorPush notificador = (usuario, mensagem) -> System.out
				.println("[PUSH NOTIFICATION] Olá " + usuario.getNome() + "! " + mensagem);

		GeradorAleatorio gerador = (min, max) -> new Random().nextInt((max - min) + 1) + min;

		RecomendadorService service = new RecomendadorService(catalogoAPI, repository, notificador, gerador,
				calculadora, filtro);

		PerfilCinefilo perfilMaria = new PerfilCinefilo();
		perfilMaria.setPeso(Genero.FICCAO_CIENTIFICA, 0.9);
		perfilMaria.setPeso(Genero.DRAMA, 0.6);
		perfilMaria.setPeso(Genero.COMEDIA, 0.5);
		perfilMaria.setPeso(Genero.TERROR, 0.0);
		perfilMaria.setFaixaDuracao(90, 150);
		perfilMaria.setClassificacaoMaxima(ClassificacaoEtaria.DEZESSEIS);
		perfilMaria.getIdiomasAceitos().addAll(List.of(Idioma.PT, Idioma.EN));
		perfilMaria.getHistoricoAssistidos().add("F04");

		Usuario maria = new Usuario("Maria", 28, perfilMaria);
		maria.setNotificacoesLigadas(true);

		System.out.println("=== 1. RECOMENDAÇÃO INDIVIDUAL PARA " + maria.getNome().toUpperCase() + " ===");
		List<Recomendacao> recsMaria = service.recomendar(maria, 3);
		exibirRecomendacoes(recsMaria);

		System.out.println("\n" + "=".repeat(50) + "\n");

		PerfilCinefilo perfilM = new PerfilCinefilo();
		perfilM.setPeso(Genero.ACAO, 1.0);
		perfilM.setPeso(Genero.FICCAO_CIENTIFICA, 0.8);
		perfilM.setFaixaDuracao(100, 180);
		perfilM.setClassificacaoMaxima(ClassificacaoEtaria.DEZOITO);
		perfilM.getIdiomasAceitos().addAll(List.of(Idioma.PT, Idioma.EN));
		Usuario mauricio = new Usuario("Maurício", 25, perfilM);
		mauricio.setNotificacoesLigadas(true);

		PerfilCinefilo perfilA = new PerfilCinefilo();
		perfilA.setPeso(Genero.DRAMA, 1.0);
		perfilA.setPeso(Genero.ROMANCE, 0.9);
		perfilA.setFaixaDuracao(90, 140);
		perfilA.setClassificacaoMaxima(ClassificacaoEtaria.QUATORZE);
		perfilA.getIdiomasAceitos().add(Idioma.PT);
		Usuario ana = new Usuario("Ana", 22, perfilA);
		ana.setNotificacoesLigadas(true);

		System.out.println("=== 2. RECOMENDAÇÃO PARA DUPLA: " + mauricio.getNome().toUpperCase() + " & "
				+ ana.getNome().toUpperCase() + " ===");
		List<Recomendacao> recsDupla = service.recomendarParaDupla(mauricio, ana, 3);
		exibirRecomendacoes(recsDupla);
	}

	private static void exibirRecomendacoes(List<Recomendacao> lista) {
		if (lista.isEmpty()) {
			System.out.println("Nenhum filme encontrado para os critérios estabelecidos.");
		} else {
			System.out.println("\n-=-=- RECOMENDAÇÃO AGILE MOVIES -=-=-");
			for (int i = 0; i < lista.size(); i++) {
				Recomendacao r = lista.get(i);
				System.out.printf("%dº %s | Score: %.2f | Justificativa: %s%n", (i + 1), r.getFilme().getTitulo(),
						r.getScore(), r.getJustificativa());
			}
		}
	}
}
