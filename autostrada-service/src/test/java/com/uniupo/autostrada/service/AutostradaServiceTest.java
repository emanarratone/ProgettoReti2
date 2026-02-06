package com.uniupo.autostrada.service;

import com.uniupo.autostrada.model.Autostrada;
import com.uniupo.autostrada.model.dto.AutostradaCreateUpdateDTO;
import com.uniupo.autostrada.model.dto.AutostradaDTO;
import com.uniupo.autostrada.repository.AutostradaRepository;
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
class AutostradaServiceTest {

    @Mock
    private AutostradaRepository autostradarepository;

    @InjectMocks
    private AutostradaService autostradaService;

    private Autostrada autostrada1;
    private Autostrada autostrada2;
    private AutostradaCreateUpdateDTO createUpdateDTO;

    @BeforeEach
    void setUp() {
        autostrada1 = new Autostrada(1, "A1", 1);
        autostrada2 = new Autostrada(2, "A4", 2);
        
        createUpdateDTO = new AutostradaCreateUpdateDTO();
        createUpdateDTO.setSigla("A14");
        createUpdateDTO.setIdRegione(3);
    }

    @Test
    void testGetAll() {
        // Given
        List<Autostrada> autostrade = Arrays.asList(autostrada1, autostrada2);
        when(autostradarepository.findAll()).thenReturn(autostrade);

        // When
        List<AutostradaDTO> result = autostradaService.getAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getSigla()).isEqualTo("A1");
        assertThat(result.get(0).getIdRegione()).isEqualTo(1);
        assertThat(result.get(1).getId()).isEqualTo(2);
        assertThat(result.get(1).getSigla()).isEqualTo("A4");
        assertThat(result.get(1).getIdRegione()).isEqualTo(2);

        verify(autostradarepository, times(1)).findAll();
    }

    @Test
    void testGetAll_EmptyList() {
        // Given
        when(autostradarepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<AutostradaDTO> result = autostradaService.getAll();

        // Then
        assertThat(result).isEmpty();
        verify(autostradarepository, times(1)).findAll();
    }

    @Test
    void testGetByRegion() {
        // Given
        Integer idRegione = 1;
        List<Autostrada> autostrade = Arrays.asList(autostrada1);
        when(autostradarepository.findByIdRegioneOrderBySiglaAsc(idRegione)).thenReturn(autostrade);

        // When
        List<AutostradaDTO> result = autostradaService.getByRegion(idRegione);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getSigla()).isEqualTo("A1");
        assertThat(result.get(0).getIdRegione()).isEqualTo(1);

        verify(autostradarepository, times(1)).findByIdRegioneOrderBySiglaAsc(idRegione);
    }

    @Test
    void testGetByRegion_NoResults() {
        // Given
        Integer idRegione = 999;
        when(autostradarepository.findByIdRegioneOrderBySiglaAsc(idRegione)).thenReturn(Arrays.asList());

        // When
        List<AutostradaDTO> result = autostradaService.getByRegion(idRegione);

        // Then
        assertThat(result).isEmpty();
        verify(autostradarepository, times(1)).findByIdRegioneOrderBySiglaAsc(idRegione);
    }

    @Test
    void testCreate() {
        // Given
        Autostrada newAutostrada = new Autostrada("A14", 3);
        Autostrada savedAutostrada = new Autostrada(3, "A14", 3);
        when(autostradarepository.save(any(Autostrada.class))).thenReturn(savedAutostrada);

        // When
        AutostradaDTO result = autostradaService.create(createUpdateDTO);

        // Then
        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getSigla()).isEqualTo("A14");
        assertThat(result.getIdRegione()).isEqualTo(3);

        verify(autostradarepository, times(1)).save(any(Autostrada.class));
    }

    @Test
    void testUpdate_Success() {
        // Given
        Integer id = 1;
        Autostrada existingAutostrada = new Autostrada(1, "A1", 1);
        Autostrada updatedAutostrada = new Autostrada(1, "A14", 3);
        
        when(autostradarepository.findById(id)).thenReturn(Optional.of(existingAutostrada));
        when(autostradarepository.save(any(Autostrada.class))).thenReturn(updatedAutostrada);

        // When
        AutostradaDTO result = autostradaService.update(id, createUpdateDTO);

        // Then
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getSigla()).isEqualTo("A14");
        assertThat(result.getIdRegione()).isEqualTo(3);

        verify(autostradarepository, times(1)).findById(id);
        verify(autostradarepository, times(1)).save(existingAutostrada);
    }

    @Test
    void testUpdate_AutostradaNotFound() {
        // Given
        Integer id = 999;
        when(autostradarepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> autostradaService.update(id, createUpdateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Autostrada non trovata");

        verify(autostradarepository, times(1)).findById(id);
        verify(autostradarepository, never()).save(any(Autostrada.class));
    }

    @Test
    void testDelete() {
        // Given
        Integer id = 1;

        // When
        autostradaService.delete(id);

        // Then
        verify(autostradarepository, times(1)).deleteById(id);
    }

    @Test
    void testSearch() {
        // Given
        String query = "A1";
        List<Autostrada> autostrade = Arrays.asList(autostrada1);
        when(autostradarepository.findAutostradasBySiglaOrderBySiglaAsc(query)).thenReturn(autostrade);

        // When
        List<AutostradaDTO> result = autostradaService.search(query);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getSigla()).isEqualTo("A1");
        assertThat(result.get(0).getIdRegione()).isEqualTo(1);

        verify(autostradarepository, times(1)).findAutostradasBySiglaOrderBySiglaAsc(query);
    }

    @Test
    void testSearch_NoResults() {
        // Given
        String query = "X99";
        when(autostradarepository.findAutostradasBySiglaOrderBySiglaAsc(query)).thenReturn(Arrays.asList());

        // When
        List<AutostradaDTO> result = autostradaService.search(query);

        // Then
        assertThat(result).isEmpty();
        verify(autostradarepository, times(1)).findAutostradasBySiglaOrderBySiglaAsc(query);
    }

    @Test
    void testSearch_MultipleResults() {
        // Given
        String query = "A";
        List<Autostrada> autostrade = Arrays.asList(autostrada1, autostrada2);
        when(autostradarepository.findAutostradasBySiglaOrderBySiglaAsc(query)).thenReturn(autostrade);

        // When
        List<AutostradaDTO> result = autostradaService.search(query);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSigla()).isEqualTo("A1");
        assertThat(result.get(1).getSigla()).isEqualTo("A4");

        verify(autostradarepository, times(1)).findAutostradasBySiglaOrderBySiglaAsc(query);
    }
}