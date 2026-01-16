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

    private static final Long SITE_ID = 1L;

    @Test
    @DisplayName("GET ALL - Tüm siteleri getirmeli")
    void shouldReturnAllSites() throws Exception {
        when(siteService.getAll()).thenReturn(List.of(getSiteResponse()));
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
        when(siteService.getSiteById(SITE_ID)).thenReturn(getSiteResponse());
        mockMvc.perform(get(SiteApiPath.BASE_URL + SiteApiPath.GET_BY_ID, SITE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(SITE_ID.intValue())))
                .andExpect(jsonPath("$.name", is("Google")));
    }

    @Test
    @DisplayName("CREATE - Yeni site oluşturulmalı")
    void shouldCreateSite() throws Exception {
        when(siteService.create(any(SiteRequest.class))).thenReturn(getSiteResponse());
        mockMvc.perform(post(SiteApiPath.BASE_URL + SiteApiPath.CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getSiteRequest())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(SITE_ID.intValue())))
                .andExpect(jsonPath("$.name", is("Google")));
    }

    @Test
    @DisplayName("UPDATE - Site güncellenmeli")
    void shouldUpdateSite() throws Exception {
        when(siteService.updateSite(any(SiteRequest.class), eq(SITE_ID))).thenReturn(getSiteResponse());
        mockMvc.perform(put(SiteApiPath.BASE_URL + SiteApiPath.UPDATE, SITE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getSiteRequest())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Google")));
    }

    @Test
    @DisplayName("DELETE - Site silinmeli")
    void shouldDeleteSite() throws Exception {
        doNothing().when(siteService).deleteSite(SITE_ID);
        mockMvc.perform(delete(SiteApiPath.BASE_URL + SiteApiPath.DELETE, SITE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    private SiteRequest getSiteRequest() {
        return SiteRequest.builder()
                .name("Google")
                .port("8080")
                .userId(5L)
                .build();
    }

    private SiteResponse getSiteResponse() {
        return SiteResponse.builder()
                .id(SITE_ID)
                .name("Google")
                .port("8080")
                .build();
    }
}