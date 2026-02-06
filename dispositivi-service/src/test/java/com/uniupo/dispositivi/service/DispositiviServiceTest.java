package com.uniupo.dispositivi.service;

import com.uniupo.dispositivi.model.Dispositivo;
import com.uniupo.dispositivi.model.Sbarra;
import com.uniupo.dispositivi.model.Telecamera;
import com.uniupo.dispositivi.model.Totem;
import com.uniupo.dispositivi.repository.DispositivoRepository;
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
class DispositiviServiceTest {

    @Mock
    private DispositivoRepository dispositivoRepository;

    @InjectMocks
    private DispositiviService dispositiviService;

    private Dispositivo sbarra;
    private Dispositivo telecamera;
    private Dispositivo totem;

    @BeforeEach
    void setUp() {
        sbarra = new Sbarra(true, 1, 1);
        sbarra.setID(1);

        telecamera = new Telecamera(true, 2, 1);
        telecamera.setID(2);
        
        totem = new Totem(false, 3, 2);
        totem.setID(3);
    }

    @Test
    void testGetAll() {
        // Given
        List<Dispositivo> dispositivi = Arrays.asList(sbarra, telecamera, totem);
        when(dispositivoRepository.findAll()).thenReturn(dispositivi);

        // When
        List<Dispositivo> result = dispositiviService.getAll();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(sbarra, telecamera, totem);
        verify(dispositivoRepository, times(1)).findAll();
    }

