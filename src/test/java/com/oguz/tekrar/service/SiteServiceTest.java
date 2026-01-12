package com.oguz.tekrar.service;

import com.oguz.tekrar.dto.SiteRequest;
import com.oguz.tekrar.dto.SiteResponse;
import com.oguz.tekrar.entity.Site;
import com.oguz.tekrar.entity.User;
import com.oguz.tekrar.mapper.SiteMapper;
import com.oguz.tekrar.repository.SiteRepository;
import com.oguz.tekrar.repository.UserRepository;
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

    @Test
    @DisplayName("Tüm siteleri getirmeli")
    void shouldReturnAllSites() {
        Site site = Site.builder().id(1L).name("Test Site").build();
        List<Site> siteList = List.of(site);
        List<SiteResponse> expectedResponse = List.of(SiteResponse.builder().id(1L).name("Test Site").build());
        when(siteRepository.findAll()).thenReturn(siteList);
        when(mapper.toSiteResponseList(siteList)).thenReturn(expectedResponse);
        List<SiteResponse> result = siteService.getAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(siteRepository).findAll();
    }

    @Test
    @DisplayName("ID ile site bulunduğunda SiteResponse dönmeli")
    void shouldReturnSite_WhenIdExists() {
        Long siteId = 1L;
        Site site = Site.builder().id(siteId).name("Google").build();
        SiteResponse expectedResponse = SiteResponse.builder().id(siteId).name("Google").build();
        when(siteRepository.findById(siteId)).thenReturn(Optional.of(site));
        when(mapper.toDto(site)).thenReturn(expectedResponse);
        SiteResponse result = siteService.getSiteById(siteId);
        assertEquals("Google", result.getName());
        verify(siteRepository).findById(siteId);
    }

    @Test
    @DisplayName("ID ile site bulunamazsa hata fırlatmalı")
    void shouldThrowException_WhenSiteNotFound() {
        Long siteId = 99L;
        when(siteRepository.findById(siteId)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> siteService.getSiteById(siteId));
        assertEquals("Aranan Id ile site bulunamadı.", exception.getMessage());
    }

    @Test
    @DisplayName("Site oluştururken User bulunduysa işlem başarılı olmalı")
    void shouldCreateSite_WhenUserExists() {
        Long userId = 5L;
        SiteRequest request = SiteRequest.builder().name("New Site").userId(userId).build();
        User user = User.builder().id(userId).name("Site Owner").build();
        Site siteToSave = Site.builder().name("New Site").build();
        Site savedSite = Site.builder().id(1L).name("New Site").user(user).build();
        SiteResponse expectedResponse = SiteResponse.builder().id(1L).name("New Site").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.toEntity(request)).thenReturn(siteToSave);
        when(siteRepository.save(siteToSave)).thenReturn(savedSite);
        when(mapper.toDto(savedSite)).thenReturn(expectedResponse);
        SiteResponse result = siteService.create(request);
        assertNotNull(result);
        assertEquals(user, siteToSave.getUser());
        verify(userRepository).findById(userId);
        verify(siteRepository).save(any(Site.class));
    }

    @Test
    @DisplayName("Site oluştururken User bulunamazsa hata fırlatmalı ve kayıt yapmamalı")
    void shouldThrowException_WhenUserNotFound_DuringCreate() {
        Long userId = 99L;
        SiteRequest request = SiteRequest.builder().name("New Site").userId(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> siteService.create(request));
        assertEquals("User bulunamadı", exception.getMessage());
        verify(siteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Site silme işlemi başarılı olmalı")
    void shouldDeleteSite_WhenIdExists() {
        Long siteId = 1L;
        Site site = Site.builder().id(siteId).build();
        when(siteRepository.findById(siteId)).thenReturn(Optional.of(site));
        siteService.deleteSite(siteId);
        verify(siteRepository).delete(site);
    }

    @Test
    @DisplayName("Silinecek site bulunamazsa hata fırlatmalı")
    void shouldThrowException_WhenDeletingNonExistingSite() {
        Long siteId = 99L;
        when(siteRepository.findById(siteId)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> siteService.deleteSite(siteId));
        assertEquals("Silinmek istenen id ile site bulumadı.", exception.getMessage());
        verify(siteRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Site güncelleme başarılı olmalı")
    void shouldUpdateSite_WhenIdExists() {
        Long siteId = 1L;
        SiteRequest request = SiteRequest.builder().name("Updated Name").build();
        Site existingSite = Site.builder().id(siteId).name("Old Name").build();
        Site updatedSite = Site.builder().id(siteId).name("Updated Name").build();
        SiteResponse response = SiteResponse.builder().id(siteId).name("Updated Name").build();
        when(siteRepository.findById(siteId)).thenReturn(Optional.of(existingSite));
        when(siteRepository.save(existingSite)).thenReturn(updatedSite);
        when(mapper.toDto(updatedSite)).thenReturn(response);
        SiteResponse result = siteService.updateSite(request, siteId);
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        verify(mapper).updateEntityFromRequest(request, existingSite);
        verify(siteRepository).save(existingSite);
    }

    @Test
    @DisplayName("Güncellenecek site bulunamazsa hata fırlatmalı")
    void shouldThrowException_WhenUpdatingNonExistingSite() {
        Long siteId = 99L;
        SiteRequest request = SiteRequest.builder().name("Updated Name").build();
        when(siteRepository.findById(siteId)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> siteService.updateSite(request, siteId));
        assertEquals("Güncellenmek istenen id ile site bulunamadı.", exception.getMessage());
        verify(mapper, never()).updateEntityFromRequest(any(), any());
        verify(siteRepository, never()).save(any());
    }
}