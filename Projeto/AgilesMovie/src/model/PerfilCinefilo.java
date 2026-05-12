package model;

import exception.DuracaoInvalidaException;
import exception.PesoInvalidoException;
import model.enums.ClassificacaoEtaria;
import model.enums.Genero;
import model.enums.Idioma;

import java.util.*;

public class PerfilCinefilo {
	private Map<Genero, Double> pesosGeneros = new HashMap<>();
	private int duracaoMinima;
	private int duracaoMaxima;
	private ClassificacaoEtaria classificacaoMaxima;
	private List<Idioma> idiomasAceitos = new ArrayList<>();
	private Set<String> historicoAssistidos = new HashSet<>();
	private Map<String, Integer> notas = new HashMap<>();

	public void setPeso(Genero genero, double peso) {
		if (peso < 0.0 || peso > 1.0) {
			throw new PesoInvalidoException("O peso deve estar entre 0.0 e 1.0.");
		}
		this.pesosGeneros.put(genero, peso);
	}

	public void setFaixaDuracao(int minima, int maxima) {
		if (minima > maxima) {
			throw new DuracaoInvalidaException("Duração mínima não pode ser maior que a máxima.");
		}
		this.duracaoMinima = minima;
		this.duracaoMaxima = maxima;
	}

	public void adicionarNota(String filmeId, int nota) {
		if (nota < 1 || nota > 5) {
			throw new IllegalArgumentException("A nota deve ser entre 1 e 5.");
		}
		this.notas.put(filmeId, nota);
	}

	public Map<Genero, Double> getPesosGeneros() {
		return pesosGeneros;
	}

	public int getDuracaoMinima() {
		return duracaoMinima;
	}

	public int getDuracaoMaxima() {
		return duracaoMaxima;
	}

	public ClassificacaoEtaria getClassificacaoMaxima() {
		return classificacaoMaxima;
	}

	public void setClassificacaoMaxima(ClassificacaoEtaria classificacaoMaxima) {
		this.classificacaoMaxima = classificacaoMaxima;
	}

	public List<Idioma> getIdiomasAceitos() {
		return idiomasAceitos;
	}

	public Set<String> getHistoricoAssistidos() {
		return historicoAssistidos;
	}

	public Map<String, Integer> getNotas() {
		return notas;
	}
}