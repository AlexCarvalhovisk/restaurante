package dev.alexcarvalho.restaurante.repository;

import dev.alexcarvalho.restaurante.domain.entity.FechamentoConta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FechamentoContaRepository extends JpaRepository<FechamentoConta, Long> {
}
