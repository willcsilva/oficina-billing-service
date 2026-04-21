package com.oficina.billing.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oficina.billing.domain.model.OsCriadaMessage;
import com.oficina.billing.infrastructure.payment.MercadoPagoService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OsCriadaListener {

    private final ObjectMapper objectMapper;
    private final MercadoPagoService mercadoPagoService;

    @SqsListener("billing-os-criada-queue")
    public void processarNovaOs(String payloadJson) {
        System.out.println("==================================================");
        System.out.println("💰 SAGA: Nova OS recebida no Billing Service!");
        
        try {
            // 1. Converte o JSON string (da fila) para o nosso objeto Java
            OsCriadaMessage mensagem = objectMapper.readValue(payloadJson, OsCriadaMessage.class);
            
            System.out.println("Processando OS #" + mensagem.getOsId() + " para " + mensagem.getClienteNome());
            System.out.println("Valor a cobrar: R$ " + mensagem.getValorTotal());

            // 2. Chama o Mercado Pago
            String linkPagamento = mercadoPagoService.gerarLinkPagamento(mensagem);
            
            if (linkPagamento != null) {
                System.out.println("✅ Link de Pagamento gerado com sucesso!");
                System.out.println("🔗 " + linkPagamento);
                
                // PRÓXIMO PASSO FUTURO: Salvar no DynamoDB
            }

        } catch (Exception e) {
            System.err.println("Erro ao processar a mensagem SQS: " + e.getMessage());
        }
        System.out.println("==================================================");
    }
}