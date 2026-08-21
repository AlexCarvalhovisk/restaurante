package dev.alexcarvalho.restaurante.repository;

import dev.alexcarvalho.restaurante.domain.entity.PedidoItem;
import dev.alexcarvalho.restaurante.domain.enums.StatusItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {

    List<PedidoItem> findByPedidoId(Long pedidoId);

    List<PedidoItem> findByStatusOrderByIdAsc(StatusItemPedido status);
}
