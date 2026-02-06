package com.uniupo.regione.service;

import com.uniupo.regione.model.Regione;
import com.uniupo.regione.model.dto.RegioneCreateUpdateDTO;
import com.uniupo.regione.model.dto.RegioneDTO;
import com.uniupo.regione.repository.RegioneRepository;
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
class RegioneServiceTest {

    @Mock
    private RegioneRepository regioneRepository;

    @InjectMocks
    private RegioneService regioneService;

    private Regione regione1;
    private Regione regione2;
    private RegioneCreateUpdateDTO createUpdateDTO;

    @BeforeEach
    void setUp() {
        regione1 = new Regione(1, "Piemonte");
        regione2 = new Regione(2, "Lombardia");
        
        createUpdateDTO = new RegioneCreateUpdateDTO();
        createUpdateDTO.setNome("Veneto");
    }

    @Test
    void testGetAll() {
        // Given
        List<Regione> regioni = Arrays.asList(regione1, regione2);
        when(regioneRepository.findAll()).thenReturn(regioni);

        // When
        List<RegioneDTO> result = regioneService.getAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getNome()).isEqualTo("Piemonte");
        assertThat(result.get(1).getId()).isEqualTo(2);
        assertThat(result.get(1).getNome()).isEqualTo("Lombardia");

        verify(regioneRepository, times(1)).findAll();
    }

    @Test
    void testGetAll_EmptyList() {
        // Given
        when(regioneRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<RegioneDTO> result = regioneService.getAll();

        // Then
        assertThat(result).isEmpty();
        verify(regioneRepository, times(1)).findAll();
    }

    @Test
    void testCreate() {
        // Given
        Regione savedRegione = new Regione(3, "Veneto");
        when(regioneRepository.save(any(Regione.class))).thenReturn(savedRegione);

        // When
        RegioneDTO result = regioneService.create(createUpdateDTO);

        // Then
        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getNome()).isEqualTo("Veneto");

        verify(regioneRepository, times(1)).save(any(Regione.class));
    }

    @Test
    void testUpdate_Success() {
        // Given
        Integer id = 1;
        RegioneCreateUpdateDTO updateDTO = new RegioneCreateUpdateDTO();
        updateDTO.setNome("Piemonte Aggiornato");
        
        when(regioneRepository.findById(id)).thenReturn(Optional.of(regione1));
        
        Regione updatedRegione = new Regione(1, "Piemonte Aggiornato");
        when(regioneRepository.save(any(Regione.class))).thenReturn(updatedRegione);

        // When
        RegioneDTO result = regioneService.update(id, updateDTO);

        // Then
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getNome()).isEqualTo("Piemonte Aggiornato");

        verify(regioneRepository, times(1)).findById(id);
        verify(regioneRepository, times(1)).save(regione1);
    }

    @Test
    void testUpdate_RegioneNotFound() {
        // Given
        Integer id = 999;
        when(regioneRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> regioneService.update(id, createUpdateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Regione non trovata");

        verify(regioneRepository, times(1)).findById(id);
        verify(regioneRepository, never()).save(any(Regione.class));
    }

    @Test
    void testDelete() {
        // Given
        Integer id = 1;

        // When
        regioneService.delete(id);

        // Then
        verify(regioneRepository, times(1)).deleteById(id);
    }

    @Test
    void testSearch() {
        // Given
        String query = "Pie";
        List<Regione> regioni = Arrays.asList(regione1);
        when(regioneRepository.findTop20ByNomeContainingIgnoreCaseOrderByNomeAsc(query)).thenReturn(regioni);

        // When
        List<RegioneDTO> result = regioneService.search(query);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getNome()).isEqualTo("Piemonte");

        verify(regioneRepository, times(1)).findTop20ByNomeContainingIgnoreCaseOrderByNomeAsc(query);
    }

    @Test
    void testSearch_MultipleResults() {
        // Given
        String query = "o"; // matches both "Piemonte" and "Lombardia"
        List<Regione> regioni = Arrays.asList(regione1, regione2);
        when(regioneRepository.findTop20ByNomeContainingIgnoreCaseOrderByNomeAsc(query)).thenReturn(regioni);

        // When
        List<RegioneDTO> result = regioneService.search(query);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNome()).isEqualTo("Piemonte");
        assertThat(result.get(1).getNome()).isEqualTo("Lombardia");

        verify(regioneRepository, times(1)).findTop20ByNomeContainingIgnoreCaseOrderByNomeAsc(query);
    }

    @Test
    void testSearch_NoResults() {
        // Given
        String query = "NonEsiste";
        when(regioneRepository.findTop20ByNomeContainingIgnoreCaseOrderByNomeAsc(query)).thenReturn(Arrays.asList());

        // When
        List<RegioneDTO> result = regioneService.search(query);

        // Then
        assertThat(result).isEmpty();
        verify(regioneRepository, times(1)).findTop20ByNomeContainingIgnoreCaseOrderByNomeAsc(query);
    }

    @Test
    void testSearch_CaseInsensitive() {
        // Given
        String query = "PIEMONTE"; // uppercase
        List<Regione> regioni = Arrays.asList(regione1);
        when(regioneRepository.findTop20ByNomeContainingIgnoreCaseOrderByNomeAsc(query)).thenReturn(regioni);

        // When
        List<RegioneDTO> result = regioneService.search(query);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("Piemonte");

        verify(regioneRepository, times(1)).findTop20ByNomeContainingIgnoreCaseOrderByNomeAsc(query);
    }
}