package com.oficina.billing.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OsCriadaMessage {
    private Long osId;
    private String clienteNome;
    private String clienteEmail;
    private BigDecimal valorTotal;
    private String descricao;
}
