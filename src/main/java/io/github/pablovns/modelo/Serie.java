package io.github.pablovns.modelo;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe que representa uma série de TV.
 */
public class Serie {
    private int id;
    private String nome;
    private String idioma;
    private List<String> generos;
    private double nota;
    private String estado;
    private LocalDate dataEstreia;
    private LocalDate dataTermino;
    private String emissora;
    private String resumo;
    private String urlImagem;

    public Serie(int id, String nome, String idioma, List<String> generos, double nota, 
                String estado, LocalDate dataEstreia, LocalDate dataTermino, 
                String emissora, String resumo, String urlImagem) {
        this.id = id;
        this.nome = nome;
        this.idioma = idioma;
        this.generos = generos;
        this.nota = nota;
        this.estado = estado;
        this.dataEstreia = dataEstreia;
        this.dataTermino = dataTermino;
        this.emissora = emissora;
        this.resumo = resumo;
        this.urlImagem = urlImagem;
    }

    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getIdioma() { return idioma; }
    public List<String> getGeneros() { return generos; }
    public double getNota() { return nota; }
    public String getEstado() { return estado; }
    public LocalDate getDataEstreia() { return dataEstreia; }
    public LocalDate getDataTermino() { return dataTermino; }
    public String getEmissora() { return emissora; }
    public String getResumo() { return resumo; }
    public String getUrlImagem() { return urlImagem; }

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