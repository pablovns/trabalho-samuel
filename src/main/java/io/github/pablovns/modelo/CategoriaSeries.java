package io.github.pablovns.modelo;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum CategoriaSeries {
    BUSCA(0, "Busca", usuario -> null, null),
    FAVORITOS(1, "Favoritos", Usuario::getSeriesFavoritas, Usuario::adicionarSerieFavorita, "favoritas"),
    ASSISTIDAS(2, "Assistidas", Usuario::getSeriesAssistidas, Usuario::adicionarSerieAssistida, "assistidas"),
    PARA_ASSISTIR(3, "Para Assistir", Usuario::getSeriesParaAssistir, Usuario::adicionarSerieParaAssistir, "para assistir");

    private final int indice;
    private final String titulo;
    private final Function<Usuario, List<Serie>> getSeries;
    private final BiConsumer<Usuario, Serie> adicionarSerie;
    private final String descricaoLista;

    CategoriaSeries(int indice, String titulo, Function<Usuario, List<Serie>> getSeries, BiConsumer<Usuario, Serie> adicionarSerie) {
        this(indice, titulo, getSeries, adicionarSerie, null);
    }

    CategoriaSeries(int indice, String titulo, Function<Usuario, List<Serie>> getSeries, BiConsumer<Usuario, Serie> adicionarSerie, String descricaoLista) {
        this.indice = indice;
        this.titulo = titulo;
        this.getSeries = getSeries;
        this.adicionarSerie = adicionarSerie;
        this.descricaoLista = descricaoLista;
    }

    public int getIndice() {
        return indice;
    }

    public String getTitulo() {
        return titulo;
    }

    public List<Serie> getSeriesDaCategoria(Usuario usuario) {
        return getSeries.apply(usuario);
    }

    public void adicionarSerie(Usuario usuario, Serie serie) {
        if (this == BUSCA) {
            throw new UnsupportedOperationException("Não é possível adicionar séries à categoria de busca");
        }
        adicionarSerie.accept(usuario, serie);
    }

    public String getDescricaoLista() {
        return descricaoLista;
    }

    public static CategoriaSeries fromIndice(int indice) {
        for (CategoriaSeries categoria : values()) {
            if (categoria.indice == indice) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Índice de categoria inválido: " + indice);
    }
}
