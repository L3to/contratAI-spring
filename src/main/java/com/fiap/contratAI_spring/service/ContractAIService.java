package com.fiap.contratAI_spring.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Gson;
import com.fiap.contratAI_spring.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import okhttp3.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class ContractAIService {

    private static final Logger log = LoggerFactory.getLogger(ContractAIService.class);

    private final RabbitTemplate rabbitTemplate;
    private final OkHttpClient httpClient;
    private final String ollamaBaseUrl;
    private final String modelName;
    private final Gson gson;

    /**
     * Número máximo de caracteres permitidos no prompt enviado ao modelo.
     * Se o contrato/termos excederem, serão truncados para evitar erros no endpoint da IA.
     */
    private final int maxPromptChars;

    public ContractAIService(
            RabbitTemplate rabbitTemplate,
            @Value("${ollama.base-url:http://localhost:11434}") String ollamaBaseUrl,
            @Value("${ollama.model:gpt-oss:20b}") String modelName,
            @Value("${ollama.max-prompt-chars:15000}") int maxPromptChars,
            @Value("${okhttp.connect-timeout-seconds:30}") long connectTimeoutSeconds,
            @Value("${okhttp.read-timeout-seconds:300}") long readTimeoutSeconds,
            @Value("${okhttp.write-timeout-seconds:30}") long writeTimeoutSeconds
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.ollamaBaseUrl = ollamaBaseUrl.endsWith("/") ? ollamaBaseUrl.substring(0, ollamaBaseUrl.length()-1) : ollamaBaseUrl;
        this.modelName = modelName;
        this.maxPromptChars = Math.max(1000, maxPromptChars); // segurança mínima

        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(readTimeoutSeconds, TimeUnit.SECONDS)
                .writeTimeout(writeTimeoutSeconds, TimeUnit.SECONDS)
                .build();

        this.gson = new Gson();
    }


    /**
     * Gera um rascunho de contrato a partir de 'terms'.
     * Faz validações de tamanho e chama o endpoint do Ollama.
     *
     * @param terms descrição/termos para gerar o contrato
     * @return texto gerado pela IA
     */
    public String generateContract(String terms) {
        if (terms == null || terms.isBlank()) {
            throw new IllegalArgumentException("Parâmetro 'terms' é obrigatório.");
        }

        String prompt = "Gere um rascunho de contrato de " + terms + " em português brasileiro, " +
                "incluindo cláusulas de rescisão e foro de eleição.";

        prompt = sanitizeAndTruncate(prompt);

        String response = callOllamaAPI(prompt);

        log.info("Contrato gerado (len={}): returned {} characters", response.length(), response.length());
        return response;
    }

    /**
     * Analisa um contrato e retorna o resultado.
     * Usa um prompt com instruções claras para a IA.
     *
     * @param rawContract texto completo do contrato a ser analisado
     * @return resultado da análise
     */
    public String analyzeContract(String rawContract) {
        if (rawContract == null || rawContract.isBlank()) {
            throw new IllegalArgumentException("Conteúdo do contrato é obrigatório para análise.");
        }

        String prompt = String.format("""
                Você é um assistente jurídico especializado em análise de contratos.
                Analise o seguinte contrato e retorne:
                1) As três cláusulas mais arriscadas para a parte mais fraca, com breve justificativa.
                2) Uma sugestão de refatoração para a cláusula mais complexa.
                
                CONTRATO:
                %s
                """, rawContract);

        prompt = sanitizeAndTruncate(prompt);

        String response = callOllamaAPI(prompt);

        log.info("Análise realizada (len={}): returned {} characters", response.length(), response.length());
        return response;
    }

    /**
     * Envia o contrato para análise assíncrona via RabbitMQ.
     * Monta JSON de forma segura usando Gson.
     *
     * @param rawContract conteúdo do contrato
     * @param userId id do usuário que solicitou
     */
    public void sendContractForAnalysis(String rawContract, Long userId) {
        if (rawContract == null) rawContract = "";
        if (userId == null) throw new IllegalArgumentException("userId não pode ser nulo.");

        JsonObject json = new JsonObject();
        json.addProperty("contract", rawContract);
        json.addProperty("userId", userId);

        String message = gson.toJson(json);

        log.debug("Enviando mensagem ao RabbitMQ exchange='{}' routingKey='{}' size={}", RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, message.length());

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, message);
        } catch (Exception e) {
            log.error("Falha ao enviar mensagem para RabbitMQ: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao enfileirar contrato para análise.", e);
        }
    }

    // -------------------------
    // internal helpers
    // -------------------------

    /**
     * Faz a chamada HTTP ao serviço Ollama (endpoint /api/generate).
     * Faz parsing seguro da resposta e trata erros comuns.
     *
     * @param prompt prompt já sanitizado e truncado
     * @return string de resposta retornada pela IA
     */
    private String callOllamaAPI(String prompt) {
        Instant start = Instant.now();
        log.debug("Chamando Ollama (model='{}') promptLen={}", modelName, prompt.length());

        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", modelName);
            requestBody.addProperty("prompt", prompt);
            requestBody.addProperty("stream", false);

            RequestBody body = RequestBody.create(requestBody.toString(), MediaType.parse("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(ollamaBaseUrl + "/api/generate")
                    .post(body)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response == null) {
                    throw new IOException("Resposta nula do Ollama.");
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    log.error("Ollama retornou status não-sucesso: {} body={}", response.code(), responseBody);
                    throw new IOException("Erro na chamada Ollama: HTTP " + response.code());
                }

                JsonObject jsonResponse;
                try {
                    jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                } catch (JsonSyntaxException | IllegalStateException ex) {
                    log.error("Resposta inválida do Ollama (não é JSON): {}", responseBody);
                    throw new IOException("Resposta inválida do Ollama: " + ex.getMessage(), ex);
                }

                if (jsonResponse.has("response") && !jsonResponse.get("response").isJsonNull()) {
                    String result = jsonResponse.get("response").getAsString();
                    log.debug("Ollama respondeu com {} chars em {}ms", result.length(), Duration.between(start, Instant.now()).toMillis());
                    return result;
                } else if (jsonResponse.has("error")) {
                    String error = jsonResponse.get("error").toString();
                    log.error("Ollama retornou erro no JSON: {}", error);
                    throw new IOException("Erro da API Ollama: " + error);
                } else {
                    log.error("Resposta do Ollama não contém 'response' nem 'error': {}", jsonResponse);
                    throw new IOException("Resposta inesperada da API Ollama.");
                }
            }
        } catch (IOException e) {
            log.error("Erro na chamada ao Ollama: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao chamar API do Ollama: " + e.getMessage(), e);
        } finally {
            log.debug("Tempo total chamada Ollama: {}ms", Duration.between(start, Instant.now()).toMillis());
        }
    }

    private String sanitizeAndTruncate(String text) {
        if (text == null) return "";
        String cleaned = text.replace("\r\n", "\n").replace("\r", "\n");
        cleaned = cleaned.replace("\u0000", ""); 
        if (cleaned.length() > maxPromptChars) {
            log.warn("Prompt excedeu maxPromptChars ({}). Truncando de {} para {} caracteres.", maxPromptChars, cleaned.length(), maxPromptChars);
            cleaned = cleaned.substring(0, maxPromptChars);
        }
        return cleaned;
    }
}
