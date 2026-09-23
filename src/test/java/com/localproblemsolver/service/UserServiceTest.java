package com.localproblemsolver.service;

import com.localproblemsolver.dto.UserAuthorityUpdateRequest;
import com.localproblemsolver.dto.UserResponse;
import com.localproblemsolver.dto.UserRoleUpdateRequest;
import com.localproblemsolver.entity.Authority;
import com.localproblemsolver.entity.Role;
import com.localproblemsolver.entity.User;
import com.localproblemsolver.exception.UserNotFoundException;
import com.localproblemsolver.repository.AuthorityRepository;
import com.localproblemsolver.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @InjectMocks
    private UserService userService;

    private User citizen;
    private User authorityUser;
    private User moderator;
    private Authority authority;

    @BeforeEach
    void setUp() {

        citizen = new User();
        citizen.setId(1L);
        citizen.setName("Citizen A");
        citizen.setEmail("citizen@gmail.com");
        citizen.setRole(Role.CITIZEN);

        authorityUser = new User();
        authorityUser.setId(2L);
        authorityUser.setName("Authority User");
        authorityUser.setEmail("authority@gmail.com");
        authorityUser.setRole(Role.AUTHORITY);

        moderator = new User();
        moderator.setId(3L);
        moderator.setName("Moderator");
        moderator.setEmail("moderator@gmail.com");
        moderator.setRole(Role.MODERATOR);

        authority = new Authority();
        authority.setId(10L);
        authority.setName("Road Authority");
    }

    // =========================================================
    // GET ALL USERS
    // =========================================================

    @Test
    void getAllUsersReturnsUsers() {

        when(userRepository.findAll())
                .thenReturn(List.of(citizen, authorityUser));

        List<UserResponse> result =
                userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals(
                "Citizen A",
                result.get(0).getName()
        );
        assertEquals(
                "citizen@gmail.com",
                result.get(0).getEmail()
        );
        assertEquals(
                Role.CITIZEN,
                result.get(0).getRole()
        );

        assertEquals(2L, result.get(1).getId());
        assertEquals(
                Role.AUTHORITY,
                result.get(1).getRole()
        );

        verify(userRepository).findAll();
    }

    @Test
    void getAllUsersReturnsEmptyListWhenNoUsersExist() {

        when(userRepository.findAll())
                .thenReturn(List.of());

        List<UserResponse> result =
                userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
    }

    // =========================================================
    // GET USER BY ID
    // =========================================================

    @Test
    void getUserByIdReturnsUser() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(citizen));

        UserResponse result =
                userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(
                "Citizen A",
                result.getName()
        );
        assertEquals(
                "citizen@gmail.com",
                result.getEmail()
        );
        assertEquals(
                Role.CITIZEN,
                result.getRole()
        );
    }

    @Test
    void getUserByIdFailsWhenUserDoesNotExist() {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        UserNotFoundException exception =
                assertThrows(
                        UserNotFoundException.class,
                        () -> userService.getUserById(999L)
                );

        assertTrue(
                exception.getMessage()
                        .contains("999")
        );

        verify(userRepository).findById(999L);
    }

    // =========================================================
    // UPDATE USER ROLE
    // =========================================================

    @Test
    void updateUserRoleSuccessfully() {

        UserRoleUpdateRequest request =
                new UserRoleUpdateRequest();

        request.setRole(Role.MODERATOR);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(citizen));

        when(userRepository.save(citizen))
                .thenReturn(citizen);

        UserResponse result =
                userService.updateUserRole(
                        1L,
                        request
                );

        assertNotNull(result);
        assertEquals(Role.MODERATOR, citizen.getRole());
        assertEquals(Role.MODERATOR, result.getRole());

        verify(userRepository).save(citizen);
    }

    @Test
    void updateUserRoleCanSetAuthorityRole() {

        UserRoleUpdateRequest request =
                new UserRoleUpdateRequest();

        request.setRole(Role.AUTHORITY);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(citizen));

        when(userRepository.save(citizen))
                .thenReturn(citizen);

        UserResponse result =
                userService.updateUserRole(
                        1L,
                        request
                );

        assertEquals(Role.AUTHORITY, citizen.getRole());
        assertEquals(Role.AUTHORITY, result.getRole());
    }

    @Test
    void updateUserRoleFailsWhenUserDoesNotExist() {

        UserRoleUpdateRequest request =
                new UserRoleUpdateRequest();

        request.setRole(Role.MODERATOR);

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUserRole(
                        999L,
                        request
                )
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void updateUserRoleFailsWhenRoleIsNull() {

        UserRoleUpdateRequest request =
                new UserRoleUpdateRequest();

        request.setRole(null);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(citizen));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.updateUserRole(
                                1L,
                                request
                        )
                );

        assertEquals(
                "Role cannot be null",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    // =========================================================
    // UPDATE USER AUTHORITY
    // =========================================================

    @Test
    void updateUserAuthoritySuccessfully() {

        UserAuthorityUpdateRequest request =
                new UserAuthorityUpdateRequest();

        request.setAuthorityId(10L);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(authorityUser));

        when(authorityRepository.findById(10L))
                .thenReturn(Optional.of(authority));

        when(userRepository.save(authorityUser))
                .thenReturn(authorityUser);

        UserResponse result =
                userService.updateUserAuthority(
                        2L,
                        request
                );

        assertNotNull(result);
        assertEquals(
                authority,
                authorityUser.getAuthority()
        );

        assertEquals(
                Role.AUTHORITY,
                result.getRole()
        );

        verify(authorityRepository)
                .findById(10L);

        verify(userRepository)
                .save(authorityUser);
    }

    @Test
    void updateUserAuthorityFailsWhenUserDoesNotExist() {

        UserAuthorityUpdateRequest request =
                new UserAuthorityUpdateRequest();

        request.setAuthorityId(10L);

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUserAuthority(
                        999L,
                        request
                )
        );

        verify(authorityRepository, never())
                .findById(anyLong());

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void updateUserAuthorityFailsForNonAuthorityUser() {

        UserAuthorityUpdateRequest request =
                new UserAuthorityUpdateRequest();

        request.setAuthorityId(10L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(citizen));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.updateUserAuthority(
                                1L,
                                request
                        )
                );

        assertEquals(
                "Only users with AUTHORITY role can be linked to an authority",
                exception.getMessage()
        );

        verify(authorityRepository, never())
                .findById(anyLong());

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void updateUserAuthorityFailsWhenAuthorityIdIsNull() {

        UserAuthorityUpdateRequest request =
                new UserAuthorityUpdateRequest();

        request.setAuthorityId(null);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(authorityUser));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.updateUserAuthority(
                                2L,
                                request
                        )
                );

        assertEquals(
                "Authority id cannot be null",
                exception.getMessage()
        );

        verify(authorityRepository, never())
                .findById(anyLong());

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void updateUserAuthorityFailsWhenAuthorityDoesNotExist() {

        UserAuthorityUpdateRequest request =
                new UserAuthorityUpdateRequest();

        request.setAuthorityId(999L);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(authorityUser));

        when(authorityRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> userService.updateUserAuthority(
                                2L,
                                request
                        )
                );

        assertEquals(
                404,
                exception.getStatusCode().value()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    // =========================================================
    // VERIFY AUTHORITY LINK
    // =========================================================

    @Test
    void updateUserAuthorityReplacesExistingAuthority() {

        Authority oldAuthority = new Authority();
        oldAuthority.setId(20L);
        oldAuthority.setName("Old Authority");

        authorityUser.setAuthority(oldAuthority);

        UserAuthorityUpdateRequest request =
                new UserAuthorityUpdateRequest();

        request.setAuthorityId(10L);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(authorityUser));

        when(authorityRepository.findById(10L))
                .thenReturn(Optional.of(authority));

        when(userRepository.save(authorityUser))
                .thenReturn(authorityUser);

        userService.updateUserAuthority(
                2L,
                request
        );

        assertEquals(
                10L,
                authorityUser.getAuthority().getId()
        );

        assertEquals(
                "Road Authority",
                authorityUser.getAuthority().getName()
        );
    }
}