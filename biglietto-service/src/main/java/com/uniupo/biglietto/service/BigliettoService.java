package com.uniupo.biglietto.service;

import com.uniupo.biglietto.controller.BigliettoController;
import com.uniupo.biglietto.model.Biglietto;
import com.uniupo.biglietto.repository.BigliettoRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BigliettoService {

    private final BigliettoRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(BigliettoService.class);


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

    public Map<String, Object> getTrafficStats30d() {
        try {
            List<Object[]> rawStats = repository.getTrafficStatsRaw();
            if (rawStats.isEmpty()) {
                return Map.of("media", 0, "totale_30d", 0);
            }

            Object[] stats = rawStats.get(0);  // Prima riga
            Number totaleNum = (Number) stats[0];
            Number giorniNum = (Number) stats[1];

            int totale = totaleNum.intValue();
            int giorni = giorniNum.intValue();
            int media = totale / Math.max(giorni, 1);

            logger.info("📊 OK: {} / {} = {}", totale, giorni, media);
            return Map.of("media", media, "totale_30d", totale);
        } catch (Exception e) {
            logger.error("❌ Errore: {}", e.getMessage());
            return Map.of("media", 0, "totale_30d", 0);
        }
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
