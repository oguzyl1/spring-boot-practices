package com.oguz.tekrar.service;

import com.oguz.tekrar.dto.UserNameResponse;
import com.oguz.tekrar.dto.UserRequest;
import com.oguz.tekrar.dto.UserResponse;
import com.oguz.tekrar.dto.UserSearchDto;
import com.oguz.tekrar.entity.Role;
import com.oguz.tekrar.entity.User;
import com.oguz.tekrar.mapper.UserMapper;
import com.oguz.tekrar.repository.RoleRepository;
import com.oguz.tekrar.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
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

    private static final Long USER_ID = 1L;
    private static final Long ROLE_ID = 10L;

    @Test
    @DisplayName("GET ALL - Tüm kullanıcıları getirmeli")
    void shouldReturnAllUsers() {
        List<User> userList = List.of(getUser());
        List<UserResponse> responseList = List.of(getUserResponse());
        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.toUserResponseList(userList)).thenReturn(responseList);
        List<UserResponse> result = userService.getAllUsers();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("GET BY ID - ID ile kullanıcı bulunduğunda UserResponse dönmeli")
    void shouldReturnUser_WhenIdExists() {
        User user = getUser();
        UserResponse expectedResponse = getUserResponse();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(expectedResponse);
        UserResponse result = userService.getUserById(USER_ID);
        assertEquals(USER_ID, result.getId());
        verify(userRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("GET BY ID - Kullanıcı bulunamazsa EntityNotFoundException fırlatmalı")
    void shouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(USER_ID));
        assertTrue(exception.getMessage().contains("User not found with id"));
    }

    @Test
    @DisplayName("CREATE - Kullanıcı oluşturulurken varsayılan ROL atanmalı")
    void shouldCreateUserWithDefaultRole() {
        UserRequest request = getUserRequest();
        User userToSave = getUser();
        userToSave.setRoles(new ArrayList<>());
        User savedUser = getUser();
        savedUser.setRoles(new ArrayList<>());
        savedUser.getRoles().add(getRole());
        UserResponse expectedResponse = getUserResponse();
        when(userMapper.toUser(request)).thenReturn(userToSave);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toUserResponse(savedUser)).thenReturn(expectedResponse);
        UserResponse result = userService.createUser(request);
        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        assertEquals(1, userToSave.getRoles().size());
        assertEquals("ROLE_USER", userToSave.getRoles().get(0).getRoleName());
    }

    @Test
    @DisplayName("DELETE - Kullanıcı silme işlemi başarılı olmalı")
    void shouldDeleteUser_WhenIdExists() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        userService.deleteUser(USER_ID);
        verify(userRepository).deleteById(USER_ID);
    }

    @Test
    @DisplayName("DELETE - Silinecek kullanıcı yoksa EntityNotFoundException fırlatmalı")
    void shouldThrowException_WhenDeletingNonExistingUser() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.deleteUser(USER_ID));
        assertTrue(exception.getMessage().contains("Cannot delete"));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("UPDATE - Kullanıcı güncelleme başarılı olmalı")
    void shouldUpdateUser_WhenIdExists() {
        UserRequest request = getUserRequest();
        User existingUser = getUser();
        User updatedUser = getUser(); // Save sonrası
        UserResponse response = getUserResponse();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toUserResponse(updatedUser)).thenReturn(response);
        UserResponse result = userService.updateUser(request, USER_ID);
        assertNotNull(result);
        verify(userMapper).updateEntityFromRequest(request, existingUser);
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("DELETE ROLE - Rol ve Kullanıcı mevcutsa ilişki kesilmeli")
    void shouldRemoveRoleFromUser_WhenRoleAndUserExist() {
        User user = getUser();
        user.setRoles(new ArrayList<>());
        Role role = getRole();
        role.setUser(user);
        user.getRoles().add(role);
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
        userService.deleteRole(ROLE_ID);
        assertFalse(user.getRoles().contains(role));
        assertNull(role.getUser());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("DELETE ROLE - Rol bulunamazsa EntityNotFoundException fırlatmalı")
    void shouldThrowException_WhenRoleNotFound() {
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.deleteRole(ROLE_ID));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("CUSTOM ALL - Özel sorgu ile tüm kullanıcıları getirmeli")
    void shouldReturnAllUsersCustom() {
        List<User> userList = List.of(getUser());
        when(userRepository.tumKullanicilariGetir()).thenReturn(userList);
        when(userMapper.toUserResponseList(userList)).thenReturn(List.of(getUserResponse()));
        List<UserResponse> result = userService.getAllUsersCustom();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("GET NAMES - Kullanıcı isim listesini dönmeli")
    void shouldReturnUserNames() {
        UserNameResponse userNameResponse = new UserNameResponse("Oguz");
        when(userRepository.findUserNames()).thenReturn(List.of(userNameResponse));
        List<UserNameResponse> result = userService.getUserNames();
        assertEquals(1, result.size());
        assertEquals("Oguz", result.get(0).name());
    }

    @Test
    @DisplayName("SEARCH - Prefix ile kullanıcı aramalı")
    void shouldSearchUsers() {
        String prefix = "Og";
        List<User> userList = List.of(getUser());
        when(userRepository.searchByPrefix(prefix)).thenReturn(userList);
        when(userMapper.toUserResponseList(userList)).thenReturn(List.of(getUserResponse()));
        List<UserResponse> result = userService.searchUsers(prefix);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("ADVANCED SEARCH - İsim ve yaşa göre DTO dönmeli")
    void shouldSearchUsersByNameAndAge() {
        String prefix = "Og";
        Integer age = 25;
        UserSearchDto dto = new UserSearchDto(USER_ID, "Oguz", 25);
        when(userRepository.searchByNameAndAge(prefix, age)).thenReturn(List.of(dto));
        List<UserSearchDto> result = userService.searchUsersByNameAndAge(prefix, age);
        assertEquals(1, result.size());
        assertEquals("Oguz", result.get(0).name());
    }

    @Test
    @DisplayName("SORT DESC - Maaşa göre azalan sıralı kullanıcıları dönmeli")
    void shouldGetUsersBySalaryDesc() {
        List<User> userList = List.of(getUser());
        when(userRepository.maasaGoreAzalanSiraylaGetir()).thenReturn(userList);
        when(userMapper.toUserResponseList(userList)).thenReturn(List.of(getUserResponse()));
        List<UserResponse> result = userService.getUsersBySalaryDesc();
        assertNotNull(result);
    }

    @Test
    @DisplayName("FIND BY NAMES - İsim listesine göre kullanıcıları dönmeli")
    void shouldFindUsersByNames() {
        List<String> names = Arrays.asList("Oguz", "Ali");
        List<User> userList = List.of(getUser());
        when(userRepository.findUsers(names)).thenReturn(userList);
        when(userMapper.toUserResponseList(userList)).thenReturn(List.of(getUserResponse()));
        List<UserResponse> result = userService.findUsersByNames(names);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("COUNT - Yaşa göre kullanıcı sayısını dönmeli")
    void shouldFindCountByAge() {
        Integer age = 20;
        Long expectedCount = 5L;
        when(userRepository.countUsers(age)).thenReturn(expectedCount);
        Long result = userService.findCountByAge(age);
        assertEquals(expectedCount, result);
    }

    @Test
    @DisplayName("MAAS RANGE - Maaş aralığına göre kullanıcıları dönmeli")
    void shouldFindMaasAraligi() {
        Double min = 1000.0;
        Double max = 5000.0;
        List<User> userList = List.of(getUser());
        when(userRepository.findMaasAraligi(min, max)).thenReturn(userList);
        when(userMapper.toUserResponseList(userList)).thenReturn(List.of(getUserResponse()));
        List<UserResponse> result = userService.findMaasAraligi(min, max);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("NULL NAME - İsmi null olan kullanıcıları dönmeli")
    void shouldFindAdiIsNull() {
        User nullNameUser = getUser();
        nullNameUser.setName(null);
        List<User> userList = List.of(nullNameUser);
        when(userRepository.findUserByNameIsNull()).thenReturn(userList);
        when(userMapper.toUserResponseList(userList)).thenReturn(List.of(UserResponse.builder().id(USER_ID).build()));
        List<UserResponse> result = userService.findAdiIsNull();
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("SUM - Maaş toplamını dönmeli")
    void shouldGetMaasSum() {
        Double totalSalary = 50000.0;
        when(userRepository.getMaasSum()).thenReturn(totalSalary);
        Double result = userService.getMaasSum();
        assertEquals(totalSalary, result);
    }

    private User getUser() {
        return User.builder()
                .id(USER_ID)
                .name("Oguz")
                .email("oguz@mail.com")
                .roles(new ArrayList<>())
                .build();
    }

    private UserResponse getUserResponse() {
        return UserResponse.builder()
                .id(USER_ID)
                .name("Oguz")
                .email("oguz@mail.com")
                .build();
    }

    private UserRequest getUserRequest() {
        return UserRequest.builder()
                .name("Oguz")
                .surname("Yilmaz")
                .email("oguz@mail.com")
                .password("pass")
                .build();
    }

    private Role getRole() {
        return Role.builder()
                .id(ROLE_ID)
                .roleName("ROLE_USER")
                .build();
    }
}

