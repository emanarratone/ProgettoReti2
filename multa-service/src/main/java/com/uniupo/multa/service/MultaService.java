package com.uniupo.multa.service;

import com.uniupo.multa.model.Multa;
import com.uniupo.multa.repository.MultaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MultaService {

    private final MultaRepository repository;

    public MultaService(MultaRepository repository) {
        this.repository = repository;
    }

    public List<Multa> getAll() {
        return repository.findAll();
    }

    public Optional<Multa> getById(Integer id) {
        return repository.findById(id);
    }

    public List<Multa> getByTarga(String targa) {
        return repository.findByTarga(targa);
    }

    public List<Multa> getUnpaid() {
        return repository.findByPagato(false);
    }

    public List<Multa> getPaid() {
        return repository.findByPagato(true);
    }

    public Double getTotalUnpaidByTarga(String targa) {
        return repository.findByTarga(targa).stream()
                .filter(m -> !m.getPagato())
                .mapToDouble(Multa::getImporto)
                .sum();
    }

    @Transactional
    public Multa create(Multa multa) {
        return repository.save(multa);
    }

    @Transactional
    public Optional<Multa> markAsPaid(Integer id) {
        return repository.findById(id)
                .map(m -> {
                    m.setPagato(true);
                    return repository.save(m);
                });
    }

    @Transactional
    public Optional<Multa> update(Integer id, Multa multa) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setImporto(multa.getImporto());
                    existing.setPagato(multa.getPagato());
                    existing.setTarga(multa.getTarga());
                    return repository.save(existing);
                });
    }

    @Transactional
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
