package dev.alexcarvalho.restaurante.dto;

import dev.alexcarvalho.restaurante.domain.entity.Pedido;
import dev.alexcarvalho.restaurante.domain.enums.StatusPedido;

import java.time.LocalDateTime;

public record PedidoResponse(
        Long id,
        Long mesaId,
        Integer numeroMesa,
        LocalDateTime dataAbertura,
        LocalDateTime dataFechamento,
        StatusPedido status,
        String observacao
) {

    public static PedidoResponse fromEntity(Pedido pedido) {
        return new PedidoResponse(
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
