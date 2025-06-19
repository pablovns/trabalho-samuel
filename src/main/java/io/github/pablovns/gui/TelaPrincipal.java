package io.github.pablovns.gui;

import io.github.pablovns.modelo.CategoriaSeries;
import io.github.pablovns.modelo.OpcaoOrdenacao;
import io.github.pablovns.modelo.Serie;
import io.github.pablovns.modelo.Usuario;
import io.github.pablovns.persistencia.GerenciadorPersistencia;
import io.github.pablovns.servico.ServicoTVMaze;
import io.github.pablovns.util.Constantes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TelaPrincipal extends JFrame {
    private final Usuario usuario;
    private final transient ServicoTVMaze servicoTVMaze;
    private final transient GerenciadorPersistencia gerenciadorPersistencia;
    
    private JTextField campoBusca;
    private JTable tabelaResultados;
    private DefaultTableModel modeloTabela;
    private JComboBox<String> comboOrdenacao;
    private JToggleButton botaoOrdemCrescente;
    private JTabbedPane abas;

    public TelaPrincipal(Usuario usuario) {
        this.usuario = usuario;
        this.servicoTVMaze = new ServicoTVMaze();
        this.gerenciadorPersistencia = new GerenciadorPersistencia();

        configurarJanela();
        inicializarComponentes();
        configurarEventos();
        carregarListas();
    }

    private void configurarJanela() {
        setTitle("Gerenciador de Séries - " + usuario.getNome());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        // Painel principal
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        
        // Painel de busca
        JPanel painelBusca = new JPanel(new FlowLayout());
        campoBusca = new JTextField(30);
        JButton botaoBuscar = new JButton("Buscar");
        painelBusca.add(new JLabel("Nome da série:"));
        painelBusca.add(campoBusca);
        painelBusca.add(botaoBuscar);

        // Configuração da tabela de resultados
        String[] colunas = {"Nome", "Idioma", "Gêneros", "Nota", "Estado", "Emissora", "Data de Estreia", "Data de Término"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaResultados = new JTable(modeloTabela);
        JScrollPane scrollTabela = new JScrollPane(tabelaResultados);

        // Painel de ações
        JPanel painelAcoes = new JPanel(new FlowLayout());
        JButton botaoFavoritar = new JButton("Adicionar aos Favoritos");
        JButton botaoAssistido = new JButton("Marcar como Assistido");
        JButton botaoParaAssistir = new JButton("Adicionar para Assistir");
        painelAcoes.add(botaoFavoritar);
        painelAcoes.add(botaoAssistido);
        painelAcoes.add(botaoParaAssistir);

        // Painel de ordenação
        JPanel painelOrdenacao = new JPanel(new FlowLayout());
        comboOrdenacao = new JComboBox<>(OpcaoOrdenacao.getDescricoes());
        botaoOrdemCrescente = new JToggleButton("↑");
        botaoOrdemCrescente.setToolTipText("Clique para alternar entre ordem crescente (↑) e decrescente (↓)");
        botaoOrdemCrescente.addActionListener(e -> {
            botaoOrdemCrescente.setText(botaoOrdemCrescente.isSelected() ? "↓" : "↑");
            ordenarLista();
        });
        
        painelOrdenacao.add(new JLabel("Ordenar por:"));
        painelOrdenacao.add(comboOrdenacao);
        painelOrdenacao.add(botaoOrdemCrescente);

        // Abas para as listas
        abas = new JTabbedPane();
        for (CategoriaSeries categoria : CategoriaSeries.values()) {
            if (categoria == CategoriaSeries.BUSCA) {
                abas.addTab(categoria.getTitulo(), scrollTabela);
            } else {
                abas.addTab(categoria.getTitulo(), 
                    criarPainelLista(categoria.getSeriesDaCategoria(usuario)));
            }
        }

        // Adiciona listener para ordenar ao trocar de aba
        abas.addChangeListener(e -> ordenarLista());

        // Montagem do layout
        painelPrincipal.add(painelBusca, BorderLayout.NORTH);
        JPanel painelCentral = new JPanel(new BorderLayout());
        painelCentral.add(painelOrdenacao, BorderLayout.NORTH);
        painelCentral.add(abas, BorderLayout.CENTER);
        painelCentral.add(painelAcoes, BorderLayout.SOUTH);
        painelPrincipal.add(painelCentral, BorderLayout.CENTER);

        add(painelPrincipal);

        // Configurar eventos dos botões
        botaoBuscar.addActionListener(e -> buscarSeries());
        botaoFavoritar.addActionListener(e -> adicionarSerieSelecionada(CategoriaSeries.FAVORITOS));
        botaoAssistido.addActionListener(e -> adicionarSerieSelecionada(CategoriaSeries.ASSISTIDAS));
        botaoParaAssistir.addActionListener(e -> adicionarSerieSelecionada(CategoriaSeries.PARA_ASSISTIR));
        comboOrdenacao.addActionListener(e -> ordenarLista());
    }

    private JPanel criarPainelLista(List<Serie> series) {
        DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Nome", "Idioma", "Gêneros", "Nota", "Estado", "Emissora", "Data de Estreia", "Data de Término"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabela = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabela);

        JPanel painel = new JPanel(new BorderLayout());
        painel.add(scroll, BorderLayout.CENTER);

        JPanel painelBotoes = gerarPainelComBotoes(series, tabela);
        painel.add(painelBotoes, BorderLayout.SOUTH);

        if (series != null && !series.isEmpty()) {
            preencherTabela(modelo, series);
        }
        return painel;
    }

    private JPanel gerarPainelComBotoes(List<Serie> series, JTable tabela) {
        JButton botaoRemover = new JButton("Remover");
        botaoRemover.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha != -1) {
                Serie serie = series.get(linha);
                if (abas.getSelectedIndex() == 1) {
                    usuario.removerSerieFavorita(serie);
                } else if (abas.getSelectedIndex() == 2) {
                    usuario.removerSerieAssistida(serie);
                } else if (abas.getSelectedIndex() == 3) {
                    usuario.removerSerieParaAssistir(serie);
                }
                carregarListas();
                salvarDados();
            }
        });

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.add(botaoRemover);
        return painelBotoes;
    }

    private void preencherTabela(DefaultTableModel modelo, List<Serie> series) {
        modelo.setRowCount(0);
        if (series == null || series.isEmpty()) {
            return;
        }

        for (Serie serie : series) {
            modelo.addRow(new Object[]{
                serie.getNome(),
                serie.getIdioma(),
                String.join(", ", serie.getGeneros()),
                serie.getNota(),
                serie.getEstado(),
                serie.getEmissora(),
                serie.getDataEstreia() != null ? serie.getDataEstreia().toString() : Constantes.NAO_INFORMADA,
                serie.getDataTermino() != null ? serie.getDataTermino().toString() : Constantes.NAO_INFORMADA
            });
        }
    }

    private void configurarEventos() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salvarDados();
            }
        });
    }

    private void buscarSeries() {
        String termo = campoBusca.getText().trim();
        if (termo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um termo para busca!");
            return;
        }

        try {
            List<Serie> series = servicoTVMaze.buscarSeries(termo);
            if (series.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Nenhuma série encontrada!",
                     "Falha ao buscar séries",
                    JOptionPane.WARNING_MESSAGE);
            }
            modeloTabela.setRowCount(0);
            for (Serie serie : series) {
                modeloTabela.addRow(new Object[]{
                    serie.getNome(),
                    serie.getIdioma(),
                    String.join(", ", serie.getGeneros()),
                    serie.getNota(),
                    serie.getEstado(),
                    serie.getEmissora(),
                    serie.getDataEstreia() != null ? serie.getDataEstreia().toString() : Constantes.NAO_INFORMADA,
                    serie.getDataTermino() != null ? serie.getDataTermino().toString() : Constantes.NAO_INFORMADA
                });
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            JOptionPane.showMessageDialog(this,
                    "Operação interrompida: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao buscar séries: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void adicionarSerieSelecionada(CategoriaSeries categoriaDestino) {
        CategoriaSeries categoriaAtual = CategoriaSeries.fromIndice(abas.getSelectedIndex());
        JTable tabelaAtual;
        
        if (categoriaAtual == CategoriaSeries.BUSCA) {
            tabelaAtual = tabelaResultados;
        } else {
            tabelaAtual = (JTable) ((JScrollPane) ((JPanel) abas.getSelectedComponent())
                .getComponent(0)).getViewport().getView();
        }
        
        int linha = tabelaAtual.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma série primeiro!");
            return;
        }

        try {
            Serie serie;
            if (categoriaAtual == CategoriaSeries.BUSCA) {
                // Na aba de busca, precisamos buscar a série completa na API
                String nomeSerie = (String) tabelaAtual.getValueAt(linha, 0);
                List<Serie> series = servicoTVMaze.buscarSeries(nomeSerie);
                if (series.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Não foi possível encontrar a série selecionada.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                serie = series.getFirst();
            } else {
                // Nas outras abas, a série já está completa na lista
                serie = categoriaAtual.getSeriesDaCategoria(usuario).get(linha);
            }

            // Verifica se a série já está na categoria de destino
            List<Serie> seriesDestino = categoriaDestino.getSeriesDaCategoria(usuario);
            if (seriesDestino != null && seriesDestino.stream().anyMatch(s -> s.getNome().equals(serie.getNome()))) {
                JOptionPane.showMessageDialog(this,
                    "Esta série já está na lista " + categoriaDestino.getDescricaoLista() + ".",
                    "Série Duplicada",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            categoriaDestino.adicionarSerie(usuario, serie);
            carregarListas();
            salvarDados();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            JOptionPane.showMessageDialog(this,
                "Operação interrompida: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao adicionar série: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private Serie criarSerieAPartirDaLinha(DefaultTableModel modelo, int linha) {
        String dataEstreiaStr = (String) modelo.getValueAt(linha, 6);
        String dataTerminoStr = (String) modelo.getValueAt(linha, 7);
        
        LocalDate dataEstreia = null;
        if (!Constantes.NAO_INFORMADA.equals(dataEstreiaStr)) {
            dataEstreia = LocalDate.parse(dataEstreiaStr);
        }
        
        LocalDate dataTermino = null;
        if (!Constantes.NAO_INFORMADA.equals(dataTerminoStr)) {
            dataTermino = LocalDate.parse(dataTerminoStr);
        }
        
        return new Serie(
            0,
            (String) modelo.getValueAt(linha, 0),
            (String) modelo.getValueAt(linha, 1),
            Arrays.asList(((String) modelo.getValueAt(linha, 2)).split(", ")),
            Double.parseDouble(modelo.getValueAt(linha, 3).toString()),
            (String) modelo.getValueAt(linha, 4),
            dataEstreia,
            dataTermino,
            (String) modelo.getValueAt(linha, 5),
            "",
            ""
        );
    }

    private List<Serie> obterSeriesDaTabela(DefaultTableModel modelo) {
        List<Serie> series = new ArrayList<>();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            series.add(criarSerieAPartirDaLinha(modelo, i));
        }
        return series;
    }

    private DefaultTableModel obterModeloDaAba(Component abaComponent) {
        return (DefaultTableModel) ((JTable) ((JScrollPane) ((JPanel) abaComponent)
            .getComponent(0)).getViewport().getView()).getModel();
    }

    private void ordenarLista() {
        CategoriaSeries categoriaAtual = CategoriaSeries.fromIndice(abas.getSelectedIndex());
        List<Serie> series;
        DefaultTableModel modeloAtual;
        
        if (categoriaAtual == CategoriaSeries.BUSCA) {
            modeloAtual = modeloTabela;
            if (modeloAtual.getRowCount() == 0) {
                return;
            }
            series = obterSeriesDaTabela(modeloAtual);
        } else {
            series = categoriaAtual.getSeriesDaCategoria(usuario);
            if (series == null || series.isEmpty()) {
                return;
            }
            modeloAtual = obterModeloDaAba(abas.getSelectedComponent());
        }

        OpcaoOrdenacao opcao = OpcaoOrdenacao.fromIndice(comboOrdenacao.getSelectedIndex());
        Comparator<Serie> comparador = opcao.getComparador();
        
        if (botaoOrdemCrescente.isSelected()) {
            comparador = comparador.reversed();
        }
        
        series.sort(comparador);
        modeloAtual.setRowCount(0);
        preencherTabela(modeloAtual, series);
    }

    private void carregarListas() {
        // Recriar as abas com as listas atualizadas
        for (CategoriaSeries categoria : CategoriaSeries.values()) {
            if (categoria != CategoriaSeries.BUSCA) {
                List<Serie> series = categoria.getSeriesDaCategoria(usuario);
                if (series != null && !series.isEmpty()) {
                    OpcaoOrdenacao opcao = OpcaoOrdenacao.fromIndice(comboOrdenacao.getSelectedIndex());
                    Comparator<Serie> comparador = opcao.getComparador();
                    
                    if (botaoOrdemCrescente.isSelected()) {
                        comparador = comparador.reversed();
                    }
                    
                    series.sort(comparador);
                }
                abas.setComponentAt(categoria.getIndice(), 
                    criarPainelLista(series));
            }
        }
    }

    private void salvarDados() {
        try {
            gerenciadorPersistencia.salvarUsuario(usuario);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao salvar dados: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 