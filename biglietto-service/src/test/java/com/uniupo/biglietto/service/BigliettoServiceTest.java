package com.uniupo.biglietto.service;

import com.uniupo.biglietto.model.Biglietto;
import com.uniupo.biglietto.repository.BigliettoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BigliettoServiceTest {

    @Mock
    private BigliettoRepository bigliettoRepository;

    @InjectMocks
    private BigliettoService bigliettoService;

    private Biglietto biglietto1;
    private Biglietto biglietto2;

    @BeforeEach
    void setUp() {
        biglietto1 = new Biglietto(1, 101, "AB123CD", Timestamp.from(Instant.now()), 1);
        biglietto2 = new Biglietto(2, 102, "EF456GH", Timestamp.from(Instant.now()), 2);
    }

    @Test
    void testGetAll() {
        // Given
        List<Biglietto> biglietti = Arrays.asList(biglietto1, biglietto2);
        when(bigliettoRepository.findAll()).thenReturn(biglietti);

        // When
        List<Biglietto> result = bigliettoService.getAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIdBiglietto()).isEqualTo(1);
        assertThat(result.get(1).getIdBiglietto()).isEqualTo(2);
        verify(bigliettoRepository, times(1)).findAll();
    }

    @Test
    void testGetAll_EmptyList() {
        // Given
        when(bigliettoRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Biglietto> result = bigliettoService.getAll();

        // Then
        assertThat(result).isEmpty();
        verify(bigliettoRepository, times(1)).findAll();
    }

    @Test
    void testGetById() {
        // Given
        Integer id = 1;
        when(bigliettoRepository.findById(id)).thenReturn(Optional.of(biglietto1));

        // When
        Optional<Biglietto> result = bigliettoService.getById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getIdBiglietto()).isEqualTo(1);
        assertThat(result.get().getTarga()).isEqualTo("AB123CD");
        verify(bigliettoRepository, times(1)).findById(id);
    }

    @Test
    void testGetById_NotFound() {
        // Given
        Integer id = 999;
        when(bigliettoRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Biglietto> result = bigliettoService.getById(id);

        // Then
        assertThat(result).isEmpty();
        verify(bigliettoRepository, times(1)).findById(id);
    }

    @Test
    void testGetByTarga() {
        // Given
        String targa = "AB123CD";
        when(bigliettoRepository.findByTarga(targa)).thenReturn(Arrays.asList(biglietto1));

        // When
        List<Biglietto> result = bigliettoService.getByTarga(targa);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTarga()).isEqualTo(targa);
        verify(bigliettoRepository, times(1)).findByTarga(targa);
    }

    @Test
    void testGetByTarga_NoResults() {
        // Given
        String targa = "XX999XX";
        when(bigliettoRepository.findByTarga(targa)).thenReturn(Arrays.asList());

        // When
        List<Biglietto> result = bigliettoService.getByTarga(targa);

        // Then
        assertThat(result).isEmpty();
        verify(bigliettoRepository, times(1)).findByTarga(targa);
    }

    @Test
    void testGetAverageTraffic_Success() {
        // Given
        Object[] statsData = {50, 5}; // totale, giorni
        List<Object[]> rawStats = Arrays.asList(new Object[][]{statsData});
        when(bigliettoRepository.getTraffiAverage30d()).thenReturn(rawStats);

        // When
        Map<String, Object> result = bigliettoService.getAverageTraffic();

        // Then
        assertThat(result).containsEntry("media", 10); // 50/5
        assertThat(result).containsEntry("totale_30d", 50);
        verify(bigliettoRepository, times(1)).getTraffiAverage30d();
    }

    @Test
    void testGetAverageTraffic_EmptyStats() {
        // Given
        when(bigliettoRepository.getTraffiAverage30d()).thenReturn(Arrays.asList());

        // When
        Map<String, Object> result = bigliettoService.getAverageTraffic();

        // Then
        assertThat(result).containsEntry("media", 0);
        assertThat(result).containsEntry("totale_30d", 0);
        verify(bigliettoRepository, times(1)).getTraffiAverage30d();
    }

    @Test
    void testGetAverageTraffic_Exception() {
        // Given
        when(bigliettoRepository.getTraffiAverage30d()).thenThrow(new RuntimeException("DB Error"));

        // When
        Map<String, Object> result = bigliettoService.getAverageTraffic();

        // Then
        assertThat(result).containsEntry("media", 0);
        assertThat(result).containsEntry("totale_30d", 0);
        verify(bigliettoRepository, times(1)).getTraffiAverage30d();
    }

    @Test
    void testGetTraffic30Days() {
        // Given
        Object[] traffic1 = {"2024-01-01", 25};
        Object[] traffic2 = {"2024-01-02", 30};
        List<Object[]> trafficData = Arrays.asList(traffic1, traffic2);
        when(bigliettoRepository.getTraffic30days()).thenReturn(trafficData);

        // When
        List<Object[]> result = bigliettoService.getTraffic30Days();

        // Then
        assertThat(result).hasSize(2);
        verify(bigliettoRepository, times(1)).getTraffic30days();
    }

    @Test
    void testGetTraffic24Hours() {
        // Given
        Object[] traffic1 = {"10:00", 5};
        Object[] traffic2 = {"11:00", 8};
        List<Object[]> trafficData = Arrays.asList(traffic1, traffic2);
        when(bigliettoRepository.getTraffic24hours()).thenReturn(trafficData);

        // When
        List<Object[]> result = bigliettoService.getTraffic24Hours();

        // Then
        assertThat(result).hasSize(2);
        verify(bigliettoRepository, times(1)).getTraffic24hours();
    }

    @Test
    void testCreate() {
        // Given
        Biglietto newBiglietto = new Biglietto(103, "IJ789KL", Timestamp.from(Instant.now()), 3);
        Biglietto savedBiglietto = new Biglietto(3, 103, "IJ789KL", newBiglietto.getTimestampIn(), 3);
        when(bigliettoRepository.save(newBiglietto)).thenReturn(savedBiglietto);

        // When
        Biglietto result = bigliettoService.create(newBiglietto);

        // Then
        assertThat(result.getIdBiglietto()).isEqualTo(3);
        assertThat(result.getTarga()).isEqualTo("IJ789KL");
        verify(bigliettoRepository, times(1)).save(newBiglietto);
    }

    @Test
    void testDelete() {
        // Given
        Integer id = 1;

        // When
        bigliettoService.delete(id);

        // Then
        verify(bigliettoRepository, times(1)).deleteById(id);
    }
}