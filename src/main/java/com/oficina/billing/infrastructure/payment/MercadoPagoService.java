package com.oficina.billing.infrastructure.payment;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import com.oficina.billing.domain.model.OsCriadaMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;

@Service
public class MercadoPagoService {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @PostConstruct
    public void init() {
        // Inicializa o SDK do Mercado Pago com o token do application.properties
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public String gerarLinkPagamento(OsCriadaMessage os) {
        try {
            PreferenceClient client = new PreferenceClient();

            // 1. Cria o item que será cobrado
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .title("Ordem de Serviço #" + os.getOsId() + " - " + os.getDescricao())
                    .quantity(1)
                    .unitPrice(os.getValorTotal())
                    .build();

            // 2. Configura a Preferência de pagamento
            PreferenceRequest request = PreferenceRequest.builder()
                    .items(Collections.singletonList(item))
                    // Opcional: Para onde redirecionar após o pagamento
                    // .backUrls(PreferenceBackUrlsRequest.builder().success("https://sua-oficina.com/sucesso").build())
                    .build();

            // 3. Chama a API
            Preference preference = client.create(request);

            // Retorna o link (URL) do checkout do Mercado Pago
            return preference.getInitPoint();

        } catch (Exception e) {
            System.err.println("Erro ao gerar link no Mercado Pago: " + e.getMessage());
            return null;
        }
    }
}
