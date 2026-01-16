package com.oguz.tekrar.service;

import com.oguz.tekrar.dto.SiteRequest;
import com.oguz.tekrar.dto.SiteResponse;
import com.oguz.tekrar.entity.Site;
import com.oguz.tekrar.entity.User;
import com.oguz.tekrar.mapper.SiteMapper;
import com.oguz.tekrar.repository.SiteRepository;
import com.oguz.tekrar.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SiteServiceTest {

    @InjectMocks
    SiteService siteService;

    @Mock
    SiteRepository siteRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    SiteMapper mapper;

    private static final Long SITE_ID = 1L;
    private static final Long USER_ID = 5L;

    @Test
    @DisplayName("GET ALL - Tüm siteleri getirmeli")
    void shouldReturnAllSites() {
        List<Site> siteList = List.of(getSite());
        List<SiteResponse> expectedResponse = List.of(getSiteResponse());
        when(siteRepository.findAll()).thenReturn(siteList);
        when(mapper.toSiteResponseList(siteList)).thenReturn(expectedResponse);
        List<SiteResponse> result = siteService.getAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(siteRepository).findAll();
    }

    @Test
    @DisplayName("GET BY ID - ID ile site bulunduğunda SiteResponse dönmeli")
    void shouldReturnSite_WhenIdExists() {
        Site site = getSite();
        SiteResponse expectedResponse = getSiteResponse();
        when(siteRepository.findById(SITE_ID)).thenReturn(Optional.of(site));
        when(mapper.toDto(site)).thenReturn(expectedResponse);
        SiteResponse result = siteService.getSiteById(SITE_ID);
        assertNotNull(result);
        assertEquals("Google", result.getName());
        verify(siteRepository).findById(SITE_ID);
    }

    @Test
    @DisplayName("GET BY ID - ID ile site bulunamazsa EntityNotFoundException fırlatmalı")
    void shouldThrowException_WhenSiteNotFound() {
        when(siteRepository.findById(SITE_ID)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> siteService.getSiteById(SITE_ID));
        assertTrue(exception.getMessage().contains("Aranan Id ile site bulunamadı."));
    }

    @Test
    @DisplayName("CREATE - User mevcutsa site başarıyla oluşturulmalı")
    void shouldCreateSite_WhenUserExists() {
        SiteRequest request = getSiteRequest();
        User user = getUser();
        Site siteToSave = getSite();
        Site savedSite = getSite();
        savedSite.setUser(user);
        SiteResponse expectedResponse = getSiteResponse();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(mapper.toEntity(request)).thenReturn(siteToSave);
        when(siteRepository.save(siteToSave)).thenReturn(savedSite);
        when(mapper.toDto(savedSite)).thenReturn(expectedResponse);
        SiteResponse result = siteService.create(request);
        assertNotNull(result);
        assertEquals(SITE_ID, result.getId());
        verify(userRepository).findById(USER_ID);
        verify(siteRepository).save(siteToSave);
    }

    @Test
    @DisplayName("CREATE - User bulunamazsa EntityNotFoundException fırlatmalı")
    void shouldThrowException_WhenUserNotFound_DuringCreate() {
        SiteRequest request = getSiteRequest();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> siteService.create(request));
        assertEquals("User bulunamadı", exception.getMessage());
        verify(siteRepository, never()).save(any());
    }

    @Test
    @DisplayName("DELETE - Site silme işlemi başarılı olmalı")
    void shouldDeleteSite_WhenIdExists() {
        Site site = getSite(); // Test sınıfınızın altındaki yardımcı metottan site nesnesi alıyoruz
        when(siteRepository.findById(SITE_ID)).thenReturn(Optional.of(site));
        siteService.deleteSite(SITE_ID);
        verify(siteRepository).delete(site);
    }

    @Test
    @DisplayName("DELETE - Silinecek site bulunamazsa EntityNotFoundException fırlatmalı")
    void shouldThrowException_WhenDeletingNonExistingSite() {
        when(siteRepository.findById(SITE_ID)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> siteService.deleteSite(SITE_ID));
        assertTrue(exception.getMessage().contains("site bulunamadı"));
        verify(siteRepository, never()).delete(any());
    }

    @Test
    @DisplayName("UPDATE - Site güncelleme başarılı olmalı")
    void shouldUpdateSite_WhenIdExists() {
        SiteRequest request = getSiteRequest();
        request.setName("Updated Google");
        Site existingSite = getSite();
        Site updatedSite = getSite();
        updatedSite.setName("Updated Google");
        SiteResponse response = getSiteResponse();
        response.setName("Updated Google");
        when(siteRepository.findById(SITE_ID)).thenReturn(Optional.of(existingSite));
        when(siteRepository.save(existingSite)).thenReturn(updatedSite);
        when(mapper.toDto(updatedSite)).thenReturn(response);
        SiteResponse result = siteService.updateSite(request, SITE_ID);
        assertNotNull(result);
        assertEquals("Updated Google", result.getName());
        verify(mapper).updateEntityFromRequest(request, existingSite);
        verify(siteRepository).save(existingSite);
    }

    @Test
    @DisplayName("UPDATE - Güncellenecek site bulunamazsa EntityNotFoundException fırlatmalı")
    void shouldThrowException_WhenUpdatingNonExistingSite() {
        SiteRequest request = getSiteRequest();
        when(siteRepository.findById(SITE_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> siteService.updateSite(request, SITE_ID));
        verify(mapper, never()).updateEntityFromRequest(any(), any());
        verify(siteRepository, never()).save(any());
    }

    private Site getSite() {
        return Site.builder()
                .id(SITE_ID)
                .name("Google")
                .build();
    }

    private User getUser() {
        return User.builder()
                .id(USER_ID)
                .name("Site Owner")
                .build();
    }

    private SiteRequest getSiteRequest() {
        return SiteRequest.builder()
                .name("Google")
                .userId(USER_ID)
                .build();
    }

    private SiteResponse getSiteResponse() {
        return SiteResponse.builder()
                .id(SITE_ID)
                .name("Google")
                .build();
    }
}