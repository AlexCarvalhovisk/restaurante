package dev.alexcarvalho.restaurante.repository;

import dev.alexcarvalho.restaurante.domain.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {
}
