package dev.alexcarvalho.restaurante.dto;

import dev.alexcarvalho.restaurante.domain.entity.Produto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProdutoResponse(
        //Aqui escrevo o que eu vou responder para o front-end
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        Boolean disponivel,
        Integer tempoPreparoMinutos,
        Long categoriaId,
        String categoriaNome,
        LocalDateTime criadoEm
) {

    public static ProdutoResponse fromEntity(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getDisponivel(),
                produto.getTempoPreparoMinutos(),
                produto.getCategoria().getId(),
                produto.getCategoria().getNome(),
                produto.getCriadoEm()
        );
    }
}
