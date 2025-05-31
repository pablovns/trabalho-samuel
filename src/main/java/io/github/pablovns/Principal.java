package io.github.pablovns;

import io.github.pablovns.gui.TelaPrincipal;
import io.github.pablovns.modelo.Usuario;
import io.github.pablovns.persistencia.GerenciadorPersistencia;

import javax.swing.*;
import java.io.IOException;

public class Principal {
    public static void main(String[] args) {
        try {
            // Configurar o look and feel para parecer com o sistema operacional
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            GerenciadorPersistencia gerenciador = new GerenciadorPersistencia();
            Usuario usuario;

            try {
                usuario = gerenciador.carregarUsuario();
                if (usuario == null) {
                    String nome = JOptionPane.showInputDialog(null,
                        "Bem-vindo ao Gerenciador de Séries!\n" +
                        "Por favor, digite seu nome ou apelido:",
                        "Cadastro de Usuário",
                        JOptionPane.QUESTION_MESSAGE);

                    if (nome == null || nome.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null,
                            "É necessário informar um nome para utilizar o sistema.",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }

                    usuario = new Usuario(nome.trim());
                    try {
                        gerenciador.salvarUsuario(usuario);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null,
                            "Erro ao salvar dados do usuário: " + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }

                TelaPrincipal telaPrincipal = new TelaPrincipal(usuario);
                telaPrincipal.setVisible(true);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                    "Erro ao carregar dados do usuário: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
} 