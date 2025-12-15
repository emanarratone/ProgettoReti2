package com.example.veicolo.service;

import com.example.veicolo.model.Veicolo;
import com.example.veicolo.repository.VeicoloRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VeicoloService {

    private final VeicoloRepository repository;

    public VeicoloService(VeicoloRepository repository) {
        this.repository = repository;
    }

    public List<Veicolo> getAll() {
        return repository.findAll();
    }

    public Optional<Veicolo> getByTarga(String targa) {
        return repository.findById(targa);
    }

    public List<Veicolo> getByTipo(Veicolo.TipoVeicolo tipo) {
        return repository.findByTipoVeicolo(tipo);
    }

    public boolean exists(String targa) {
        return repository.existsById(targa);
    }

    @Transactional
    public Veicolo create(Veicolo veicolo) {
        return repository.save(veicolo);
    }

    @Transactional
    public Optional<Veicolo> update(String targa, Veicolo veicolo) {
        return repository.findById(targa)
                .map(existing -> {
                    existing.setTipoVeicolo(veicolo.getTipoVeicolo());
                    return repository.save(existing);
                });
    }

    @Transactional
    public void delete(String targa) {
        repository.deleteById(targa);
    }

    public long countByTipo(Veicolo.TipoVeicolo tipo) {
        return repository.findByTipoVeicolo(tipo).size();
    }
}
