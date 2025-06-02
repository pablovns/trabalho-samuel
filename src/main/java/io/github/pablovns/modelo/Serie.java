package io.github.pablovns.modelo;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe que representa uma série de TV.
 */
public class Serie {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String nome;

    @SerializedName("language")
    private String idioma;

    @SerializedName("genres")
    private List<String> generos;

    @SerializedName("rating")
    private Rating nota;

    @SerializedName("status")
    private String estado;

    @SerializedName("premiered")
    private String dataEstreiaStr;

    @SerializedName("ended")
    private String dataTerminoStr;

    @SerializedName("network")
    private Network emissora;

    @SerializedName("summary")
    private String resumo;

    @SerializedName("image")
    private Image imagem;

    // Classes internas para mapeamento do JSON
    public static class Rating {
        @SerializedName("average")
        private Double average;

        public Double getAverage() {
            return average != null ? average : 0.0;
        }
    }

    public static class Network {
        @SerializedName("name")
        private String name;

        public String getName() {
            return name != null ? name : "Não informado";
        }
    }

    public static class Image {
        @SerializedName("medium")
        private String medium;

        public String getMedium() {
            return medium != null ? medium : "";
        }
    }

    // Construtor para criação manual de séries
    public Serie(int id, String nome, String idioma, List<String> generos, double nota,
                String estado, LocalDate dataEstreia, LocalDate dataTermino,
                String emissora, String resumo, String urlImagem) {
        this.id = id;
        this.nome = nome;
        this.idioma = idioma;
        this.generos = generos;
        this.nota = new Rating();
        this.nota.average = nota;
        this.estado = estado;
        this.dataEstreiaStr = dataEstreia != null ? dataEstreia.toString() : null;
        this.dataTerminoStr = dataTermino != null ? dataTermino.toString() : null;
        this.emissora = new Network();
        this.emissora.name = emissora;
        this.resumo = resumo;
        this.imagem = new Image();
        this.imagem.medium = urlImagem;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome != null ? nome : "Sem nome";
    }

    public String getIdioma() {
        return idioma != null ? idioma : "Não informado";
    }

    public List<String> getGeneros() {
        return generos;
    }

    public double getNota() {
        return nota != null ? nota.getAverage() : 0.0;
    }

    public String getEstado() {
        return estado != null ? estado : "Não informado";
    }

    public LocalDate getDataEstreia() {
        try {
            return dataEstreiaStr != null ? LocalDate.parse(dataEstreiaStr) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public LocalDate getDataTermino() {
        try {
            return dataTerminoStr != null ? LocalDate.parse(dataTerminoStr) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public String getEmissora() {
        return emissora != null ? emissora.getName() : "Não informado";
    }

    public String getResumo() {
        if (resumo == null) {
            return "Sem resumo disponível";
        }
        return resumo.replaceAll("<[^>]*>", "");
    }

    public String getUrlImagem() {
        return imagem != null ? imagem.getMedium() : "";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Serie serie = (Serie) obj;
        return id == serie.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return nome;
    }
} 