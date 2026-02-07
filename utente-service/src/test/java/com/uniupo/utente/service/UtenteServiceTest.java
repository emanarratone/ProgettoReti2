package com.uniupo.utente.service;

import com.uniupo.utente.model.Utente;
import com.uniupo.utente.repository.UtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UtenteServiceTest {

    @Mock
    private UtenteRepository utenteRepository;

    @InjectMocks
    private UtenteService utenteService;

    private Utente admin;
    private Utente regularUser;

    @BeforeEach
    void setUp() {
        admin = new Utente();
        admin.setUsername("admin");
        admin.setPassword("$2a$10$hashedAdminPassword");
        admin.setIsAdmin(true);
        
        regularUser = new Utente();
        regularUser.setUsername("user");
        regularUser.setPassword("$2a$10$hashedUserPassword");
        regularUser.setIsAdmin(false);
    }

    @Test
    void testGetAll() {
        // Given
        List<Utente> utenti = Arrays.asList(admin, regularUser);
        when(utenteRepository.findAll()).thenReturn(utenti);

        // When
        List<Utente> result = utenteService.getAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(admin, regularUser);
        verify(utenteRepository, times(1)).findAll();
    }

    @Test
    void testGetAll_EmptyList() {
        // Given
        when(utenteRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Utente> result = utenteService.getAll();

        // Then
        assertThat(result).isEmpty();
        verify(utenteRepository, times(1)).findAll();
    }

    @Test
    void testGetByUsername_Found() {
        // Given
        String username = "admin";
        when(utenteRepository.findById(username)).thenReturn(Optional.of(admin));

        // When
        Optional<Utente> result = utenteService.getByUsername(username);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(admin);
        verify(utenteRepository, times(1)).findById(username);
    }

    @Test
    void testGetByUsername_NotFound() {
        // Given
        String username = "nonexistent";
        when(utenteRepository.findById(username)).thenReturn(Optional.empty());

        // When
        Optional<Utente> result = utenteService.getByUsername(username);

        // Then
        assertThat(result).isEmpty();
        verify(utenteRepository, times(1)).findById(username);
    }

    @Test
    void testGetAdmins() {
        // Given
        List<Utente> admins = Arrays.asList(admin);
        when(utenteRepository.findByIsAdmin(true)).thenReturn(admins);

        // When
        List<Utente> result = utenteService.getAdmins();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(admin);
        verify(utenteRepository, times(1)).findByIsAdmin(true);
    }

    @Test
    void testGetRegularUsers() {
        // Given
        List<Utente> regularUsers = Arrays.asList(regularUser);
        when(utenteRepository.findByIsAdmin(false)).thenReturn(regularUsers);

        // When
        List<Utente> result = utenteService.getRegularUsers();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(regularUser);
        verify(utenteRepository, times(1)).findByIsAdmin(false);
    }

    @Test
    void testExists_True() {
        // Given
        String username = "admin";
        when(utenteRepository.existsById(username)).thenReturn(true);

        // When
        boolean result = utenteService.exists(username);

        // Then
        assertThat(result).isTrue();
        verify(utenteRepository, times(1)).existsById(username);
    }

    @Test
    void testExists_False() {
        // Given
        String username = "nonexistent";
        when(utenteRepository.existsById(username)).thenReturn(false);

        // When
        boolean result = utenteService.exists(username);

        // Then
        assertThat(result).isFalse();
        verify(utenteRepository, times(1)).existsById(username);
    }

    @Test
    void testCreate() {
        // Given
        Utente newUtente = new Utente();
        newUtente.setUsername("newuser");
        newUtente.setPassword("plainPassword");
        newUtente.setIsAdmin(false);
        
        Utente savedUtente = new Utente();
        savedUtente.setUsername("newuser");
        savedUtente.setPassword("$2a$10$hashedPassword");
        savedUtente.setIsAdmin(false);

        try (MockedStatic<BCrypt> bcryptMock = mockStatic(BCrypt.class)) {
            bcryptMock.when(() -> BCrypt.hashpw(eq("plainPassword"), any(String.class)))
                     .thenReturn("$2a$10$hashedPassword");
            bcryptMock.when(() -> BCrypt.gensalt()).thenReturn("$2a$10$salt");
            
            when(utenteRepository.save(newUtente)).thenReturn(savedUtente);

            // When
            Utente result = utenteService.create(newUtente);

            // Then
            assertThat(result.getUsername()).isEqualTo("newuser");
            assertThat(result.getPassword()).isEqualTo("$2a$10$hashedPassword");
            assertThat(result.getIsAdmin()).isFalse();
            verify(utenteRepository, times(1)).save(newUtente);
        }
    }

    @Test
    void testAuthenticate_Success() {
        // Given
        String username = "admin";
        String password = "plainPassword";
        
        when(utenteRepository.findById(username)).thenReturn(Optional.of(admin));

        try (MockedStatic<BCrypt> bcryptMock = mockStatic(BCrypt.class)) {
            bcryptMock.when(() -> BCrypt.checkpw(password, admin.getPassword()))
                     .thenReturn(true);

            // When
            boolean result = utenteService.authenticate(username, password);

            // Then
            assertThat(result).isTrue();
            verify(utenteRepository, times(1)).findById(username);
        }
    }

    @Test
    void testAuthenticate_WrongPassword() {
        // Given
        String username = "admin";
        String password = "wrongPassword";
        
        when(utenteRepository.findById(username)).thenReturn(Optional.of(admin));

        try (MockedStatic<BCrypt> bcryptMock = mockStatic(BCrypt.class)) {
            bcryptMock.when(() -> BCrypt.checkpw(password, admin.getPassword()))
                     .thenReturn(false);

            // When
            boolean result = utenteService.authenticate(username, password);

            // Then
            assertThat(result).isFalse();
            verify(utenteRepository, times(1)).findById(username);
        }
    }

    @Test
    void testAuthenticate_UserNotFound() {
        // Given
        String username = "nonexistent";
        String password = "password";
        
        when(utenteRepository.findById(username)).thenReturn(Optional.empty());

        // When
        boolean result = utenteService.authenticate(username, password);

        // Then
        assertThat(result).isFalse();
        verify(utenteRepository, times(1)).findById(username);
    }

    @Test
    void testLogin_Success() {
        // Given
        String username = "admin";
        String password = "plainPassword";
        
        when(utenteRepository.findById(username)).thenReturn(Optional.of(admin));

        try (MockedStatic<BCrypt> bcryptMock = mockStatic(BCrypt.class)) {
            bcryptMock.when(() -> BCrypt.checkpw(password, admin.getPassword()))
                     .thenReturn(true);

            // When
            Optional<Utente> result = utenteService.login(username, password);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(admin);
            verify(utenteRepository, times(1)).findById(username);
        }
    }

    @Test
    void testLogin_Failure() {
        // Given
        String username = "admin";
        String password = "wrongPassword";
        
        when(utenteRepository.findById(username)).thenReturn(Optional.of(admin));

        try (MockedStatic<BCrypt> bcryptMock = mockStatic(BCrypt.class)) {
            bcryptMock.when(() -> BCrypt.checkpw(password, admin.getPassword()))
                     .thenReturn(false);

            // When
            Optional<Utente> result = utenteService.login(username, password);

            // Then
            assertThat(result).isEmpty();
            verify(utenteRepository, times(1)).findById(username);
        }
    }

    @Test
    void testUpdatePassword_Success() {
        // Given
        String username = "admin";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        
        when(utenteRepository.findById(username)).thenReturn(Optional.of(admin));
        when(utenteRepository.save(admin)).thenReturn(admin);

        try (MockedStatic<BCrypt> bcryptMock = mockStatic(BCrypt.class)) {
            bcryptMock.when(() -> BCrypt.checkpw(oldPassword, admin.getPassword()))
                     .thenReturn(true);
            bcryptMock.when(() -> BCrypt.hashpw(eq(newPassword), any(String.class)))
                     .thenReturn("$2a$10$newHashedPassword");
            bcryptMock.when(() -> BCrypt.gensalt()).thenReturn("$2a$10$salt");

            // When
            Optional<Utente> result = utenteService.updatePassword(username, oldPassword, newPassword);

            // Then
            assertThat(result).isPresent();
            verify(utenteRepository, times(1)).findById(username);
            verify(utenteRepository, times(1)).save(admin);
        }
    }

    @Test
    void testUpdatePassword_WrongOldPassword() {
        // Given
        String username = "admin";
        String oldPassword = "wrongOldPassword";
        String newPassword = "newPassword";
        
        when(utenteRepository.findById(username)).thenReturn(Optional.of(admin));

        try (MockedStatic<BCrypt> bcryptMock = mockStatic(BCrypt.class)) {
            bcryptMock.when(() -> BCrypt.checkpw(oldPassword, admin.getPassword()))
                     .thenReturn(false);

            // When
            Optional<Utente> result = utenteService.updatePassword(username, oldPassword, newPassword);

            // Then
            assertThat(result).isEmpty();
            verify(utenteRepository, times(1)).findById(username);
            verify(utenteRepository, never()).save(any(Utente.class));
        }
    }

    @Test
    void testToggleAdmin() {
        // Given
        String username = "user";
        when(utenteRepository.findById(username)).thenReturn(Optional.of(regularUser));
        when(utenteRepository.save(regularUser)).thenReturn(regularUser);

        // When
        Optional<Utente> result = utenteService.toggleAdmin(username);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getIsAdmin()).isTrue(); // was false, now true
        verify(utenteRepository, times(1)).findById(username);
        verify(utenteRepository, times(1)).save(regularUser);
    }

    @Test
    void testToggleAdmin_UserNotFound() {
        // Given
        String username = "nonexistent";
        when(utenteRepository.findById(username)).thenReturn(Optional.empty());

        // When
        Optional<Utente> result = utenteService.toggleAdmin(username);

        // Then
        assertThat(result).isEmpty();
        verify(utenteRepository, times(1)).findById(username);
        verify(utenteRepository, never()).save(any(Utente.class));
    }

    @Test
    void testDelete() {
        // Given
        String username = "admin";

        // When
        utenteService.delete(username);

        // Then
        verify(utenteRepository, times(1)).deleteById(username);
    }
}