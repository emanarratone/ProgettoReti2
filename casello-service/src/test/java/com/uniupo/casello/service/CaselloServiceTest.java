package com.uniupo.casello.service;

import com.uniupo.casello.model.Casello;
import com.uniupo.casello.model.dto.CaselloDTO;
import com.uniupo.casello.repository.CaselloRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaselloServiceTest {

    @Mock
    private CaselloRepository caselloRepository;

    @InjectMocks
    private CaselloService caselloService;

    private Casello casello1;
    private Casello casello2;
    private CaselloDTO caselloDTO;

    @BeforeEach
    void setUp() {
        casello1 = new Casello(1, "Milano Nord", 1, false, 130);
        casello2 = new Casello(2, "Milano Sud", 1, true, 110);
        
        caselloDTO = new CaselloDTO(null, "Torino Est", 2, false, 120);
    }

    @Test
    void testGetAll() {
        // Given
        List<Casello> caselli = Arrays.asList(casello1, casello2);
        when(caselloRepository.findAll()).thenReturn(caselli);

        // When
        List<CaselloDTO> result = caselloService.getAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIdCasello()).isEqualTo(1);
        assertThat(result.get(0).getSigla()).isEqualTo("Milano Nord");
        assertThat(result.get(0).isClosed()).isFalse();
        assertThat(result.get(1).getIdCasello()).isEqualTo(2);
        assertThat(result.get(1).getSigla()).isEqualTo("Milano Sud");
        assertThat(result.get(1).isClosed()).isTrue();

        verify(caselloRepository, times(1)).findAll();
    }

    @Test
    void testGetAll_EmptyList() {
        // Given
        when(caselloRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<CaselloDTO> result = caselloService.getAll();

        // Then
        assertThat(result).isEmpty();
        verify(caselloRepository, times(1)).findAll();
    }

    @Test
    void testCreate() {
        // Given
        Casello savedCasello = new Casello(3, "Torino Est", 2, false, 120);
        when(caselloRepository.save(any(Casello.class))).thenReturn(savedCasello);

        // When
        CaselloDTO result = caselloService.create(caselloDTO);

        // Then
        assertThat(result.getIdCasello()).isEqualTo(3);
        assertThat(result.getSigla()).isEqualTo("Torino Est");
        assertThat(result.getIdAutostrada()).isEqualTo(2);
        assertThat(result.isClosed()).isFalse();
        assertThat(result.getLimite()).isEqualTo(120);

        verify(caselloRepository, times(1)).save(any(Casello.class));
    }

    @Test
    void testUpdate_Success() {
        // Given
        Integer id = 1;
        CaselloDTO updateDTO = new CaselloDTO(1, "Milano Centro", 1, true, 100);
        
        when(caselloRepository.findById(id)).thenReturn(Optional.of(casello1));
        
        Casello updatedCasello = new Casello(1, "Milano Centro", 1, true, 100);
        when(caselloRepository.save(any(Casello.class))).thenReturn(updatedCasello);

        // When
        CaselloDTO result = caselloService.update(id, updateDTO);

        // Then
        assertThat(result.getIdCasello()).isEqualTo(1);
        assertThat(result.getSigla()).isEqualTo("Milano Centro");
        assertThat(result.isClosed()).isTrue();
        assertThat(result.getLimite()).isEqualTo(100);

        verify(caselloRepository, times(1)).findById(id);
        verify(caselloRepository, times(1)).save(casello1);
    }

    @Test
    void testUpdate_CaselloNotFound() {
        // Given
        Integer id = 999;
        when(caselloRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> caselloService.update(id, caselloDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Casello non trovato");

        verify(caselloRepository, times(1)).findById(id);
        verify(caselloRepository, never()).save(any(Casello.class));
    }

    @Test
    void testDelete() {
        // Given
        Integer id = 1;

        // When
        caselloService.delete(id);

        // Then
        verify(caselloRepository, times(1)).deleteById(id);
    }

    @Test
    void testSearch() {
        // Given
        String query = "Milano";
        List<Casello> caselli = Arrays.asList(casello1, casello2);
        when(caselloRepository.findCaselloBySiglaOrderBySiglaAsc(query)).thenReturn(caselli);

        // When
        List<CaselloDTO> result = caselloService.search(query);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSigla()).isEqualTo("Milano Nord");
        assertThat(result.get(1).getSigla()).isEqualTo("Milano Sud");

        verify(caselloRepository, times(1)).findCaselloBySiglaOrderBySiglaAsc(query);
    }

    @Test
    void testSearch_NoResults() {
        // Given
        String query = "NonEsiste";
        when(caselloRepository.findCaselloBySiglaOrderBySiglaAsc(query)).thenReturn(Arrays.asList());

        // When
        List<CaselloDTO> result = caselloService.search(query);

        // Then
        assertThat(result).isEmpty();
        verify(caselloRepository, times(1)).findCaselloBySiglaOrderBySiglaAsc(query);
    }

    @Test
    void testGetByAutostrada() {
        // Given
        Integer idAutostrada = 1;
        List<Casello> caselli = Arrays.asList(casello1, casello2);
        when(caselloRepository.findByIdAutostradaOrderBySiglaAsc(idAutostrada)).thenReturn(caselli);

        // When
        List<CaselloDTO> result = caselloService.getByAutostrada(idAutostrada);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIdAutostrada()).isEqualTo(1);
        assertThat(result.get(1).getIdAutostrada()).isEqualTo(1);

        verify(caselloRepository, times(1)).findByIdAutostradaOrderBySiglaAsc(idAutostrada);
    }

    @Test
    void testCreateForHighway() {
        // Given
        Integer idAutostrada = 2;
        String sigla = "Torino Ovest";
        Integer limite = 140;
        Boolean chiuso = false;
        
        Casello savedCasello = new Casello(4, sigla, idAutostrada, chiuso, limite);
        when(caselloRepository.save(any(Casello.class))).thenReturn(savedCasello);

        // When
        CaselloDTO result = caselloService.createForHighway(idAutostrada, sigla, limite, chiuso);

        // Then
        assertThat(result.getIdCasello()).isEqualTo(4);
        assertThat(result.getSigla()).isEqualTo(sigla);
        assertThat(result.getIdAutostrada()).isEqualTo(idAutostrada);
        assertThat(result.isClosed()).isFalse();
        assertThat(result.getLimite()).isEqualTo(limite);

        verify(caselloRepository, times(1)).save(any(Casello.class));
    }

    @Test
    void testGetById_Found() {
        // Given
        Integer id = 1;
        when(caselloRepository.findById(id)).thenReturn(Optional.of(casello1));

        // When
        Optional<CaselloDTO> result = caselloService.getById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getIdCasello()).isEqualTo(1);
        assertThat(result.get().getSigla()).isEqualTo("Milano Nord");

        verify(caselloRepository, times(1)).findById(id);
    }

    @Test
    void testGetById_NotFound() {
        // Given
        Integer id = 999;
        when(caselloRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<CaselloDTO> result = caselloService.getById(id);

        // Then
        assertThat(result).isEmpty();
        verify(caselloRepository, times(1)).findById(id);
    }

    @Test
    void testUpdateFromDTO() {
        // Given
        Integer id = 1;
        CaselloDTO updateDTO = new CaselloDTO(1, "Updated Casello", 1, true, 90);
        
        when(caselloRepository.findById(id)).thenReturn(Optional.of(casello1));
        
        Casello updatedCasello = new Casello(1, "Updated Casello", 1, true, 90);
        when(caselloRepository.save(any(Casello.class))).thenReturn(updatedCasello);

        // When
        CaselloDTO result = caselloService.updateFromDTO(id, updateDTO);

        // Then
        assertThat(result.getSigla()).isEqualTo("Updated Casello");
        assertThat(result.isClosed()).isTrue();
        assertThat(result.getLimite()).isEqualTo(90);

        verify(caselloRepository, times(1)).findById(id);
        verify(caselloRepository, times(1)).save(casello1);
    }
}