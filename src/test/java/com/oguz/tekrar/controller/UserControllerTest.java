package com.oguz.tekrar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oguz.tekrar.constant.UserApiPath;
import com.oguz.tekrar.dto.UserRequest;
import com.oguz.tekrar.dto.UserResponse;
import com.oguz.tekrar.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("GET ALL - Tüm kullanıcıları getirmeli")
    void shouldReturnAllUsers() throws Exception {
        UserResponse response = UserResponse.builder().id(1L).name("Ali").email("ali@mail.com").build();
        List<UserResponse> responseList = List.of(response);

        when(userService.getAll()).thenReturn(responseList);

        mockMvc.perform(get(UserApiPath.BASE_URL + UserApiPath.GET_ALL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Ali")));
    }

    @Test
    @DisplayName("GET BY ID - ID ile kullanıcı getirilmeli")
    void shouldReturnUserById() throws Exception {
        Long userId = 1L;
        UserResponse response = UserResponse.builder().id(userId).name("Veli").email("veli@mail.com").build();
        when(userService.getUserById(userId)).thenReturn(response);
        String url = UserApiPath.BASE_URL + UserApiPath.GET_BY_ID.replace("{id}", userId.toString());

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Veli")));
    }

    @Test
    @DisplayName("CREATE - Yeni kullanıcı oluşturulmalı")
    void shouldCreateUser() throws Exception {
        UserRequest request = new UserRequest("Ayse", "Yilmaz", "ayse@mail.com", "pass");
        UserResponse response = UserResponse.builder().id(10L).name("Ayse").email("ayse@mail.com").build();
        when(userService.create(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post(UserApiPath.BASE_URL + UserApiPath.CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("Ayse")));
    }

    @Test
    @DisplayName("UPDATE - Kullanıcı güncellenmeli")
    void shouldUpdateUser() throws Exception {
        Long userId = 1L;
        UserRequest request = new UserRequest("Mehmet", "Demir", "mehmet@mail.com", "pass");
        UserResponse response = UserResponse.builder().id(userId).name("Mehmet").email("mehmet@mail.com").build();

        when(userService.updateUser(any(UserRequest.class), eq(userId))).thenReturn(response);

        String url = UserApiPath.BASE_URL + UserApiPath.UPDATE.replace("{id}", userId.toString());

        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Mehmet")));
    }

    @Test
    @DisplayName("DELETE - Kullanıcı silinmeli")
    void shouldDeleteUser() throws Exception {
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        String url = UserApiPath.BASE_URL + UserApiPath.DELETE.replace("{id}", userId.toString());

        mockMvc.perform(delete(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE ROLE - Rol silinmeli")
    void shouldDeleteRole() throws Exception {
        Long roleId = 5L;
        doNothing().when(userService).deleteRole(roleId);

        String url = UserApiPath.BASE_URL + UserApiPath.DELETE_ROLE.replace("{roleId}", roleId.toString());

        mockMvc.perform(delete(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}