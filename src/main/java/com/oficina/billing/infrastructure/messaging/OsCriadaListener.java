package com.oficina.billing.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oficina.billing.domain.model.BillingEntity;
import com.oficina.billing.domain.model.OsCriadaMessage;
import com.oficina.billing.infrastructure.database.BillingRepository;
import com.oficina.billing.infrastructure.payment.StripeService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OsCriadaListener {

    private final ObjectMapper objectMapper;
    private final StripeService stripeService;
    private final BillingRepository billingRepository;

    @SqsListener("billing-os-criada-queue")
    public void processarNovaOs(String payloadJson) {
        System.out.println("==================================================");
        System.out.println("💰 SAGA (STAGE): Nova OS recebida da fila LocalStack!");
        
        try {
            // 1. Extrair os dados da mensagem
            OsCriadaMessage mensagem = objectMapper.readValue(payloadJson, OsCriadaMessage.class);
            System.out.println("Processando OS #" + mensagem.getOsId() + " para " + mensagem.getClienteNome());

            // 2. Gerar link de pagamento na Stripe (Sandbox)
            String linkPagamento = stripeService.gerarLinkPagamento(mensagem);
            
            if (linkPagamento != null) {
                System.out.println("✅ Link Stripe gerado com sucesso: " + linkPagamento);
                
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
            }

        } catch (Exception e) {
            System.err.println("Erro ao processar a Saga no ambiente Dev: " + e.getMessage());
        }
        System.out.println("==================================================");
    }
}
