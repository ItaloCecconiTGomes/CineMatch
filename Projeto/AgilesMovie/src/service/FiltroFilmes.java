package service;

import model.Filme;
import model.PerfilCinefilo;
import model.enums.Genero;

import java.util.List;
import java.util.stream.Collectors;

public class FiltroFilmes {

	public List<Filme> filtrar(List<Filme> catalogo, PerfilCinefilo perfil) {
		if (catalogo == null || catalogo.isEmpty())
			return List.of();

		return catalogo.stream().filter(filme -> !perfil.getHistoricoAssistidos().contains(filme.getId())).filter(
				filme -> filme.getClassificacao().getIdadeMaxima() <= perfil.getClassificacaoMaxima().getIdadeMaxima())
				.filter(filme -> perfil.getIdiomasAceitos().contains(filme.getIdioma()))
				.filter(filme -> temGeneroAceito(filme, perfil)).collect(Collectors.toList());
	}

	private boolean temGeneroAceito(Filme filme, PerfilCinefilo perfil) {
		for (Genero genero : filme.getGeneros()) {
			Double peso = perfil.getPesosGeneros().getOrDefault(genero, 0.0);
			if (peso > 0.0) {
				return true;
			}
		}
		return false;
	}
}