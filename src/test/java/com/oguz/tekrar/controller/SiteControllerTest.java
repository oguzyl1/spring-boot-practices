package com.oguz.tekrar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oguz.tekrar.constant.SiteApiPath;
import com.oguz.tekrar.dto.SiteRequest;
import com.oguz.tekrar.dto.SiteResponse;
import com.oguz.tekrar.service.SiteService;
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

@WebMvcTest(SiteController.class)
class SiteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SiteService siteService;

    @Test
    @DisplayName("GET ALL - Tüm siteleri getirmeli")
    void shouldReturnAllSites() throws Exception {
        SiteResponse response = SiteResponse.builder().id(1L).name("Google").port("8080").build();
        List<SiteResponse> responseList = List.of(response);
        when(siteService.getAll()).thenReturn(responseList);
        mockMvc.perform(get(SiteApiPath.BASE_URL + SiteApiPath.GET_ALL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Google")));
    }

    @Test
    @DisplayName("GET BY ID - ID ile site getirilmeli")
    void shouldReturnSiteById() throws Exception {
        Long siteId = 1L;
        SiteResponse response = SiteResponse.builder().id(siteId).name("Amazon").port("443").build();
        when(siteService.getSiteById(siteId)).thenReturn(response);
        String url = SiteApiPath.BASE_URL + SiteApiPath.GET_BY_ID.replace("{id}", siteId.toString());
        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Amazon")));
    }

    @Test
    @DisplayName("CREATE - Yeni site oluşturulmalı")
    void shouldCreateSite() throws Exception {
        SiteRequest request = SiteRequest.builder().name("Facebook").port("80").userId(5L).build();
        SiteResponse response = SiteResponse.builder().id(10L).name("Facebook").port("80").build();
        when(siteService.create(any(SiteRequest.class))).thenReturn(response);
        mockMvc.perform(post(SiteApiPath.BASE_URL + SiteApiPath.CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("Facebook")));
    }

    @Test
    @DisplayName("UPDATE - Site güncellenmeli")
    void shouldUpdateSite() throws Exception {
        Long siteId = 1L;
        SiteRequest request = SiteRequest.builder().name("Updated Name").port("9090").build();
        SiteResponse response = SiteResponse.builder().id(siteId).name("Updated Name").port("9090").build();
        when(siteService.updateSite(any(SiteRequest.class), eq(siteId))).thenReturn(response);
        String url = SiteApiPath.BASE_URL + SiteApiPath.UPDATE.replace("{id}", siteId.toString());
        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")));
    }

    @Test
    @DisplayName("DELETE - Site silinmeli")
    void shouldDeleteSite() throws Exception {
        Long siteId = 1L;
        doNothing().when(siteService).deleteSite(siteId);
        String url = SiteApiPath.BASE_URL + SiteApiPath.DELETE.replace("{id}", siteId.toString());
        mockMvc.perform(delete(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}