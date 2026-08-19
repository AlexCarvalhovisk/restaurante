package dev.alexcarvalho.restaurante.service;

import dev.alexcarvalho.restaurante.domain.entity.Mesa;
import dev.alexcarvalho.restaurante.domain.entity.Pedido;
import dev.alexcarvalho.restaurante.domain.entity.PedidoItem;
import dev.alexcarvalho.restaurante.domain.entity.Produto;
import dev.alexcarvalho.restaurante.domain.enums.StatusItemPedido;
import dev.alexcarvalho.restaurante.domain.enums.StatusMesa;
import dev.alexcarvalho.restaurante.domain.enums.StatusPedido;
import dev.alexcarvalho.restaurante.dto.PedidoItemRequest;
import dev.alexcarvalho.restaurante.dto.PedidoItemResponse;
import dev.alexcarvalho.restaurante.dto.PedidoRequest;
import dev.alexcarvalho.restaurante.dto.PedidoResponse;
import dev.alexcarvalho.restaurante.exception.RegraNegocioException;
import dev.alexcarvalho.restaurante.repository.MesaRepository;
import dev.alexcarvalho.restaurante.repository.PedidoItemRepository;
import dev.alexcarvalho.restaurante.repository.PedidoRepository;
import dev.alexcarvalho.restaurante.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoItemRepository pedidoItemRepository;

    public PedidoService(PedidoRepository pedidoRepository, MesaRepository mesaRepository, ProdutoRepository produtoRepository, PedidoItemRepository pedidoItemRepository) {
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
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
        Pedido pedido = buscarPedidoPorId(id);
        return PedidoResponse.fromEntity(pedido);
    }

    public PedidoItemResponse adicionarItem(Long pedidoId, PedidoItemRequest request) {
        Pedido pedido = buscarPedidoPorId(pedidoId);
        if(pedido.getStatus() != StatusPedido.ABERTO){
            throw new RegraNegocioException("Só é possível adicionar item em pedidos abertos.");
        }

        Produto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado"));

        if(!produto.getDisponivel()){
            throw new RegraNegocioException("Produto indisponível no cardápio.");
        }

        if(request.quantidade() == null || request.quantidade() <= 0){
            throw new RegraNegocioException("A quantidade deve ser maior que zero.");
        }

        PedidoItem pedidoItem = new PedidoItem();
        pedidoItem.setPedido(pedido);
        pedidoItem.setProduto(produto);
        pedidoItem.setQuantidade(request.quantidade());
        pedidoItem.setPrecoUnitario(produto.getPreco());
        pedidoItem.setObservacao(request.observacao());
        pedidoItem.setStatus(StatusItemPedido.PENDENTE);
        PedidoItem itemSalvo = pedidoItemRepository.save(pedidoItem);
        return PedidoItemResponse.fromEntity(itemSalvo);
    }

    public List<PedidoItemResponse> listarItens(Long pedidoId) {
        buscarPedidoPorId(pedidoId);

        return pedidoItemRepository.findByPedidoId(pedidoId).stream().map(PedidoItemResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private Pedido buscarPedidoPorId(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado"));
    }
}
