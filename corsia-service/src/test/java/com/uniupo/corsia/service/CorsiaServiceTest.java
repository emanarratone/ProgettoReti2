package com.uniupo.corsia.service;

import com.uniupo.corsia.model.Corsia;
import com.uniupo.corsia.model.dto.CorsiaDTO;
import com.uniupo.corsia.repository.CorsiaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorsiaServiceTest {

    @Mock
    private CorsiaRepository corsiaRepository;

    @InjectMocks
    private CorsiaService corsiaService;

    private Corsia corsia1;
    private Corsia corsia2;
    private CorsiaDTO corsiaDTO;

    @BeforeEach
    void setUp() {
        corsia1 = new Corsia(1, 1, Corsia.Verso.ENTRATA, Corsia.Tipo.EMERGENZA);
        corsia2 = new Corsia(1, 2, Corsia.Verso.USCITA, Corsia.Tipo.MANUALE);
        
        corsiaDTO = new CorsiaDTO(2, 1, Corsia.Verso.ENTRATA, Corsia.Tipo.TELEPASS, false);
    }

    @Test
    void testGetAll() {
        // Given
        List<Corsia> corsie = Arrays.asList(corsia1, corsia2);
        when(corsiaRepository.findAll()).thenReturn(corsie);

        // When
        List<CorsiaDTO> result = corsiaService.getAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCasello()).isEqualTo(1);
        assertThat(result.get(0).getNumCorsia()).isEqualTo(1);
        assertThat(result.get(0).getVerso()).isEqualTo(Corsia.Verso.ENTRATA);
        assertThat(result.get(0).getTipo()).isEqualTo(Corsia.Tipo.EMERGENZA);
        
        assertThat(result.get(1).getCasello()).isEqualTo(1);
        assertThat(result.get(1).getNumCorsia()).isEqualTo(2);
        assertThat(result.get(1).getVerso()).isEqualTo(Corsia.Verso.USCITA);
        assertThat(result.get(1).getTipo()).isEqualTo(Corsia.Tipo.MANUALE);

        verify(corsiaRepository, times(1)).findAll();
    }

    @Test
    void testGetAll_EmptyList() {
        // Given
        when(corsiaRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<CorsiaDTO> result = corsiaService.getAll();

        // Then
        assertThat(result).isEmpty();
        verify(corsiaRepository, times(1)).findAll();
    }

    @Test
    void testCreate() {
        // Given
        Corsia savedCorsia = new Corsia(2, 1, Corsia.Verso.ENTRATA, Corsia.Tipo.TELEPASS);
        when(corsiaRepository.save(any(Corsia.class))).thenReturn(savedCorsia);

        // When
        CorsiaDTO result = corsiaService.create(corsiaDTO);

        // Then
        assertThat(result.getCasello()).isEqualTo(2);
        assertThat(result.getNumCorsia()).isEqualTo(1);
        assertThat(result.getVerso()).isEqualTo(Corsia.Verso.ENTRATA);
        assertThat(result.getTipo()).isEqualTo(Corsia.Tipo.TELEPASS);

        verify(corsiaRepository, times(1)).save(any(Corsia.class));
    }

    @Test
    void testCreateForToll() {
        // Given
        Integer idCasello = 1;
        String versoStr = "ENTRATA";
        String tipoStr = "TELEPASS";
        Boolean chiuso = false;
        
        when(corsiaRepository.findMaxNumCorsiaByCasello(idCasello)).thenReturn(2);
        
        Corsia savedCorsia = new Corsia(1, 3, Corsia.Verso.ENTRATA, Corsia.Tipo.TELEPASS, false);
        when(corsiaRepository.save(any(Corsia.class))).thenReturn(savedCorsia);

        // When
        CorsiaDTO result = corsiaService.createForToll(idCasello, versoStr, tipoStr, chiuso);

        // Then
        assertThat(result.getCasello()).isEqualTo(1);
        assertThat(result.getNumCorsia()).isEqualTo(3);
        assertThat(result.getVerso()).isEqualTo(Corsia.Verso.ENTRATA);
        assertThat(result.getTipo()).isEqualTo(Corsia.Tipo.TELEPASS);
        assertThat(result.getClosed()).isFalse();

        verify(corsiaRepository, times(1)).findMaxNumCorsiaByCasello(idCasello);
        verify(corsiaRepository, times(1)).save(any(Corsia.class));
    }

    @Test
    void testCreateForToll_NullMaxNum() {
        // Given
        Integer idCasello = 1;
        String versoStr = "USCITA";
        String tipoStr = "AUTOMATICA";
        Boolean chiuso = true;
        
        when(corsiaRepository.findMaxNumCorsiaByCasello(idCasello)).thenReturn(null);
        
        Corsia savedCorsia = new Corsia(1, 1, Corsia.Verso.USCITA, Corsia.Tipo.EMERGENZA, true);
        when(corsiaRepository.save(any(Corsia.class))).thenReturn(savedCorsia);

        // When
        CorsiaDTO result = corsiaService.createForToll(idCasello, versoStr, tipoStr, chiuso);

        // Then
        assertThat(result.getNumCorsia()).isEqualTo(1); // 0 + 1
        assertThat(result.getVerso()).isEqualTo(Corsia.Verso.USCITA);
        assertThat(result.getTipo()).isEqualTo(Corsia.Tipo.EMERGENZA);
        assertThat(result.getClosed()).isTrue();

        verify(corsiaRepository, times(1)).findMaxNumCorsiaByCasello(idCasello);
        verify(corsiaRepository, times(1)).save(any(Corsia.class));
    }

    @Test
    void testCreateForToll_InvalidEnumValues() {
        // Given
        Integer idCasello = 1;
        String versoStr = "INVALID_VERSO";
        String tipoStr = "INVALID_TIPO";
        Boolean chiuso = null;
        
        when(corsiaRepository.findMaxNumCorsiaByCasello(idCasello)).thenReturn(0);
        
        Corsia savedCorsia = new Corsia(1, 1, Corsia.Verso.ENTRATA, Corsia.Tipo.MANUALE, false);
        when(corsiaRepository.save(any(Corsia.class))).thenReturn(savedCorsia);

        // When
        CorsiaDTO result = corsiaService.createForToll(idCasello, versoStr, tipoStr, chiuso);

        // Then
        assertThat(result.getVerso()).isEqualTo(Corsia.Verso.ENTRATA); // default
        assertThat(result.getTipo()).isEqualTo(Corsia.Tipo.MANUALE); // default
        assertThat(result.getClosed()).isFalse(); // default

        verify(corsiaRepository, times(1)).save(any(Corsia.class));
    }

    @Test
    void testDeleteByCaselloAndNum() {
        // Given
        Integer casello = 1;
        Integer numCorsia = 2;

        // When
        corsiaService.deleteByCaselloAndNum(casello, numCorsia);

        // Then
        verify(corsiaRepository, times(1)).deleteByCaselloAndNumCorsia(casello, numCorsia);
    }

    @Test
    void testUpdate_Success() {
        // Given
        Integer idCasello = 1;
        Integer numCorsia = 1;
        CorsiaDTO updateDTO = new CorsiaDTO(1, 1, Corsia.Verso.USCITA, Corsia.Tipo.TELEPASS, true);
        
        List<Corsia> corsie = Arrays.asList(corsia1, corsia2);
        when(corsiaRepository.findByCaselloOrderByNumCorsiaAsc(idCasello)).thenReturn(corsie);
        
        Corsia updatedCorsia = new Corsia(1, 1, Corsia.Verso.USCITA, Corsia.Tipo.TELEPASS, true);
        when(corsiaRepository.save(any(Corsia.class))).thenReturn(updatedCorsia);

        // When
        CorsiaDTO result = corsiaService.update(idCasello, numCorsia, updateDTO);

        // Then
        assertThat(result.getVerso()).isEqualTo(Corsia.Verso.USCITA);
        assertThat(result.getTipo()).isEqualTo(Corsia.Tipo.TELEPASS);
        assertThat(result.getClosed()).isTrue();

        verify(corsiaRepository, times(1)).findByCaselloOrderByNumCorsiaAsc(idCasello);
        verify(corsiaRepository, times(1)).save(corsia1);
    }

    @Test
    void testUpdate_CorsiaNotFound() {
        // Given
        Integer idCasello = 1;
        Integer numCorsia = 99;
        CorsiaDTO updateDTO = new CorsiaDTO(1, 99, Corsia.Verso.USCITA, Corsia.Tipo.TELEPASS, true);
        
        List<Corsia> corsie = Arrays.asList(corsia1, corsia2);
        when(corsiaRepository.findByCaselloOrderByNumCorsiaAsc(idCasello)).thenReturn(corsie);

        // When & Then
        assertThatThrownBy(() -> corsiaService.update(idCasello, numCorsia, updateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Corsia non trovata");

        verify(corsiaRepository, times(1)).findByCaselloOrderByNumCorsiaAsc(idCasello);
        verify(corsiaRepository, never()).save(any(Corsia.class));
    }

    @Test
    void testDelete() {
        // Given
        Integer id = 1;

        // When
        corsiaService.delete(id);

        // Then
        verify(corsiaRepository, times(1)).deleteById(id);
    }

    @Test
    void testSearch() {
        // Given
        Integer idCasello = 1;
        List<Corsia> corsie = Arrays.asList(corsia1, corsia2);
        when(corsiaRepository.findByCaselloOrderByNumCorsiaAsc(idCasello)).thenReturn(corsie);

        // When
        List<CorsiaDTO> result = corsiaService.search(idCasello);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCasello()).isEqualTo(1);
        assertThat(result.get(1).getCasello()).isEqualTo(1);

        verify(corsiaRepository, times(1)).findByCaselloOrderByNumCorsiaAsc(idCasello);
    }

    @Test
    void testSearch_NoResults() {
        // Given
        Integer idCasello = 999;
        when(corsiaRepository.findByCaselloOrderByNumCorsiaAsc(idCasello)).thenReturn(Arrays.asList());

        // When
        List<CorsiaDTO> result = corsiaService.search(idCasello);

        // Then
        assertThat(result).isEmpty();
        verify(corsiaRepository, times(1)).findByCaselloOrderByNumCorsiaAsc(idCasello);
    }
}