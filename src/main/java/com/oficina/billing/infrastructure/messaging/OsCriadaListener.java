package com.oficina.billing.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oficina.billing.domain.model.BillingEntity;
import com.oficina.billing.domain.model.OsCriadaMessage;
import com.oficina.billing.infrastructure.database.BillingRepository;
import com.oficina.billing.infrastructure.payment.StripeService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OsCriadaListener {

    private final ObjectMapper objectMapper;
    private final StripeService stripeService;
    private final BillingRepository billingRepository;

    // Fila alinhada com a infraestrutura IaC do LocalStack
    @SqsListener("oficina-os-queue")
    public void processarNovaOs(String payloadJson) {
        log.info("==================================================");
        log.info("💰 SAGA (STAGE): Nova OS recebida da fila LocalStack!");
        
        try {
            // 1. Extrair os dados da mensagem
            OsCriadaMessage mensagem = objectMapper.readValue(payloadJson, OsCriadaMessage.class);
            log.info("Processando faturamento da OS #{} para o cliente: {}", mensagem.getOsId(), mensagem.getClienteNome());

            // 2. Gerar link de pagamento na Stripe (Sandbox)
            String linkPagamento = stripeService.gerarLinkPagamento(mensagem);
            
            if (linkPagamento != null) {
                log.info("✅ Link Stripe gerado com sucesso: {}", linkPagamento);
                
                // 3. Salvar o registro no DynamoDB do LocalStack
                BillingEntity entity = new BillingEntity(
                        String.valueOf(mensagem.getOsId()),
                        mensagem.getClienteNome(),
                        mensagem.getClienteEmail(),
                        mensagem.getValorTotal().doubleValue(),
                        "PENDING",
                        linkPagamento
                );
                
                billingRepository.salvar(entity);
                log.info("💾 Fatura da OS #{} salva com sucesso no banco de dados.", mensagem.getOsId());
            }

        } catch (Exception e) {
            log.error("❌ Erro ao processar a Saga no ambiente Stage: {}", e.getMessage(), e);
        }
        log.info("==================================================");
    }
}