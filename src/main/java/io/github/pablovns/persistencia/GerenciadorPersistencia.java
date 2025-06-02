package io.github.pablovns.persistencia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.pablovns.modelo.Usuario;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Classe responsável por gerenciar a persistência dos dados do usuário em formato JSON.
 */
public class GerenciadorPersistencia {
    private static final String ARQUIVO_DADOS = "dados_usuario.json";
    private final Gson gson;

    public GerenciadorPersistencia() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting() // Para formatar o JSON de forma legível
                .create();
    }

    public void salvarUsuario(Usuario usuario) throws IOException {
        String json = gson.toJson(usuario);
        Files.writeString(Paths.get(ARQUIVO_DADOS), json);
    }

    public Usuario carregarUsuario() throws IOException {
        Path arquivo = Paths.get(ARQUIVO_DADOS);
        if (!Files.exists(arquivo)) {
            return null;
        }

        String conteudo = Files.readString(arquivo);
        return gson.fromJson(conteudo, Usuario.class);
    }

}