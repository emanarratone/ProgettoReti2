package com.uniupo.dispositivi.service;

import com.uniupo.dispositivi.model.Dispositivo;
import com.uniupo.dispositivi.model.Sbarra;
import com.uniupo.dispositivi.model.Telecamera;
import com.uniupo.dispositivi.model.Totem;
import com.uniupo.dispositivi.repository.DispositivoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DispositiviService {

    private final DispositivoRepository repository;

    public DispositiviService(DispositivoRepository repository) {
        this.repository = repository;
    }

    public List<Dispositivo> getAll() {
        return repository.findAll();
    }

    public Optional<Dispositivo> getById(Integer id) {
        return repository.findById(id);
    }

    public List<Dispositivo> getByCasello(Integer casello) {
        return repository.findByCasello(casello);
    }

    public List<Dispositivo> getByCorsia(Integer corsia) {
        return repository.findByCorsia(corsia);
    }

    public List<Dispositivo> getByCaselloAndCorsia(Integer casello, Integer corsia) {
        return repository.findByCaselloAndCorsia(casello, corsia);
    }

    @Transactional
    public Dispositivo createForLane(Integer casello, Integer corsia, String tipo, String stato) {
        boolean statusBool = "ATTIVO".equalsIgnoreCase(stato) || "true".equalsIgnoreCase(stato);
        Dispositivo d;
        if (tipo == null) tipo = "DISPLAY";
        switch (tipo.toUpperCase()) {
            case "Sbarra":
            case "SBARRA":
            case "SBARRE":
                d = new Sbarra(statusBool, corsia, casello);
                break;
            case "TELECAMERA":
            case "TELECAM":
            case "CAM":
                d = new Telecamera(statusBool, corsia, casello);
                break;
            case "TOTEM":
            case "SENSORE":
                d = new Totem(statusBool, corsia, casello);
                break;
            default:
                // default to Totem if unknown
                d = new Totem(statusBool, corsia, casello);
        }
        return repository.save(d);
    }
    public List<Sbarra> getSbarre() {
        return repository.findAll().stream()
                .filter(d -> d instanceof Sbarra)
                .map(d -> (Sbarra) d)
                .collect(Collectors.toList());
    }

    public List<Telecamera> getTelecamere() {
        return repository.findAll().stream()
                .filter(d -> d instanceof Telecamera)
                .map(d -> (Telecamera) d)
                .collect(Collectors.toList());
    }

    public List<Totem> getTotem() {
        return repository.findAll().stream()
                .filter(d -> d instanceof Totem)
                .map(d -> (Totem) d)
                .collect(Collectors.toList());
    }

    public List<Dispositivo> getActive() {
        return repository.findByStatus(true);
    }

    public List<Dispositivo> getInactive() {
        return repository.findByStatus(false);
    }

    public Dispositivo create(Dispositivo dispositivo) {
        return repository.save(dispositivo);
    }

    public Optional<Dispositivo> update(Integer id, Dispositivo dispositivo) {
        return repository.findById(id).map(d -> {
            d.setCasello(dispositivo.getCasello());
            d.setCorsia(dispositivo.getCorsia());
            d.setStatus(dispositivo.getStatusBoolean());
            return repository.save(d);
        });
    }

    public void updateStatus(Integer id, Boolean status) {
        repository.findById(id).ifPresent(d -> {
            d.setStatus(status);
            repository.save(d);
        });
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
