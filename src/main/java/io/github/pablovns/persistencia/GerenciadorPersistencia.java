package io.github.pablovns.persistencia;

import io.github.pablovns.modelo.Serie;
import io.github.pablovns.modelo.Usuario;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por gerenciar a persistência dos dados do usuário em formato JSON.
 */
public class GerenciadorPersistencia {
    private static final String ARQUIVO_DADOS = "dados_usuario.json";

    public void salvarUsuario(Usuario usuario) throws IOException {
        JSONObject dadosUsuario = new JSONObject();
        dadosUsuario.put("nome", usuario.getNome());
        
        // Salvar séries favoritas
        JSONArray favoritas = new JSONArray();
        for (Serie serie : usuario.getSeriesFavoritas()) {
            favoritas.put(serieParaJSON(serie));
        }
        dadosUsuario.put("seriesFavoritas", favoritas);
        
        // Salvar séries assistidas
        JSONArray assistidas = new JSONArray();
        for (Serie serie : usuario.getSeriesAssistidas()) {
            assistidas.put(serieParaJSON(serie));
        }
        dadosUsuario.put("seriesAssistidas", assistidas);
        
        // Salvar séries para assistir
        JSONArray paraAssistir = new JSONArray();
        for (Serie serie : usuario.getSeriesParaAssistir()) {
            paraAssistir.put(serieParaJSON(serie));
        }
        dadosUsuario.put("seriesParaAssistir", paraAssistir);

        Files.writeString(Paths.get(ARQUIVO_DADOS), dadosUsuario.toString(2));
    }

    public Usuario carregarUsuario() throws IOException {
        Path arquivo = Paths.get(ARQUIVO_DADOS);
        if (!Files.exists(arquivo)) {
            return null;
        }

        String conteudo = Files.readString(arquivo);
        JSONObject dadosUsuario = new JSONObject(conteudo);

        Usuario usuario = new Usuario(dadosUsuario.getString("nome"));

        // Carregar séries favoritas
        JSONArray favoritas = dadosUsuario.getJSONArray("seriesFavoritas");
        for (int i = 0; i < favoritas.length(); i++) {
            Serie serie = jsonParaSerie(favoritas.getJSONObject(i));
            usuario.adicionarSerieFavorita(serie);
        }

        // Carregar séries assistidas
        JSONArray assistidas = dadosUsuario.getJSONArray("seriesAssistidas");
        for (int i = 0; i < assistidas.length(); i++) {
            Serie serie = jsonParaSerie(assistidas.getJSONObject(i));
            usuario.adicionarSerieAssistida(serie);
        }

        // Carregar séries para assistir
        JSONArray paraAssistir = dadosUsuario.getJSONArray("seriesParaAssistir");
        for (int i = 0; i < paraAssistir.length(); i++) {
            Serie serie = jsonParaSerie(paraAssistir.getJSONObject(i));
            usuario.adicionarSerieParaAssistir(serie);
        }

        return usuario;
    }

    private JSONObject serieParaJSON(Serie serie) {
        JSONObject jsonSerie = new JSONObject();
        jsonSerie.put("id", serie.getId());
        jsonSerie.put("nome", serie.getNome());
        jsonSerie.put("idioma", serie.getIdioma());
        jsonSerie.put("generos", new JSONArray(serie.getGeneros()));
        jsonSerie.put("nota", serie.getNota());
        jsonSerie.put("estado", serie.getEstado());
        jsonSerie.put("dataEstreia", serie.getDataEstreia() != null ? serie.getDataEstreia().toString() : JSONObject.NULL);
        jsonSerie.put("dataTermino", serie.getDataTermino() != null ? serie.getDataTermino().toString() : JSONObject.NULL);
        jsonSerie.put("emissora", serie.getEmissora());
        jsonSerie.put("resumo", serie.getResumo());
        jsonSerie.put("urlImagem", serie.getUrlImagem());
        return jsonSerie;
    }

    private Serie jsonParaSerie(JSONObject json) {
        List<String> generos = new ArrayList<>();
        JSONArray generosArray = json.getJSONArray("generos");
        for (int i = 0; i < generosArray.length(); i++) {
            generos.add(generosArray.getString(i));
        }

        LocalDate dataEstreia = json.isNull("dataEstreia") ? null : 
                               LocalDate.parse(json.getString("dataEstreia"));
        LocalDate dataTermino = json.isNull("dataTermino") ? null : 
                               LocalDate.parse(json.getString("dataTermino"));

        return new Serie(
            json.getInt("id"),
            json.getString("nome"),
            json.getString("idioma"),
            generos,
            json.getDouble("nota"),
            json.getString("estado"),
            dataEstreia,
            dataTermino,
            json.getString("emissora"),
            json.getString("resumo"),
            json.getString("urlImagem")
        );
    }
} 