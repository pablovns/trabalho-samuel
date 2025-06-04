package io.github.pablovns.gui;

import io.github.pablovns.modelo.CategoriaSeries;
import io.github.pablovns.modelo.Serie;
import io.github.pablovns.modelo.Usuario;
import io.github.pablovns.persistencia.GerenciadorPersistencia;
import io.github.pablovns.servico.ServicoTVMaze;

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
        String[] colunas = {"Nome", "Idioma", "Gêneros", "Nota", "Estado", "Emissora", "Data de Estreia"};
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
        comboOrdenacao = new JComboBox<>(new String[]{
            "Ordem Alfabética",
            "Nota",
            "Estado",
            "Data de Estreia"
        });
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
            new String[]{"Nome", "Idioma", "Gêneros", "Nota", "Estado", "Emissora", "Data de Estreia"}, 0
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
            return; // Apenas limpa a tabela se a lista estiver vazia
        }

        for (Serie serie : series) {
            modelo.addRow(new Object[]{
                serie.getNome(),
                serie.getIdioma(),
                String.join(", ", serie.getGeneros()),
                serie.getNota(),
                serie.getEstado(),
                serie.getEmissora(),
                serie.getDataEstreia() != null ? serie.getDataEstreia().toString() : "Não informada"
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
            modeloTabela.setRowCount(0);
            for (Serie serie : series) {
                modeloTabela.addRow(new Object[]{
                    serie.getNome(),
                    serie.getIdioma(),
                    String.join(", ", serie.getGeneros()),
                    serie.getNota(),
                    serie.getEstado(),
                    serie.getEmissora(),
                    serie.getDataEstreia() != null ? serie.getDataEstreia().toString() : "Não informada"
                });
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            JOptionPane.showMessageDialog(this,
                    "Operação interrompida: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao buscar séries: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void adicionarSerieSelecionada(CategoriaSeries categoria) {
        int linha = tabelaResultados.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma série primeiro!");
            return;
        }

        try {
            String nomeSerie = (String) tabelaResultados.getValueAt(linha, 0);
            List<Serie> series = servicoTVMaze.buscarSeries(nomeSerie);
            if (!series.isEmpty()) {
                Serie serie = series.getFirst();
                categoria.adicionarSerie(usuario, serie);
                carregarListas();
                salvarDados();
            }
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

    private void ordenarLista() {
        CategoriaSeries categoriaAtual = CategoriaSeries.fromIndice(abas.getSelectedIndex());
        DefaultTableModel modeloAtual;
        List<Serie> series;
        
        if (categoriaAtual == CategoriaSeries.BUSCA) {
            modeloAtual = modeloTabela;
            if (modeloAtual.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "Não há séries para ordenar. Faça uma busca primeiro.",
                    "Lista Vazia",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            series = new ArrayList<>();
            for (int i = 0; i < modeloAtual.getRowCount(); i++) {
                String dataEstreiaStr = (String) modeloAtual.getValueAt(i, 6);
                LocalDate dataEstreia = null;
                if (!"Não informada".equals(dataEstreiaStr)) {
                    dataEstreia = LocalDate.parse(dataEstreiaStr);
                }
                
                Serie serie = new Serie(
                    0,
                    (String) modeloAtual.getValueAt(i, 0),
                    (String) modeloAtual.getValueAt(i, 1),
                    Arrays.asList(((String) modeloAtual.getValueAt(i, 2)).split(", ")),
                    Double.parseDouble(modeloAtual.getValueAt(i, 3).toString()),
                    (String) modeloAtual.getValueAt(i, 4),
                    dataEstreia,
                    null,
                    (String) modeloAtual.getValueAt(i, 5),
                    "",
                    ""
                );
                series.add(serie);
            }
        } else {
            series = categoriaAtual.getSeriesDaCategoria(usuario);
            
            if (series == null || series.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Não há séries " + categoriaAtual.getDescricaoLista() + " para ordenar.",
                    "Lista Vazia",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            modeloAtual = (DefaultTableModel) ((JTable) ((JScrollPane) ((JPanel) abas.getSelectedComponent())
                    .getComponent(0)).getViewport().getView()).getModel();
        }

        boolean ordemDecrescente = botaoOrdemCrescente.isSelected();
        
        Comparator<Serie> comparador = switch (comboOrdenacao.getSelectedIndex()) {
            case 0 -> Comparator.comparing(Serie::getNome);
            case 1 -> Comparator.comparing(Serie::getNota);
            case 2 -> Comparator.comparing(Serie::getEstado);
            case 3 -> Comparator.comparing(Serie::getDataEstreia, 
                     Comparator.nullsLast(Comparator.naturalOrder()));
            default -> null;
        };

        if (comparador != null) {
            if (ordemDecrescente) {
                comparador = comparador.reversed();
            }
            series.sort(comparador);
            modeloAtual.setRowCount(0);
            preencherTabela(modeloAtual, series);
        }
    }

    private void carregarListas() {
        // Recriar as abas com as listas atualizadas
        for (CategoriaSeries categoria : CategoriaSeries.values()) {
            if (categoria != CategoriaSeries.BUSCA) {
                abas.setComponentAt(categoria.getIndice(), 
                    criarPainelLista(categoria.getSeriesDaCategoria(usuario)));
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