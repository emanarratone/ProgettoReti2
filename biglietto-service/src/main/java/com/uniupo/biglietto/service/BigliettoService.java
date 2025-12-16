package com.uniupo.biglietto.service;

import com.uniupo.biglietto.model.Biglietto;
import com.uniupo.biglietto.repository.BigliettoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BigliettoService {

    private final BigliettoRepository repository;

    public BigliettoService(BigliettoRepository repository) {
        this.repository = repository;
    }

    public List<Biglietto> getAll() {
        return repository.findAll();
    }

    public Optional<Biglietto> getById(Integer id) {
        return repository.findById(id);
    }

    public List<Biglietto> getByTarga(String targa) {
        return repository.findByTarga(targa);
    }

    @Transactional
    public Biglietto create(Biglietto biglietto) {
        return repository.save(biglietto);
    }

    @Transactional
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
