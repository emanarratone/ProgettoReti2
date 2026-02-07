package com.uniupo.veicolo.service;

import com.uniupo.veicolo.model.Veicolo;
import com.uniupo.veicolo.repository.VeicoloRepository;
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
class VeicoloServiceTest {

    @Mock
    private VeicoloRepository veicoloRepository;

    @InjectMocks
    private VeicoloService veicoloService;

    private Veicolo auto;
    private Veicolo moto;
    private Veicolo autobus;

    @BeforeEach
    void setUp() {
        auto = new Veicolo();
        auto.setTarga("AB123CD");
        auto.setTipoVeicolo(Veicolo.TipoVeicolo.B);
        
        moto = new Veicolo();
        moto.setTarga("MO456TO");
        moto.setTipoVeicolo(Veicolo.TipoVeicolo.A);
        
        autobus = new Veicolo();
        autobus.setTarga("BU789SS");
        autobus.setTipoVeicolo(Veicolo.TipoVeicolo.D);
    }

    @Test
    void testGetAll() {
        // Given
        List<Veicolo> veicoli = Arrays.asList(auto, moto, autobus);
        when(veicoloRepository.findAll()).thenReturn(veicoli);

        // When
        List<Veicolo> result = veicoloService.getAll();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(auto, moto, autobus);
        verify(veicoloRepository, times(1)).findAll();
    }

    @Test
    void testGetAll_EmptyList() {
        // Given
        when(veicoloRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Veicolo> result = veicoloService.getAll();

        // Then
        assertThat(result).isEmpty();
        verify(veicoloRepository, times(1)).findAll();
    }

    @Test
    void testGetByTarga_Found() {
        // Given
        String targa = "AB123CD";
        when(veicoloRepository.findById(targa)).thenReturn(Optional.of(auto));

        // When
        Optional<Veicolo> result = veicoloService.getByTarga(targa);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(auto);
        verify(veicoloRepository, times(1)).findById(targa);
    }

    @Test
    void testGetByTarga_NotFound() {
        // Given
        String targa = "XX999XX";
        when(veicoloRepository.findById(targa)).thenReturn(Optional.empty());

        // When
        Optional<Veicolo> result = veicoloService.getByTarga(targa);

        // Then
        assertThat(result).isEmpty();
        verify(veicoloRepository, times(1)).findById(targa);
    }

    @Test
    void testGetByTipo_Auto() {
        // Given
        Veicolo.TipoVeicolo tipo = Veicolo.TipoVeicolo.B;
        List<Veicolo> veicoli = Arrays.asList(auto);
        when(veicoloRepository.findByTipoVeicolo(tipo)).thenReturn(veicoli);

        // When
        List<Veicolo> result = veicoloService.getByTipo(tipo);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(auto);
        verify(veicoloRepository, times(1)).findByTipoVeicolo(tipo);
    }

    @Test
    void testGetByTipo_Moto() {
        // Given
        Veicolo.TipoVeicolo tipo = Veicolo.TipoVeicolo.A;
        List<Veicolo> veicoli = Arrays.asList(moto);
        when(veicoloRepository.findByTipoVeicolo(tipo)).thenReturn(veicoli);

        // When
        List<Veicolo> result = veicoloService.getByTipo(tipo);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(moto);
        verify(veicoloRepository, times(1)).findByTipoVeicolo(tipo);
    }

    @Test
    void testGetByTipo_NoResults() {
        // Given
        Veicolo.TipoVeicolo tipo = Veicolo.TipoVeicolo.E;
        when(veicoloRepository.findByTipoVeicolo(tipo)).thenReturn(Arrays.asList());

        // When
        List<Veicolo> result = veicoloService.getByTipo(tipo);

        // Then
        assertThat(result).isEmpty();
        verify(veicoloRepository, times(1)).findByTipoVeicolo(tipo);
    }

    @Test
    void testExists_True() {
        // Given
        String targa = "AB123CD";
        when(veicoloRepository.existsById(targa)).thenReturn(true);

        // When
        boolean result = veicoloService.exists(targa);

        // Then
        assertThat(result).isTrue();
        verify(veicoloRepository, times(1)).existsById(targa);
    }

    @Test
    void testExists_False() {
        // Given
        String targa = "XX999XX";
        when(veicoloRepository.existsById(targa)).thenReturn(false);

        // When
        boolean result = veicoloService.exists(targa);

        // Then
        assertThat(result).isFalse();
        verify(veicoloRepository, times(1)).existsById(targa);
    }

    @Test
    void testCreate() {
        // Given
        Veicolo newVeicolo = new Veicolo();
        newVeicolo.setTarga("NEW123XY");
        newVeicolo.setTipoVeicolo(Veicolo.TipoVeicolo.E);
        
        when(veicoloRepository.save(newVeicolo)).thenReturn(newVeicolo);

        // When
        Veicolo result = veicoloService.create(newVeicolo);

        // Then
        assertThat(result.getTarga()).isEqualTo("NEW123XY");
        assertThat(result.getTipoVeicolo()).isEqualTo(Veicolo.TipoVeicolo.E);
        verify(veicoloRepository, times(1)).save(newVeicolo);
    }

    @Test
    void testUpdate_Success() {
        // Given
        String targa = "AB123CD";
        Veicolo updateVeicolo = new Veicolo();
        updateVeicolo.setTarga(targa);
        updateVeicolo.setTipoVeicolo(Veicolo.TipoVeicolo.C);
        
        when(veicoloRepository.findById(targa)).thenReturn(Optional.of(auto));
        
        auto.setTipoVeicolo(Veicolo.TipoVeicolo.C);
        when(veicoloRepository.save(auto)).thenReturn(auto);

        // When
        Optional<Veicolo> result = veicoloService.update(targa, updateVeicolo);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTipoVeicolo()).isEqualTo(Veicolo.TipoVeicolo.C);
        verify(veicoloRepository, times(1)).findById(targa);
        verify(veicoloRepository, times(1)).save(auto);
    }

