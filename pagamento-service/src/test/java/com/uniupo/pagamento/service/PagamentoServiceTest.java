package com.uniupo.pagamento.service;

import com.uniupo.pagamento.model.Pagamento;
import com.uniupo.pagamento.repository.PagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @InjectMocks
    private PagamentoService pagamentoService;

    private Pagamento pagamento1;
    private Pagamento pagamento2;
    private Pagamento pagamento3;

    @BeforeEach
    void setUp() {
        pagamento1 = new Pagamento();
        pagamento1.setIdPagamento(1);
        pagamento1.setIdBiglietto(101);
        pagamento1.setPrezzo(15.50);
        pagamento1.setStato("NON_PAGATO");
        pagamento1.setCaselloOut(2);
        pagamento1.setTimestampOut(LocalDateTime.now());
        
        pagamento2 = new Pagamento();
        pagamento2.setIdPagamento(2);
        pagamento2.setIdBiglietto(102);
        pagamento2.setPrezzo(25.75);
        pagamento2.setStato("PAGATO");
        pagamento2.setCaselloOut(3);
        pagamento2.setTimestampOut(LocalDateTime.now());
        
        pagamento3 = new Pagamento();
        pagamento3.setIdPagamento(3);
        pagamento3.setIdBiglietto(103);
        pagamento3.setPrezzo(8.25);
        pagamento3.setStato("NON_PAGATO");
        pagamento3.setCaselloOut(1);
        pagamento3.setTimestampOut(LocalDateTime.now());
    }

    @Test
    void testGetAll() {
        // Given
        List<Pagamento> pagamenti = Arrays.asList(pagamento1, pagamento2, pagamento3);
        when(pagamentoRepository.findAll()).thenReturn(pagamenti);

        // When
        List<Pagamento> result = pagamentoService.getAll();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(pagamento1, pagamento2, pagamento3);
        verify(pagamentoRepository, times(1)).findAll();
    }

    @Test
    void testGetAll_EmptyList() {
        // Given
        when(pagamentoRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Pagamento> result = pagamentoService.getAll();

        // Then
        assertThat(result).isEmpty();
        verify(pagamentoRepository, times(1)).findAll();
    }

    @Test
    void testGetById_Found() {
        // Given
        Integer id = 1;
        when(pagamentoRepository.findById(id)).thenReturn(Optional.of(pagamento1));

        // When
        Optional<Pagamento> result = pagamentoService.getById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(pagamento1);
        verify(pagamentoRepository, times(1)).findById(id);
    }

    @Test
    void testGetById_NotFound() {
        // Given
        Integer id = 999;
        when(pagamentoRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Pagamento> result = pagamentoService.getById(id);

        // Then
        assertThat(result).isEmpty();
        verify(pagamentoRepository, times(1)).findById(id);
    }

    @Test
    void testGetByBiglietto() {
        // Given
        Integer idBiglietto = 101;
        List<Pagamento> pagamenti = Arrays.asList(pagamento1);
        when(pagamentoRepository.findByIdBiglietto(idBiglietto)).thenReturn(pagamenti);

        // When
        List<Pagamento> result = pagamentoService.getByBiglietto(idBiglietto);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(pagamento1);
        verify(pagamentoRepository, times(1)).findByIdBiglietto(idBiglietto);
    }

    @Test
    void testGetByBiglietto_NoResults() {
        // Given
        Integer idBiglietto = 999;
        when(pagamentoRepository.findByIdBiglietto(idBiglietto)).thenReturn(Arrays.asList());

        // When
        List<Pagamento> result = pagamentoService.getByBiglietto(idBiglietto);

        // Then
        assertThat(result).isEmpty();
        verify(pagamentoRepository, times(1)).findByIdBiglietto(idBiglietto);
    }

    @Test
    void testGetUnpaid() {
        // Given
        List<Pagamento> unpaidPagamenti = Arrays.asList(pagamento1, pagamento3);
        when(pagamentoRepository.findByStato("NON_PAGATO")).thenReturn(unpaidPagamenti);

        // When
        List<Pagamento> result = pagamentoService.getUnpaid();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(pagamento1, pagamento3);
        verify(pagamentoRepository, times(1)).findByStato("NON_PAGATO");
    }

    @Test
    void testGetPaid() {
        // Given
        List<Pagamento> paidPagamenti = Arrays.asList(pagamento2);
        when(pagamentoRepository.findByStato("PAGATO")).thenReturn(paidPagamenti);

        // When
        List<Pagamento> result = pagamentoService.getPaid();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(pagamento2);
        verify(pagamentoRepository, times(1)).findByStato("PAGATO");
    }

    @Test
    void testCreate() {
        // Given
        Pagamento newPagamento = new Pagamento();
        newPagamento.setIdBiglietto(104);
        newPagamento.setPrezzo(12.00);
        newPagamento.setStato("NON_PAGATO");
        newPagamento.setCaselloOut(4);
        
        Pagamento savedPagamento = new Pagamento();
        savedPagamento.setIdPagamento(4);
        savedPagamento.setIdBiglietto(104);
        savedPagamento.setPrezzo(12.00);
        savedPagamento.setStato("NON_PAGATO");
        savedPagamento.setCaselloOut(4);
        
        when(pagamentoRepository.save(newPagamento)).thenReturn(savedPagamento);

        // When
        Pagamento result = pagamentoService.create(newPagamento);

        // Then
        assertThat(result.getIdPagamento()).isEqualTo(4);
        assertThat(result.getIdBiglietto()).isEqualTo(104);
        assertThat(result.getPrezzo()).isEqualTo(12.00);
        assertThat(result.getStato()).isEqualTo("NON_PAGATO");
        verify(pagamentoRepository, times(1)).save(newPagamento);
    }

    @Test
    void testMarkAsPaid_Success() {
        // Given
        Integer id = 1;
        when(pagamentoRepository.findById(id)).thenReturn(Optional.of(pagamento1));
        
        pagamento1.setStato("PAGATO");
        when(pagamentoRepository.save(pagamento1)).thenReturn(pagamento1);

        // When
        Optional<Pagamento> result = pagamentoService.markAsPaid(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getStato()).isEqualTo("PAGATO");
        verify(pagamentoRepository, times(1)).findById(id);
        verify(pagamentoRepository, times(1)).save(pagamento1);
    }

    @Test
    void testMarkAsPaid_PagamentoNotFound() {
        // Given
        Integer id = 999;
        when(pagamentoRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Pagamento> result = pagamentoService.markAsPaid(id);

        // Then
        assertThat(result).isEmpty();
        verify(pagamentoRepository, times(1)).findById(id);
        verify(pagamentoRepository, never()).save(any(Pagamento.class));
    }

    @Test
    void testUpdate_Success() {
        // Given
        Integer id = 1;
        Pagamento updatePagamento = new Pagamento();
        updatePagamento.setPrezzo(20.00);
        updatePagamento.setStato("PAGATO");
        updatePagamento.setCaselloOut(5);
        updatePagamento.setTimestampOut(LocalDateTime.now());
        
        when(pagamentoRepository.findById(id)).thenReturn(Optional.of(pagamento1));
        
        pagamento1.setPrezzo(20.00);
        pagamento1.setStato("PAGATO");
        pagamento1.setCaselloOut(5);
        pagamento1.setTimestampOut(updatePagamento.getTimestampOut());
        when(pagamentoRepository.save(pagamento1)).thenReturn(pagamento1);

        // When
        Optional<Pagamento> result = pagamentoService.update(id, updatePagamento);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getPrezzo()).isEqualTo(20.00);
        assertThat(result.get().getStato()).isEqualTo("PAGATO");
        assertThat(result.get().getCaselloOut()).isEqualTo(5);
        verify(pagamentoRepository, times(1)).findById(id);
        verify(pagamentoRepository, times(1)).save(pagamento1);
    }

    @Test
    void testUpdate_PagamentoNotFound() {
        // Given
        Integer id = 999;
        Pagamento updatePagamento = new Pagamento();
        when(pagamentoRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Pagamento> result = pagamentoService.update(id, updatePagamento);

        // Then
        assertThat(result).isEmpty();
        verify(pagamentoRepository, times(1)).findById(id);
        verify(pagamentoRepository, never()).save(any(Pagamento.class));
    }

    @Test
    void testDelete() {
        // Given
        Integer id = 1;

        // When
        pagamentoService.delete(id);

        // Then
        verify(pagamentoRepository, times(1)).deleteById(id);
    }
}