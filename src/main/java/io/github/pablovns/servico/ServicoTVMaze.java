package io.github.pablovns.servico;

import io.github.pablovns.modelo.Serie;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por fazer as requisições à API do TVMaze.
 */
public class ServicoTVMaze {
    private static final String URL_BASE = "https://api.tvmaze.com";
    private final HttpClient cliente;

    public ServicoTVMaze() {
        this.cliente = HttpClient.newHttpClient();
    }

    /**
     * Busca séries pelo nome.
     * @param nome Nome da série a ser buscada
     * @return Lista de séries encontradas
     * @throws IOException Em caso de erro na comunicação com a API
     * @throws InterruptedException Em caso de interrupção da requisição
     */
    public List<Serie> buscarSeries(String nome) throws IOException, InterruptedException {
        String url = URL_BASE + "/search/shows?q=" + nome.replace(" ", "+");
        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());
        return parseResultadoBusca(resposta.body());
    }

    private List<Serie> parseResultadoBusca(String json) {
        List<Serie> series = new ArrayList<>();
        JSONArray resultados = new JSONArray(json);

        for (int i = 0; i < resultados.length(); i++) {
            JSONObject resultado = resultados.getJSONObject(i);
            JSONObject show = resultado.getJSONObject("show");
            
            Serie serie = criarSerieDoJSON(show);
            if (serie != null) {
                series.add(serie);
            }
        }

        return series;
    }

    private Serie criarSerieDoJSON(JSONObject show) {
        try {
            int id = show.getInt("id");
            String nome = show.getString("name");
            String idioma = show.isNull("language") ? "Não informado" : show.getString("language");
            
            List<String> generos = new ArrayList<>();
            JSONArray generosJSON = show.getJSONArray("genres");
            for (int j = 0; j < generosJSON.length(); j++) {
                generos.add(generosJSON.getString(j));
            }

            double nota = show.has("rating") && !show.isNull("rating") ? 
                         show.getJSONObject("rating").optDouble("average", 0.0) : 0.0;

            String estado = show.isNull("status") ? "Não informado" : show.getString("status");
            
            LocalDate dataEstreia = null;
            if (!show.isNull("premiered")) {
                dataEstreia = LocalDate.parse(show.getString("premiered"));
            }

            LocalDate dataTermino = null;
            if (!show.isNull("ended")) {
                dataTermino = LocalDate.parse(show.getString("ended"));
            }

            String emissora = "Não informado";
            if (!show.isNull("network") && !show.getJSONObject("network").isNull("name")) {
                emissora = show.getJSONObject("network").getString("name");
            }

            String resumo = show.isNull("summary") ? "Sem resumo disponível" : 
                          show.getString("summary").replaceAll("<[^>]*>", "");

            String urlImagem = show.has("image") && !show.isNull("image") ? 
                             show.getJSONObject("image").optString("medium", "") : "";

            return new Serie(id, nome, idioma, generos, nota, estado, dataEstreia, 
                           dataTermino, emissora, resumo, urlImagem);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
} 