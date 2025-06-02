package io.github.pablovns.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe que representa um usuário do sistema.
 */
public class Usuario {
    @SerializedName("nome")
    private String nome;

    @SerializedName("series_favoritas")
    private List<Serie> seriesFavoritas;

    @SerializedName("series_assistidas")
    private List<Serie> seriesAssistidas;

    @SerializedName("series_para_assistir")
    private List<Serie> seriesParaAssistir;

    public Usuario(String nome) {
        this.nome = nome;
        this.seriesFavoritas = new ArrayList<>();
        this.seriesAssistidas = new ArrayList<>();
        this.seriesParaAssistir = new ArrayList<>();
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public List<Serie> getSeriesFavoritas() {
        return seriesFavoritas;
    }

    public List<Serie> getSeriesAssistidas() {
        return seriesAssistidas;
    }

    public List<Serie> getSeriesParaAssistir() {
        return seriesParaAssistir;
    }

    // Métodos para gerenciar séries favoritas
    public void adicionarSerieFavorita(Serie serie) {
        if (!seriesFavoritas.contains(serie)) {
            seriesFavoritas.add(serie);
        }
    }

    public void removerSerieFavorita(Serie serie) {
        seriesFavoritas.remove(serie);
    }

    // Métodos para gerenciar séries assistidas
    public void adicionarSerieAssistida(Serie serie) {
        if (!seriesAssistidas.contains(serie)) {
            seriesAssistidas.add(serie);
        }
    }

    public void removerSerieAssistida(Serie serie) {
        seriesAssistidas.remove(serie);
    }

    // Métodos para gerenciar séries para assistir
    public void adicionarSerieParaAssistir(Serie serie) {
        if (!seriesParaAssistir.contains(serie)) {
            seriesParaAssistir.add(serie);
        }
    }

    public void removerSerieParaAssistir(Serie serie) {
        seriesParaAssistir.remove(serie);
    }
} 