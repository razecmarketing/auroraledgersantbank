package com.aurora.ledger.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuração de Tópicos Kafka para Sistema Bancário
 * 
 * Esta configuração gerencia os tópicos Kafka necessários para Event Sourcing
 * bancário com requisitos de conformidade PCI DSS e retenção de longo prazo.
 */
@Configuration
@ConditionalOnProperty(
    name = "spring.kafka.bootstrap-servers", 
    matchIfMissing = false
)
public class KafkaTopicConfiguration {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    /**
     * Configuração do KafkaAdmin para gerenciamento de tópicos
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        configs.put(AdminClientConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 30000);
        return new KafkaAdmin(configs);
    }

    /**
     * Tópico para eventos de conta bancária
     * Retenção de 7 anos para conformidade bancária
     */
    @Bean
    public NewTopic accountEventsTopic() {
        return new NewTopic("aurora.account.events", 3, (short) 1)
                .configs(Map.of(
                    "retention.ms", String.valueOf(7L * 365 * 24 * 60 * 60 * 1000), // 7 anos
                    "compression.type", "gzip",
                    "cleanup.policy", "compact,delete",
                    "segment.ms", String.valueOf(7L * 24 * 60 * 60 * 1000), // 7 dias
                    "min.insync.replicas", "1"
                ));
    }

    /**
     * Tópico para eventos de transação
     * Retenção de 10 anos para conformidade SOX
     */
    @Bean
    public NewTopic transactionEventsTopic() {
        return new NewTopic("aurora.transaction.events", 5, (short) 1)
                .configs(Map.of(
                    "retention.ms", String.valueOf(10L * 365 * 24 * 60 * 60 * 1000), // 10 anos
                    "compression.type", "gzip",
                    "cleanup.policy", "compact,delete",
                    "segment.ms", String.valueOf(7L * 24 * 60 * 60 * 1000), // 7 dias
                    "min.insync.replicas", "1"
                ));
    }

    /**
     * Tópico para eventos de auditoria
     * Retenção de 10 anos para conformidade regulatória
     */
    @Bean
    public NewTopic auditEventsTopic() {
        return new NewTopic("aurora.audit.events", 3, (short) 1)
                .configs(Map.of(
                    "retention.ms", String.valueOf(10L * 365 * 24 * 60 * 60 * 1000), // 10 anos
                    "compression.type", "gzip",
                    "cleanup.policy", "delete",
                    "segment.ms", String.valueOf(30L * 24 * 60 * 60 * 1000), // 30 dias
                    "min.insync.replicas", "1"
                ));
    }

    /**
     * Tópico Dead Letter Queue para eventos falhados
     * Retenção de 1 ano para investigação
     */
    @Bean
    public NewTopic dlqEventsTopic() {
        return new NewTopic("aurora.dlq.events", 1, (short) 1)
                .configs(Map.of(
                    "retention.ms", String.valueOf(365L * 24 * 60 * 60 * 1000), // 1 ano
                    "compression.type", "gzip",
                    "cleanup.policy", "delete",
                    "segment.ms", String.valueOf(7L * 24 * 60 * 60 * 1000), // 7 dias
                    "min.insync.replicas", "1"
                ));
    }

    /**
     * Tópico para métricas e monitoramento
     * Retenção de 90 dias para análise de performance
     */
    @Bean
    public NewTopic metricsEventsTopic() {
        return new NewTopic("aurora.metrics.events", 2, (short) 1)
                .configs(Map.of(
                    "retention.ms", String.valueOf(90L * 24 * 60 * 60 * 1000), // 90 dias
                    "compression.type", "gzip",
                    "cleanup.policy", "delete",
                    "segment.ms", String.valueOf(24 * 60 * 60 * 1000), // 1 dia
                    "min.insync.replicas", "1"
                ));
    }
}