package com.fiap.contratAI_spring.queue;

import com.fiap.contratAI_spring.config.RabbitMQConfig;
import com.fiap.contratAI_spring.model.Contract;
import com.fiap.contratAI_spring.model.User;
import com.fiap.contratAI_spring.model.enums.ContractStatus;
import com.fiap.contratAI_spring.repository.ContractRepository;
import com.fiap.contratAI_spring.repository.UserRepository;
import com.fiap.contratAI_spring.service.ContractAIService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ContractAnalysisListener {

    private final Set<String> processingMessages = ConcurrentHashMap.newKeySet();
    private final ContractAIService contractAIService;
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;

    public ContractAnalysisListener(ContractAIService contractAIService,
                                   ContractRepository contractRepository,
                                   UserRepository userRepository) {
        this.contractAIService = contractAIService;
        this.contractRepository = contractRepository;
        this.userRepository = userRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void processContractAnalysis(String message) {
        String messageHash = String.valueOf(message.hashCode());
        
        if (!processingMessages.add(messageHash)) {
            System.out.println("[DEDUP] Mensagem duplicada detectada, ignorando: " + messageHash);
            return;
        }
        
        System.out.println("[INICIO] Processando mensagem: " + messageHash);
        System.out.println("[FILA] Mensagens em processamento simultâneo: " + processingMessages.size());
        System.out.println("Recebido para processamento assíncrono: " + message);

        try {
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();
            String contractText = json.get("contract").getAsString();
            Long userId = json.get("userId").getAsLong();
            
            System.out.println("Contrato extraído com sucesso. Tamanho: " + contractText.length() + " caracteres");
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userId));
            
            Contract contract = new Contract();
            contract.setContent(contractText);
            contract.setOwner(user);
            contract.setTitle("Contrato para Análise");
            contract.setStatus(ContractStatus.PENDING);
            contract = contractRepository.save(contract);
            
            System.out.println("Contrato salvo no banco de dados com ID: " + contract.getId());
            
            String analysisResult = contractAIService.analyzeContract(contractText);
            
            contract.setContent(contractText + "\n\n=== ANÁLISE ===\n" + analysisResult);
            contract.setStatus(ContractStatus.ANALYZED);
            contractRepository.save(contract);
            
            System.out.println("Análise concluída e salva. ID: " + contract.getId());
        } catch (Exception e) {
            System.err.println("Erro ao processar contrato: " + e.getMessage());
            throw new RuntimeException("Falha no processamento - mensagem será descartada", e);
        } finally {
            processingMessages.remove(messageHash);
            System.out.println("[FIM] Processamento finalizado. Mensagens restantes na fila: " + processingMessages.size());
        }
    }
}