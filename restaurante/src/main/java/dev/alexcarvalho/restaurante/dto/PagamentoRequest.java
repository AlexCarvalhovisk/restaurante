package dev.alexcarvalho.restaurante.dto;

public record PagamentoRequest(

        Double valor,
        String formaPagamento
) {
}
