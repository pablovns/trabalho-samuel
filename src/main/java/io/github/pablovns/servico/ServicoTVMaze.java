package io.github.pablovns.servico;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.pablovns.modelo.Serie;
import io.github.pablovns.util.Constantes;
import io.github.pablovns.util.LocalDateAdapter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por fazer as requisições à API do TVMaze.
 */
public class ServicoTVMaze {
    private static final String URL_BASE = Constantes.URL_BASE_TVMAZE;
    private final HttpClient cliente;
    private final Gson gson;

    public ServicoTVMaze() {
        this.cliente = HttpClient.newHttpClient();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    /**
     * Busca séries pelo nome.
     * @param nome Nome da série a ser buscada
     * @return Lista de séries encontradas
     * @throws IOException Em caso de erro na comunicação com a API
     * @throws InterruptedException Em caso de interrupção da requisição
     */
    public List<Serie> buscarSeries(String nome) throws IOException, InterruptedException {
        String nomeSanitizado = URLEncoder.encode(nome, StandardCharsets.UTF_8);
        String url = URL_BASE + "/search/shows?q=" + nomeSanitizado;
        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());
        return parseResultadoBusca(resposta.body());
    }

    private List<Serie> parseResultadoBusca(String json) {
        try {
            Type tipoLista = new TypeToken<List<ResultadoBusca>>(){}.getType();
            List<ResultadoBusca> resultados = gson.fromJson(json, tipoLista);
            
            List<Serie> series = new ArrayList<>();
            for (ResultadoBusca resultado : resultados) {
                if (resultado.show != null) {
                    series.add(resultado.show);
                }
            }
            return series;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Classe auxiliar para deserialização do resultado da busca
    private static class ResultadoBusca {
        Serie show;
    }

}