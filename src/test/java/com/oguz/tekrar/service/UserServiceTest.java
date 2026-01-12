package com.oguz.tekrar.service;

import com.oguz.tekrar.dto.UserRequest;
import com.oguz.tekrar.dto.UserResponse;
import com.oguz.tekrar.entity.Role;
import com.oguz.tekrar.entity.User;
import com.oguz.tekrar.mapper.UserMapper;
import com.oguz.tekrar.repository.RoleRepository;
import com.oguz.tekrar.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;
    @Mock
    RoleRepository roleRepository;


    @Test
    @DisplayName("Tüm kullanıcıları getirmeli")
    void shouldReturnAllUsers() {
        User user = User.builder().id(1L).name("Oguz").build();
        List<User> userList = List.of(user);
        UserResponse userResponse = UserResponse.builder().id(1L).build();
        List<UserResponse> responseList = List.of(userResponse);
        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.toUserResponseList(userList)).thenReturn(responseList);
        List<UserResponse> result = userService.getAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("ID ile kullanıcı bulunduğunda UserResponse dönmeli")
    void shouldReturnUser_WhenIdExists() {
        Long userId = 1L;
        User user = User.builder().id(userId).name("Ali").build();
        UserResponse expectedResponse = UserResponse.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(expectedResponse);
        UserResponse result = userService.getUserById(userId);
        assertEquals(userId, result.getId());
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("ID ile kullanıcı bulunamadığında RuntimeException fırlatmalı")
    void shouldThrowException_WhenUserNotFound() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserById(userId));
        assertEquals("Aranan Id ile kullanıcı bulunamadı.", exception.getMessage());
    }

    @Test
    @DisplayName("Kullanıcı oluşturulurken varsayılan ROL atanmalı")
    void shouldCreateUserWithDefaultRole() {
        UserRequest request = new UserRequest("Ali", "Veli", "ali@mail.com", "pass");

        User userToSave = User.builder()
                .name("Ali")
                .email("ali@mail.com")
                .roles(new ArrayList<>())
                .build();

        User savedUser = User.builder()
                .id(1L)
                .name("Ali")
                .email("ali@mail.com")
                .roles(new ArrayList<>())
                .build();
        savedUser.getRoles().add(Role.builder().roleName("ROLE_USER").build());

        UserResponse expectedResponse = UserResponse.builder().id(1L).email("ali@mail.com").build();

        when(userMapper.toUser(request)).thenReturn(userToSave);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toUserResponse(savedUser)).thenReturn(expectedResponse);

        UserResponse result = userService.create(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, userToSave.getRoles().size());
        assertEquals("ROLE_USER", userToSave.getRoles().get(0).getRoleName());
        verify(userRepository).save(any(User.class));
    }


    @Test
    @DisplayName("Kullanıcı silme işlemi başarılı olmalı")
    void shouldDeleteUser_WhenIdExists() {
        Long userId = 1L;
        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.deleteUser(userId);
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("Silinecek kullanıcı bulunamazsa hata fırlatmalı")
    void shouldThrowException_WhenDeletingNonExistingUser() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(userId));
        assertEquals("Silinmek istenen id ile kullanıcı bulumadı.", exception.getMessage());
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Kullanıcı güncelleme başarılı olmalı")
    void shouldUpdateUser_WhenIdExists() {
        Long userId = 1L;
        UserRequest request = new UserRequest("NewName", "NewLast", "new@mail.com", "pass");
        User existingUser = User.builder().id(userId).name("OldName").build();
        User updatedUser = User.builder().id(userId).name("NewName").build(); // Save sonrası dönen
        UserResponse response = UserResponse.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toUserResponse(updatedUser)).thenReturn(response);
        UserResponse result = userService.updateUser(request, userId);
        assertNotNull(result);
        verify(userMapper).updateEntityFromRequest(request, existingUser);
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("Rol silme işlemi: Rol ve Kullanıcı mevcutsa ilişki kesilmeli")
    void shouldRemoveRoleFromUser_WhenRoleAndUserExist() {
        Long roleId = 10L;
        List<Role> roles = new ArrayList<>();
        User user = User.builder().id(1L).roles(roles).build();
        Role role = Role.builder().id(roleId).roleName("ROLE_USER").user(user).build();
        roles.add(role);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        userService.deleteRole(roleId);
        assertFalse(user.getRoles().contains(role));
        assertNull(role.getUser());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Rol silme işlemi: Rol bulunamazsa işlem yapılmamalı")
    void shouldDoNothing_WhenRoleNotFound() {
        Long roleId = 10L;
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());
        userService.deleteRole(roleId);
        verify(userRepository, never()).save(any());
    }

}
