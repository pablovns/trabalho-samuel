package io.github.pablovns.modelo;

import java.util.Comparator;
import java.util.function.Function;

public enum OpcaoOrdenacao {
    ALFABETICA(0, "Ordem Alfabética", Serie::getNome),
    NOTA(1, "Nota", Serie::getNota),
    ESTADO(2, "Estado", Serie::getEstado),
    DATA_ESTREIA(3, "Data de Estreia", Serie::getDataEstreia, true),
    DATA_TERMINO(4, "Data de Término", Serie::getDataTermino, true);

    private final int indice;
    private final String descricao;
    private final Comparator<Serie> comparador;

    <T extends Comparable<T>> OpcaoOrdenacao(int indice, String descricao, Function<Serie, T> extrator) {
        this(indice, descricao, Comparator.comparing(extrator));
    }

    <T extends Comparable<T>> OpcaoOrdenacao(int indice, String descricao, Function<Serie, T> extrator, boolean aceitaNulo) {
        this(indice, descricao, aceitaNulo ? 
            Comparator.comparing(extrator, Comparator.nullsLast(Comparator.naturalOrder())) :
            Comparator.comparing(extrator));
    }

    OpcaoOrdenacao(int indice, String descricao, Comparator<Serie> comparador) {
        this.indice = indice;
        this.descricao = descricao;
        this.comparador = comparador;
    }

    public int getIndice() {
        return indice;
    }

    public String getDescricao() {
        return descricao;
    }

    public Comparator<Serie> getComparador() {
        return comparador;
    }

    public static OpcaoOrdenacao fromIndice(int indice) {
        for (OpcaoOrdenacao opcao : values()) {
            if (opcao.indice == indice) {
                return opcao;
            }
        }
        throw new IllegalArgumentException("Índice de ordenação inválido: " + indice);
    }

    public static String[] getDescricoes() {
        return java.util.Arrays.stream(values())
            .map(OpcaoOrdenacao::getDescricao)
            .toArray(String[]::new);
    }
} 