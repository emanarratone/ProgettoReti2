package com.uniupo.pagamento.repository;

import com.uniupo.pagamento.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {
    List<Pagamento> findByIdBiglietto(Integer idBiglietto);
    List<Pagamento> findByPagato(Boolean pagato);
}
