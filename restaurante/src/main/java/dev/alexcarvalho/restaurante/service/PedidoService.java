package dev.alexcarvalho.restaurante.service;

import dev.alexcarvalho.restaurante.domain.entity.Mesa;
import dev.alexcarvalho.restaurante.domain.entity.Pedido;
import dev.alexcarvalho.restaurante.domain.enums.StatusMesa;
import dev.alexcarvalho.restaurante.domain.enums.StatusPedido;
import dev.alexcarvalho.restaurante.dto.PedidoRequest;
import dev.alexcarvalho.restaurante.dto.PedidoResponse;
import dev.alexcarvalho.restaurante.exception.RegraNegocioException;
import dev.alexcarvalho.restaurante.repository.MesaRepository;
import dev.alexcarvalho.restaurante.repository.PedidoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;

    public PedidoService(PedidoRepository pedidoRepository, MesaRepository mesaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
    }

    //Primeiro passo criei uma função que abre um pedido e verificar a regra de negócio se a mesa existe.
    public PedidoResponse abrirPedido(PedidoRequest pedidoRequest) {
        Mesa mesa = mesaRepository.findById(pedidoRequest.mesaId())
                .orElseThrow(() -> new RegraNegocioException("Mesa inexistente"));

        //Nesse segundo passo testei a regra se a mesa tá livre para depois poder criar o pedido.
        if (mesa.getStatus() != StatusMesa.LIVRE){
            throw new RegraNegocioException("Mesa não está livre para abertura de pedido.");
        }

        //Se passar por tudo e der tudo certo, aqui monta o pedido
        Pedido pedido = new Pedido();
        pedido.setMesa(mesa);
        pedido.setStatus(StatusPedido.ABERTO);
        pedido.setObservacao(pedidoRequest.observacao());

        mesa.setStatus(StatusMesa.OCUPADA);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        mesaRepository.save(mesa);
        return PedidoResponse.fromEntity(pedidoSalvo);
    }

    public Page<PedidoResponse> listar(Pageable pageable) {
        return pedidoRepository.findAll(pageable).map(PedidoResponse::fromEntity);
    }

    public PedidoResponse buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado"));
        return PedidoResponse.fromEntity(pedido);
    }
}
