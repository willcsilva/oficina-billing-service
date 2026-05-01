package com.oficina.billing.infrastructure.database;

import com.oficina.billing.domain.model.BillingEntity;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class BillingRepository {

    private final DynamoDbTable<BillingEntity> table;

    public BillingRepository(DynamoDbEnhancedClient enhancedClient) {
        // Mapeia a classe para a tabela "BillingDB" que existe no LocalStack
        this.table = enhancedClient.table("BillingDB", TableSchema.fromBean(BillingEntity.class));
    }

    public void salvar(BillingEntity entity) {
        table.putItem(entity);
        System.out.println("💾 Registro salvo no DynamoDB Local! ID: " + entity.getId());
    }
}
