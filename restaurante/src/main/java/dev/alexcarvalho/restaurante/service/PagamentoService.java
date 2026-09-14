package dev.alexcarvalho.restaurante.service;

import dev.alexcarvalho.restaurante.client.PagamentoClient;
import dev.alexcarvalho.restaurante.domain.entity.FechamentoConta;
import dev.alexcarvalho.restaurante.domain.entity.Mesa;
import dev.alexcarvalho.restaurante.domain.entity.Pagamento;
import dev.alexcarvalho.restaurante.domain.entity.Pedido;
import dev.alexcarvalho.restaurante.domain.enums.FormaPagamento;
import dev.alexcarvalho.restaurante.domain.enums.StatusMesa;
import dev.alexcarvalho.restaurante.domain.enums.StatusPagamento;
import dev.alexcarvalho.restaurante.domain.enums.StatusPedido;
import dev.alexcarvalho.restaurante.dto.PagamentoRequest;
import dev.alexcarvalho.restaurante.dto.PagamentoResponse;
import dev.alexcarvalho.restaurante.exception.RegraNegocioException;
import dev.alexcarvalho.restaurante.repository.FechamentoContaRepository;
import dev.alexcarvalho.restaurante.repository.MesaRepository;
import dev.alexcarvalho.restaurante.repository.PagamentoRepository;
import dev.alexcarvalho.restaurante.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagamentoService {

    private final PagamentoClient pagamentoClient;
    private final FechamentoContaRepository fechamentoContaRepository;
    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final PagamentoRepository pagamentoRepository;

    public PagamentoService(PagamentoClient pagamentoClient, FechamentoContaRepository fechamentoContaRepository,
                            PedidoRepository pedidoRepository, MesaRepository mesaRepository, PagamentoRepository pagamentoRepository) {
        this.pagamentoClient = pagamentoClient;
        this.fechamentoContaRepository = fechamentoContaRepository;
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.pagamentoRepository = pagamentoRepository;
    }
    //Tenho que verificar se essa anotação vai funcionar
    //Se der tudo errado, o Transacional faz o rollback para mim...
    @Transactional
    public void pagar(Long pedidoId, String formaPagamento) {
        FechamentoConta fechamento = fechamentoContaRepository.findById(pedidoId)
                .orElseThrow(() -> new RegraNegocioException("Conta não encontrada."));

        PagamentoResponse response = pagamentoClient.processar(
                new PagamentoRequest(
                        fechamento.getTotal().doubleValue(),
                        formaPagamento
                )
        );

        if("APROVADO".equals(response.status())){
            Pedido pedido = fechamento.getPedido();
            pedido.setStatus(StatusPedido.FECHADO);

            Mesa mesa = pedido.getMesa();
            mesa.setStatus(StatusMesa.LIVRE);

            Pagamento pagamento = new Pagamento();
            pagamento.setPedido(pedido);
            pagamento.setFormaPagamento(FormaPagamento.valueOf(formaPagamento));
            pagamento.setStatus(StatusPagamento.APROVADO);
            pagamento.setValor(fechamento.getTotal());
            pagamento.setDataPagamento(fechamento.getDataFechamento());

            pedidoRepository.save(pedido);
            mesaRepository.save(mesa);
            pagamentoRepository.save(pagamento);
        }
    }
}
