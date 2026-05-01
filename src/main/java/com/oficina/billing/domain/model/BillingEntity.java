package com.oficina.billing.domain.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class BillingEntity {
    
    private String id; // ID da Ordem de Serviço
    private String clienteNome;
    private String clienteEmail;
    private Double valorCobrado;
    private String statusPagamento; // PENDING, PAID, CANCELLED
    private String linkCheckout;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }
}