    @Test
    void testGetAll_EmptyList() {
        // Given
        when(dispositivoRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Dispositivo> result = dispositiviService.getAll();

        // Then
        assertThat(result).isEmpty();
        verify(dispositivoRepository, times(1)).findAll();
    }

    @Test
    void testGetById_Found() {
        // Given
        Integer id = 1;
        when(dispositivoRepository.findById(id)).thenReturn(Optional.of(sbarra));

        // When
        Optional<Dispositivo> result = dispositiviService.getById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(sbarra);
        verify(dispositivoRepository, times(1)).findById(id);
    }

    @Test
    void testGetById_NotFound() {
        // Given
        Integer id = 999;
        when(dispositivoRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Dispositivo> result = dispositiviService.getById(id);

        // Then
        assertThat(result).isEmpty();
        verify(dispositivoRepository, times(1)).findById(id);
    }

    @Test
    void testGetByCasello() {
        // Given
        Integer casello = 1;
        List<Dispositivo> dispositivi = Arrays.asList(sbarra, telecamera);
        when(dispositivoRepository.findByCasello(casello)).thenReturn(dispositivi);

        // When
        List<Dispositivo> result = dispositiviService.getByCasello(casello);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(sbarra, telecamera);
        verify(dispositivoRepository, times(1)).findByCasello(casello);
    }

    @Test
    void testGetByCorsia() {
        // Given
        Integer corsia = 1;
        List<Dispositivo> dispositivi = Arrays.asList(sbarra);
        when(dispositivoRepository.findByCorsia(corsia)).thenReturn(dispositivi);

        // When
        List<Dispositivo> result = dispositiviService.getByCorsia(corsia);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(sbarra);
        verify(dispositivoRepository, times(1)).findByCorsia(corsia);
    }

    @Test
    void testGetByCaselloAndCorsia() {
        // Given
        Integer casello = 1;
        Integer corsia = 2;
        List<Dispositivo> dispositivi = Arrays.asList(telecamera);
        when(dispositivoRepository.findByCaselloAndCorsia(casello, corsia)).thenReturn(dispositivi);

        // When
        List<Dispositivo> result = dispositiviService.getByCaselloAndCorsia(casello, corsia);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(telecamera);
        verify(dispositivoRepository, times(1)).findByCaselloAndCorsia(casello, corsia);
    }

    @Test
    void testCreateForLane_Sbarra() {
        // Given
        Integer idCasello = 1;
        Integer numCorsia = 1;
        String tipoStr = "SBARRA";
        String statoStr = "ATTIVO";
        
        Sbarra savedSbarra = new Sbarra(true, numCorsia, idCasello);
        savedSbarra.setID(4);
        when(dispositivoRepository.save(any(Sbarra.class))).thenReturn(savedSbarra);

        // When
        Dispositivo result = dispositiviService.createForLane(idCasello, numCorsia, tipoStr, statoStr);

        // Then
        assertThat(result).isInstanceOf(Sbarra.class);
        assertThat(result.getID()).isEqualTo(4);
        assertThat(result.getStatus());
        assertThat(result.getCorsia()).isEqualTo(numCorsia);
        assertThat(result.getCasello()).isEqualTo(idCasello);

        verify(dispositivoRepository, times(1)).save(any(Sbarra.class));
    }

    @Test
    void testCreateForLane_Telecamera() {
        // Given
        Integer idCasello = 2;
        Integer numCorsia = 3;
        String tipoStr = "TELECAMERA";
        String statoStr = "true";
        
        Telecamera savedTelecamera = new Telecamera(true, numCorsia, idCasello);
        savedTelecamera.setID(5);
        when(dispositivoRepository.save(any(Telecamera.class))).thenReturn(savedTelecamera);

        // When
        Dispositivo result = dispositiviService.createForLane(idCasello, numCorsia, tipoStr, statoStr);

        // Then
        assertThat(result).isInstanceOf(Telecamera.class);
        assertThat(result.getID()).isEqualTo(5);
        verify(dispositivoRepository, times(1)).save(any(Telecamera.class));
    }

    @Test
    void testCreateForLane_Totem() {
        // Given
        Integer idCasello = 3;
        Integer numCorsia = 4;
        String tipoStr = "TOTEM";
        String statoStr = "false";
        
        Totem savedTotem = new Totem(false, numCorsia, idCasello);
        savedTotem.setID(6);
        when(dispositivoRepository.save(any(Totem.class))).thenReturn(savedTotem);

        // When
        Dispositivo result = dispositiviService.createForLane(idCasello, numCorsia, tipoStr, statoStr);

        // Then
        assertThat(result).isInstanceOf(Totem.class);
        assertThat(result.getID()).isEqualTo(6);
        assertThat(result.getStatus()).asBoolean().isFalse();
        verify(dispositivoRepository, times(1)).save(any(Totem.class));
    }

    @Test
    void testCreateForLane_DefaultTotem() {
        // Given
        Integer idCasello = 1;
        Integer numCorsia = 1;
        String tipoStr = "UNKNOWN_TYPE";
        String statoStr = "INATTIVO";
        
        Totem savedTotem = new Totem(false, numCorsia, idCasello);
        savedTotem.setID(7);
        when(dispositivoRepository.save(any(Totem.class))).thenReturn(savedTotem);

        // When
        Dispositivo result = dispositiviService.createForLane(idCasello, numCorsia, tipoStr, statoStr);

        // Then
        assertThat(result).isInstanceOf(Totem.class); // default type
        assertThat(result.getStatus()).asBoolean().isFalse(); // not "ATTIVO" or "true"
        verify(dispositivoRepository, times(1)).save(any(Totem.class));
    }

    @Test
    void testGetSbarre() {
        // Given
        List<Dispositivo> allDispositivi = Arrays.asList(sbarra, telecamera, totem);
        when(dispositivoRepository.findAll()).thenReturn(allDispositivi);

        // When
        List<Sbarra> result = dispositiviService.getSbarre();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(sbarra);
        verify(dispositivoRepository, times(1)).findAll();
    }

    @Test
    void testGetTelecamere() {
        // Given
        List<Dispositivo> allDispositivi = Arrays.asList(sbarra, telecamera, totem);
        when(dispositivoRepository.findAll()).thenReturn(allDispositivi);

        // When
        List<Telecamera> result = dispositiviService.getTelecamere();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(telecamera);
        verify(dispositivoRepository, times(1)).findAll();
    }

    @Test
    void testGetTotem() {
        // Given
        List<Dispositivo> allDispositivi = Arrays.asList(sbarra, telecamera, totem);
        when(dispositivoRepository.findAll()).thenReturn(allDispositivi);

        // When
        List<Totem> result = dispositiviService.getTotem();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(totem);
        verify(dispositivoRepository, times(1)).findAll();
    }

    @Test
    void testGetActive() {
        // Given
        List<Dispositivo> activeDispositivi = Arrays.asList(sbarra, telecamera);
        when(dispositivoRepository.findByStatus(true)).thenReturn(activeDispositivi);

        // When
        List<Dispositivo> result = dispositiviService.getActive();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(sbarra, telecamera);
        verify(dispositivoRepository, times(1)).findByStatus(true);
    }

    @Test
    void testGetInactive() {
        // Given
        List<Dispositivo> inactiveDispositivi = Arrays.asList(totem);
        when(dispositivoRepository.findByStatus(false)).thenReturn(inactiveDispositivi);

        // When
        List<Dispositivo> result = dispositiviService.getInactive();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(totem);
        verify(dispositivoRepository, times(1)).findByStatus(false);
    }

    @Test
    void testCreate() {
        // Given
        Dispositivo newDispositivo = new Sbarra(true, 5, 3);
        when(dispositivoRepository.save(newDispositivo)).thenReturn(newDispositivo);

        // When
        Dispositivo result = dispositiviService.create(newDispositivo);

        // Then
        assertThat(result).isEqualTo(newDispositivo);
        verify(dispositivoRepository, times(1)).save(newDispositivo);
    }
}