    @Test
    void testUpdate_VeicoloNotFound() {
        // Given
        String targa = "XX999XX";
        Veicolo updateVeicolo = new Veicolo();
        when(veicoloRepository.findById(targa)).thenReturn(Optional.empty());

        // When
        Optional<Veicolo> result = veicoloService.update(targa, updateVeicolo);

        // Then
        assertThat(result).isEmpty();
        verify(veicoloRepository, times(1)).findById(targa);
        verify(veicoloRepository, never()).save(any(Veicolo.class));
    }

    @Test
    void testDelete() {
        // Given
        String targa = "AB123CD";

        // When
        veicoloService.delete(targa);

        // Then
        verify(veicoloRepository, times(1)).deleteById(targa);
    }

    @Test
    void testCountByTipo() {
        // Given
        Veicolo.TipoVeicolo tipo = Veicolo.TipoVeicolo.B;
        List<Veicolo> autoList = Arrays.asList(auto); // 1 auto
        when(veicoloRepository.findByTipoVeicolo(tipo)).thenReturn(autoList);

        // When
        long result = veicoloService.countByTipo(tipo);

        // Then
        assertThat(result).isEqualTo(1);
        verify(veicoloRepository, times(1)).findByTipoVeicolo(tipo);
    }

    @Test
    void testCountByTipo_MultipleVehicles() {
        // Given
        Veicolo.TipoVeicolo tipo = Veicolo.TipoVeicolo.B;
        Veicolo auto2 = new Veicolo();
        auto2.setTarga("CD456EF");
        auto2.setTipoVeicolo(Veicolo.TipoVeicolo.B);
        
        List<Veicolo> autoList = Arrays.asList(auto, auto2); // 2 auto
        when(veicoloRepository.findByTipoVeicolo(tipo)).thenReturn(autoList);

        // When
        long result = veicoloService.countByTipo(tipo);

        // Then
        assertThat(result).isEqualTo(2);
        verify(veicoloRepository, times(1)).findByTipoVeicolo(tipo);
    }

    @Test
    void testCountByTipo_NoVehicles() {
        // Given
        Veicolo.TipoVeicolo tipo = Veicolo.TipoVeicolo.E;
        when(veicoloRepository.findByTipoVeicolo(tipo)).thenReturn(Arrays.asList());

        // When
        long result = veicoloService.countByTipo(tipo);

        // Then
        assertThat(result).isEqualTo(0);
        verify(veicoloRepository, times(1)).findByTipoVeicolo(tipo);
    }
}