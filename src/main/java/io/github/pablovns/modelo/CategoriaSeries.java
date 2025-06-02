package io.github.pablovns.modelo;

public enum CategoriaSeries {
    FAVORITOS("Favoritos"),
    ASSISTIDAS("Assistidas"),
    PARA_ASSISTIR("Para assistir");

    private final String valor;

    CategoriaSeries(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
