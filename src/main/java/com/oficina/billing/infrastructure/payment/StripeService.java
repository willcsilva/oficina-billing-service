package com.oficina.billing.infrastructure.payment;

import com.oficina.billing.domain.model.OsCriadaMessage;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public String gerarLinkPagamento(OsCriadaMessage os) {
        try {
            // A Stripe processa BRL em centavos (multiplicamos por 100)
            long amountInCents = os.getValorTotal().multiply(new BigDecimal(100)).longValue();

            // Evita erro de null pointer caso a descrição venha vazia do OS Service
            String nomeProduto = "Ordem de Serviço #" + os.getOsId();
            if (os.getDescricao() != null && !os.getDescricao().isEmpty()) {
                nomeProduto += " - " + os.getDescricao();
            }

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:8080/sucesso") 
                    .setCancelUrl("http://localhost:8080/cancelado")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("brl")
                                                    .setUnitAmount(amountInCents)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(nomeProduto)
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);
            return session.getUrl();

        } catch (Exception e) {
            System.err.println("Erro ao gerar link na Stripe Sandbox: " + e.getMessage());
            return null;
        }
    }
}
