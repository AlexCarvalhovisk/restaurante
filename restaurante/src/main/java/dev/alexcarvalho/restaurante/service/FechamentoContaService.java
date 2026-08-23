package dev.alexcarvalho.restaurante.service;

import dev.alexcarvalho.restaurante.domain.entity.FechamentoConta;
import dev.alexcarvalho.restaurante.domain.entity.Pedido;
import dev.alexcarvalho.restaurante.domain.entity.PedidoItem;
import dev.alexcarvalho.restaurante.domain.enums.StatusItemPedido;
import dev.alexcarvalho.restaurante.domain.enums.StatusPedido;
import dev.alexcarvalho.restaurante.dto.FechamentoContaRequest;
import dev.alexcarvalho.restaurante.dto.FechamentoContaResponse;
import dev.alexcarvalho.restaurante.exception.RegraNegocioException;
import dev.alexcarvalho.restaurante.repository.FechamentoContaRepository;
import dev.alexcarvalho.restaurante.repository.PedidoItemRepository;
import dev.alexcarvalho.restaurante.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FechamentoContaService {

    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final FechamentoContaRepository fechamentoContaRepository;

    public FechamentoContaService(PedidoRepository pedidoRepository, PedidoItemRepository pedidoItemRepository,
                                  FechamentoContaRepository fechamentoContaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
        this.fechamentoContaRepository = fechamentoContaRepository;
    }

    public FechamentoContaResponse fecharConta(Long pedidoId, FechamentoContaRequest request) {
        Pedido pedido = buscarPedidoPorId(pedidoId);
        if(pedido.getStatus() == StatusPedido.FECHADO){
            throw new RegraNegocioException("Pedido já está fechado.");
        }

        if(pedido.getStatus() == StatusPedido.CANCELADO){
            throw new RegraNegocioException("Pedido cancelado  não pode ser fechado..");
        }

        if(fechamentoContaRepository.existsByPedidoId(pedidoId)){
            throw new RegraNegocioException("Já existe um fechamento com este pedido.");
        }

        List<PedidoItem> itens = pedidoItemRepository.findByPedidoId(pedidoId);
        if(itens.isEmpty()){
            throw new RegraNegocioException("Não é possivel fechar uma conta sem intes.");
        }

        List<PedidoItem> itensNaoEntregues = pedidoItemRepository
                .findByPedidoIdAndStatusNot(pedidoId, StatusItemPedido.ENTREGUE);

        if(!itensNaoEntregues.isEmpty()){
            throw new RegraNegocioException("Todos os itens precisam estar entregues para fechar a conta.");
        }
        BigDecimal subtotal = itens.stream().map(item ->
                item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxaServico = request.taxaServico() != null ? request.taxaServico() : BigDecimal.ZERO;
        BigDecimal desconto = request.desconto() != null ? request.desconto() : BigDecimal.ZERO;

        if(taxaServico.compareTo(BigDecimal.ZERO) < 0){
            throw new RegraNegocioException("Taxa de serviço não pode ser negativa.");
        }

        if(desconto.compareTo(BigDecimal.ZERO) < 0){
            throw new RegraNegocioException("O desconto não pode ser negativo.");
        }

        BigDecimal total = subtotal.add(taxaServico.subtract(desconto));
        if(total.compareTo(BigDecimal.ZERO) < 0){
            throw new RegraNegocioException("O total da conta não pode ser negativo.");
        }

        FechamentoConta fechamento = new FechamentoConta();
        fechamento.setPedido(pedido);
        fechamento.setSubtotal(subtotal);
        fechamento.setTaxaServico(taxaServico);
        fechamento.setDesconto(desconto);
        fechamento.setTotal(total);

        pedido.setStatus(StatusPedido.FECHADO);
        pedido.setDataFechamento(LocalDateTime.now());

        FechamentoConta fechamentoSalvo = fechamentoContaRepository.save(fechamento);
        pedidoRepository.save(pedido);
        return FechamentoContaResponse.fromEntity(fechamentoSalvo);
    }

    public FechamentoContaResponse buscarPorPedido(Long pedidoId) {
        FechamentoConta fechamento = fechamentoContaRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RegraNegocioException("Fechamento não encontrado."));
        return FechamentoContaResponse.fromEntity(fechamento);
    }

    private Pedido buscarPedidoPorId(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado."));
    }
}
