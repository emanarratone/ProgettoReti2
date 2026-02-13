package com.uniupo.pagamento.service;

import com.uniupo.pagamento.model.Pagamento;
import com.uniupo.pagamento.repository.PagamentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PagamentoService {

    private final PagamentoRepository repository;

    public PagamentoService(PagamentoRepository repository) {
        this.repository = repository;
    }

    public List<Pagamento> getAll() {
        return repository.findAll();
    }

    public Optional<Pagamento> getById(Integer id) {
        return repository.findById(id);
    }

    public List<Pagamento> getByBiglietto(Integer idBiglietto) {
        return repository.findByIdBiglietto(idBiglietto);
    }

    public List<Pagamento> getUnpaid() {
        return repository.findByStato("NON_PAGATO");
    }

    public List<Pagamento> getPaid() {
        return repository.findByStato("PAGATO");
    }

    @Transactional
    public Pagamento create(Pagamento pagamento) {
        return repository.save(pagamento);
    }

    @Transactional
    public Optional<Pagamento> markAsPaid(Integer id) {
        return repository.findById(id)
                .map(p -> {
                    p.setStato("PAGATO"); // Corretto setter
                    return repository.save(p);
                });
    }

    @Transactional
    public Optional<Pagamento> update(Integer id, Pagamento pagamento) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setPrezzo(pagamento.getPrezzo());
                    existing.setStato(pagamento.getStato()); // Corretto setter
                    existing.setCaselloOut(pagamento.getCaselloOut());
                    existing.setTimestampOut(pagamento.getTimestampOut());
                    return repository.save(existing);
                });
    }

    @Transactional
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}