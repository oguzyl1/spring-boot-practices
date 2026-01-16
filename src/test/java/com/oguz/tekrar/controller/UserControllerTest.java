package com.oguz.tekrar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oguz.tekrar.constant.UserApiPath;
import com.oguz.tekrar.dto.UserNameResponse;
import com.oguz.tekrar.dto.UserRequest;
import com.oguz.tekrar.dto.UserResponse;
import com.oguz.tekrar.dto.UserSearchDto;
import com.oguz.tekrar.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
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
    private static final Long userId = 1L;

    @Test
    @DisplayName("GET ALL - Tüm kullanıcıları getirmeli")
    void shouldReturnAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userResponse()));
        mockMvc.perform(get(UserApiPath.BASE_URL + UserApiPath.GET_ALL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test")));
    }

    @Test
    @DisplayName("GET BY ID - ID ile kullanıcı getirilmeli")
    void shouldReturnUserById() throws Exception {
        when(userService.getUserById(userId)).thenReturn(userResponse());
        String url = UserApiPath.BASE_URL + UserApiPath.GET_BY_ID.replace("{id}", userId.toString());

        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test")));
    }

    @Test
    @DisplayName("CREATE - Yeni kullanıcı oluşturulmalı")
    void shouldCreateUser() throws Exception {
        when(userService.createUser(any(UserRequest.class))).thenReturn(userResponse());
        mockMvc.perform(post(UserApiPath.BASE_URL + UserApiPath.CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest())))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test")));
    }

    @Test
    @DisplayName("UPDATE - Kullanıcı güncellenmeli")
    void shouldUpdateUser() throws Exception {
        when(userService.updateUser(any(UserRequest.class), eq(userId))).thenReturn(userResponse());
        String url = UserApiPath.BASE_URL + UserApiPath.UPDATE.replace("{id}", userId.toString());
        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test")));
    }

    @Test
    @DisplayName("DELETE - Kullanıcı silinmeli")
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(userId);
        String url = UserApiPath.BASE_URL + UserApiPath.DELETE.replace("{id}", userId.toString());
        mockMvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE ROLE - Rol silinmeli")
    void shouldDeleteRole() throws Exception {
        Long roleId = 5L;
        doNothing().when(userService).deleteRole(roleId);
        String url = UserApiPath.BASE_URL + UserApiPath.DELETE_ROLE.replace("{roleId}", roleId.toString());
        mockMvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("CUSTOM GET ALL - Custom sorgu ile tüm kullanıcıları getirmeli")
    void shouldReturnAllUsersCustom() throws Exception {
        when(userService.getAllUsersCustom()).thenReturn(List.of(userResponse()));
        mockMvc.perform(get(UserApiPath.BASE_URL + UserApiPath.TUM_KULLANICILARI_GETIR)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET NAMES - Sadece kullanıcı isimlerini getirmeli")
    void shouldReturnUserNames() throws Exception {
        UserNameResponse userNameResponse = new UserNameResponse("Test");
        when(userService.getUserNames()).thenReturn(List.of(userNameResponse));

        mockMvc.perform(get(UserApiPath.BASE_URL + UserApiPath.GET_USER_NAMES)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Test")));
    }

    @Test
    @DisplayName("SEARCH - Prefix ile kullanıcı aramalı")
    void shouldSearchUsersByPrefix() throws Exception {
        String prefix = "Te";
        when(userService.searchUsers(prefix)).thenReturn(List.of(userResponse()));
        mockMvc.perform(get(UserApiPath.BASE_URL + UserApiPath.SEARCH_BY_PART)
                        .param("name", prefix)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("ADVANCED SEARCH - İsim ve yaşa göre DTO dönmeli")
    void shouldSearchAdvanced() throws Exception {
        String prefix = "Te";
        Integer age = 18;
        UserSearchDto searchDto = new UserSearchDto(1L, "Test", 18);
        when(userService.searchUsersByNameAndAge(prefix, age)).thenReturn(List.of(searchDto));
        mockMvc.perform(get(UserApiPath.BASE_URL + UserApiPath.SEARCH_ADVANCED)
                        .param("name", prefix)
                        .param("age", age.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Test")))
                .andExpect(jsonPath("$[0].age", is(18)));
    }

    @Test
    @DisplayName("SORT DESC - Maaşa göre azalan sıralı getirmeli")
    void shouldGetUsersBySalaryDesc() throws Exception {
        when(userService.getUsersBySalaryDesc()).thenReturn(List.of(userResponse()));
        mockMvc.perform(get(UserApiPath.BASE_URL + UserApiPath.GET_MAAS_AZALAN_SIRALAMA)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("FIND BY NAMES (POST) - İsim listesine göre kullanıcıları getirmeli")
    void shouldFindUsersByNames() throws Exception {
        List<String> names = Arrays.asList("Test", "Ali");
        when(userService.findUsersByNames(any())).thenReturn(List.of(userResponse()));
        mockMvc.perform(post(UserApiPath.BASE_URL + UserApiPath.FIND_USERS_BY_ISIMLER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(names))) // Listeyi JSON body olarak atıyoruz
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("COUNT - Yaşa göre kullanıcı sayısı dönmeli")
    void shouldCountUsersByAge() throws Exception {
        Integer age = 20;
        Long count = 5L;
        when(userService.findCountByAge(age)).thenReturn(count);
        mockMvc.perform(get(UserApiPath.BASE_URL + UserApiPath.COUNT_USER_BY_AGE)
                        .param("age", age.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(5)));
    }

    @Test
    @DisplayName("MAAS RANGE - Maaş aralığına göre getirmeli")
    void shouldFindMaasAraligi() throws Exception {
        Double min = 1000.0;
        Double max = 5000.0;
        when(userService.findMaasAraligi(min, max)).thenReturn(List.of(userResponse()));
        mockMvc.perform(get(UserApiPath.BASE_URL + UserApiPath.MAAS_ARALIGINA_GORE_GETIR)
                        .param("min", min.toString())
                        .param("max", max.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("NULL NAME - İsmi null olanları getirmeli")
    void shouldFindUsersNameIsNull() throws Exception {
        UserResponse responseNullName = userResponse();
        responseNullName.setName(null);
        when(userService.findAdiIsNull()).thenReturn(List.of(responseNullName));
        mockMvc.perform(get(UserApiPath.BASE_URL + UserApiPath.FIND_USERS_NAME_IS_NULL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("SUM - Maaş toplamını dönmeli")
    void shouldGetMaasSum() throws Exception {
        Double totalSalary = 150000.0;
        when(userService.getMaasSum()).thenReturn(totalSalary);
        mockMvc.perform(get(UserApiPath.BASE_URL + UserApiPath.GET_MAAS_SUM)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(150000.0)));
    }

    UserRequest userRequest() {
        return UserRequest.builder()
                .name("Test")
                .surname("Test")
                .age(18)
                .password("test123")
                .email("test@gmail.com")
                .build();
    }

    UserResponse userResponse() {
        return UserResponse.builder()
                .id(userId)
                .name("Test")
                .surname("Test")
                .age(18)
                .email("test@gmail.com")
                .build();
    }
}