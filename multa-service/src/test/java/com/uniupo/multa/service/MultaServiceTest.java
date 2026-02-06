package com.uniupo.multa.service;

import com.uniupo.multa.model.Multa;
import com.uniupo.multa.repository.MultaRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MultaServiceTest {

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private MultaService multaService;

    private Multa multa1;
    private Multa multa2;
    private Multa multa3;

    @BeforeEach
    void setUp() {
        multa1 = new Multa();
        multa1.setId(1);
        multa1.setTarga("AB123CD");
        multa1.setImporto(100.0);
        multa1.setPagato(false);
        
        multa2 = new Multa();
        multa2.setId(2);
        multa2.setTarga("AB123CD");
        multa2.setImporto(150.0);
        multa2.setPagato(true);
        
        multa3 = new Multa();
        multa3.setId(3);
        multa3.setTarga("EF456GH");
        multa3.setImporto(200.0);
        multa3.setPagato(false);
    }

    @Test
    void testGetAll() {
        // Given
        List<Multa> multe = Arrays.asList(multa1, multa2, multa3);
        when(multaRepository.findAll()).thenReturn(multe);

        // When
        List<Multa> result = multaService.getAll();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(multa1, multa2, multa3);
        verify(multaRepository, times(1)).findAll();
    }

    @Test
    void testGetAll_EmptyList() {
        // Given
        when(multaRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Multa> result = multaService.getAll();

        // Then
        assertThat(result).isEmpty();
        verify(multaRepository, times(1)).findAll();
    }

    @Test
    void testGetById_Found() {
        // Given
        Integer id = 1;
        when(multaRepository.findById(id)).thenReturn(Optional.of(multa1));

        // When
        Optional<Multa> result = multaService.getById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(multa1);
        verify(multaRepository, times(1)).findById(id);
    }

    @Test
    void testGetById_NotFound() {
        // Given
        Integer id = 999;
        when(multaRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Multa> result = multaService.getById(id);

        // Then
        assertThat(result).isEmpty();
        verify(multaRepository, times(1)).findById(id);
    }

    @Test
    void testGetByTarga() {
        // Given
        String targa = "AB123CD";
        List<Multa> multe = Arrays.asList(multa1, multa2);
        when(multaRepository.findByTarga(targa)).thenReturn(multe);

        // When
        List<Multa> result = multaService.getByTarga(targa);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(multa1, multa2);
        verify(multaRepository, times(1)).findByTarga(targa);
    }

    @Test
    void testGetByTarga_NoResults() {
        // Given
        String targa = "XX999XX";
        when(multaRepository.findByTarga(targa)).thenReturn(Arrays.asList());

        // When
        List<Multa> result = multaService.getByTarga(targa);

        // Then
        assertThat(result).isEmpty();
        verify(multaRepository, times(1)).findByTarga(targa);
    }

    @Test
    void testGetUnpaid() {
        // Given
        List<Multa> unpaidMulte = Arrays.asList(multa1, multa3);
        when(multaRepository.findByPagato(false)).thenReturn(unpaidMulte);

        // When
        List<Multa> result = multaService.getUnpaid();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(multa1, multa3);
        verify(multaRepository, times(1)).findByPagato(false);
    }

    @Test
    void testGetPaid() {
        // Given
        List<Multa> paidMulte = Arrays.asList(multa2);
        when(multaRepository.findByPagato(true)).thenReturn(paidMulte);

        // When
        List<Multa> result = multaService.getPaid();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(multa2);
        verify(multaRepository, times(1)).findByPagato(true);
    }

    @Test
    void testGetTotalUnpaidByTarga() {
        // Given
        String targa = "AB123CD";
        List<Multa>TargaMulte = Arrays.asList(multa1, multa2); // 100.0 (unpaid) + 150.0 (paid)
        when(multaRepository.findByTarga(targa)).thenReturn(TargaMulte);

        // When
        Double result = multaService.getTotalUnpaidByTarga(targa);

        // Then
        assertThat(result).isEqualTo(100.0); // only unpaid ones
        verify(multaRepository, times(1)).findByTarga(targa);
    }

    @Test
    void testGetTotalUnpaidByTarga_NoUnpaidMulte() {
        // Given
        String targa = "AB123CD";
        List<Multa> TargaMulte = Arrays.asList(multa2); // only paid multa
        when(multaRepository.findByTarga(targa)).thenReturn(TargaMulte);

        // When
        Double result = multaService.getTotalUnpaidByTarga(targa);

        // Then
        assertThat(result).isEqualTo(0.0);
        verify(multaRepository, times(1)).findByTarga(targa);
    }

    @Test
    void testGetTotalUnpaidByTarga_EmptyList() {
        // Given
        String targa = "XX999XX";
        when(multaRepository.findByTarga(targa)).thenReturn(Arrays.asList());

        // When
        Double result = multaService.getTotalUnpaidByTarga(targa);

        // Then
        assertThat(result).isEqualTo(0.0);
        verify(multaRepository, times(1)).findByTarga(targa);
    }

    @Test
    void testCreate() {
        // Given
        Multa newMulta = new Multa();
        newMulta.setTarga("IJ789KL");
        newMulta.setImporto(300.0);
        newMulta.setPagato(false);
        
        Multa savedMulta = new Multa();
        savedMulta.setId(4);
        savedMulta.setTarga("IJ789KL");
        savedMulta.setImporto(300.0);
        savedMulta.setPagato(false);
        
        when(multaRepository.save(newMulta)).thenReturn(savedMulta);

        // When
        Multa result = multaService.create(newMulta);

        // Then
        assertThat(result.getId()).isEqualTo(4);
        assertThat(result.getTarga()).isEqualTo("IJ789KL");
        assertThat(result.getImporto()).isEqualTo(300.0);
        assertThat(result.getPagato()).isFalse();
        verify(multaRepository, times(1)).save(newMulta);
    }

    @Test
    void testMarkAsPaid_Success() {
        // Given
        Integer id = 1;
        when(multaRepository.findById(id)).thenReturn(Optional.of(multa1));
        
        multa1.setPagato(true);
        when(multaRepository.save(multa1)).thenReturn(multa1);

        // When
        Optional<Multa> result = multaService.markAsPaid(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getPagato()).isTrue();
        verify(multaRepository, times(1)).findById(id);
        verify(multaRepository, times(1)).save(multa1);
    }

    @Test
    void testMarkAsPaid_MultaNotFound() {
        // Given
        Integer id = 999;
        when(multaRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Multa> result = multaService.markAsPaid(id);

        // Then
        assertThat(result).isEmpty();
        verify(multaRepository, times(1)).findById(id);
        verify(multaRepository, never()).save(any(Multa.class));
    }

    @Test
    void testUpdate_Success() {
        // Given
        Integer id = 1;
        Multa updateMulta = new Multa();
        updateMulta.setTarga("UpdatedTarga");
        updateMulta.setImporto(250.0);
        updateMulta.setPagato(true);
        
        when(multaRepository.findById(id)).thenReturn(Optional.of(multa1));
        
        multa1.setTarga("UpdatedTarga");
        multa1.setImporto(250.0);
        multa1.setPagato(true);
        when(multaRepository.save(multa1)).thenReturn(multa1);

        // When
        Optional<Multa> result = multaService.update(id, updateMulta);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTarga()).isEqualTo("UpdatedTarga");
        assertThat(result.get().getImporto()).isEqualTo(250.0);
        assertThat(result.get().getPagato()).isTrue();
        verify(multaRepository, times(1)).findById(id);
        verify(multaRepository, times(1)).save(multa1);
    }

    @Test
    void testUpdate_MultaNotFound() {
        // Given
        Integer id = 999;
        Multa updateMulta = new Multa();
        when(multaRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Multa> result = multaService.update(id, updateMulta);

        // Then
        assertThat(result).isEmpty();
        verify(multaRepository, times(1)).findById(id);
        verify(multaRepository, never()).save(any(Multa.class));
    }

    @Test
    void testDelete() {
        // Given
        Integer id = 1;

        // When
        multaService.delete(id);

        // Then
        verify(multaRepository, times(1)).deleteById(id);
    }
}