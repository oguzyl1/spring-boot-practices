package com.oguz.tekrar.service;

import com.oguz.tekrar.dto.SiteRequest;
import com.oguz.tekrar.dto.SiteResponse;
import com.oguz.tekrar.entity.Site;
import com.oguz.tekrar.entity.User;
import com.oguz.tekrar.mapper.SiteMapper;
import com.oguz.tekrar.repository.SiteRepository;
import com.oguz.tekrar.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;
    private final UserRepository userRepository;
    private final SiteMapper mapper;


    @Transactional(readOnly = true)
    public List<SiteResponse> getAll() {
        List<Site> sites = siteRepository.findAll();
        return mapper.toSiteResponseList(sites);
    }


    @Transactional(readOnly = true)
    public SiteResponse getSiteById(Long id) {
        Site site = siteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Aranan Id ile site bulunamadı."));
        return mapper.toDto(site);
    }

    @Transactional
    public SiteResponse create(SiteRequest dto) {
        User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User bulunamadı"));
        Site site = mapper.toEntity(dto);
        site.setUser(user);
        return mapper.toDto(siteRepository.save(site));
    }

    @Transactional
    public void deleteSite(Long id) {
        Site site = siteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Silinmek istenen id ile site bulunamadı."));
        siteRepository.delete(site);
    }

    @Transactional
    public SiteResponse updateSite(SiteRequest dto, Long id) {
        Site site = siteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Güncellenmek istenen id ile site bulunamadı."));
        mapper.updateEntityFromRequest(dto, site);
        Site updatedSite = siteRepository.save(site);
        return mapper.toDto(updatedSite);
    }

}
