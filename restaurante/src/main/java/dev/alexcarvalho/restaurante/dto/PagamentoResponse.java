package dev.alexcarvalho.restaurante.dto;

public record PagamentoResponse(
        String status,
        String codigoTransacao
) {
}
