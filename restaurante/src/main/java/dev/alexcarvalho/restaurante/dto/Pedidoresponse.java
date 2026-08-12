package dev.alexcarvalho.restaurante.dto;

import dev.alexcarvalho.restaurante.domain.entity.Pedido;
import dev.alexcarvalho.restaurante.domain.enums.StatusPedido;

import java.time.LocalDateTime;

public record Pedidoresponse(
        Long id,
        Long mesaId,
        Integer numeroMesa,
        LocalDateTime dataAbertura,
        LocalDateTime dataFechamento,
        StatusPedido status,
        String observacao
) {

    public static Pedidoresponse fromEntity(Pedido pedido) {
        return new Pedidoresponse(
                pedido.getId(),
                pedido.getMesa().getId(),
                pedido.getMesa().getNumero(),
                pedido.getDataAbertura(),
                pedido.getDataFechamento(),
                pedido.getStatus(),
                pedido.getObservacao()
        );
    }
}
