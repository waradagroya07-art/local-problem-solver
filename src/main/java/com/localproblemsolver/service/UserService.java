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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public UserService(
            UserRepository userRepository,
            AuthorityRepository authorityRepository) {

        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private UserResponse convertToResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + id
                        )
                );

        return convertToResponse(user);
    }

    public UserResponse updateUserRole(
            Long id,
            UserRoleUpdateRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + id
                        )
                );

        Role newRole = request.getRole();

        if (newRole == null) {
            throw new IllegalArgumentException(
                    "Role cannot be null"
            );
        }

        user.setRole(newRole);

        User updatedUser =
                userRepository.save(user);

        return convertToResponse(updatedUser);
    }

    public UserResponse updateUserAuthority(
            Long id,
            UserAuthorityUpdateRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + id
                        )
                );

        if (user.getRole() != Role.AUTHORITY) {
            throw new IllegalArgumentException(
                    "Only users with AUTHORITY role can be linked to an authority"
            );
        }

        if (request.getAuthorityId() == null) {
            throw new IllegalArgumentException(
                    "Authority id cannot be null"
            );
        }

        Authority authority = authorityRepository
                .findById(request.getAuthorityId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Authority not found with id: "
                                        + request.getAuthorityId()
                        )
                );

        user.setAuthority(authority);

        User updatedUser =
                userRepository.save(user);

        return convertToResponse(updatedUser);
    }
}