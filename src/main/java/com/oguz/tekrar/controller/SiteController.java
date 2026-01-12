package com.oguz.tekrar.controller;


import com.oguz.tekrar.dto.SiteRequest;
import com.oguz.tekrar.dto.SiteResponse;
import com.oguz.tekrar.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.oguz.tekrar.constant.SiteApiPath.*;


@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class SiteController {


    private final SiteService siteService;


    @GetMapping(GET_ALL)
    public ResponseEntity<List<SiteResponse>> getAllSite() {
        return ResponseEntity.ok(siteService.getAll());
    }

    @GetMapping(GET_BY_ID)
    public ResponseEntity<SiteResponse> getSiteById(@PathVariable Long id) {
        return ResponseEntity.ok(siteService.getSiteById(id));
    }

    @PostMapping(CREATE)
    public ResponseEntity<SiteResponse> createSite(@RequestBody SiteRequest dto) {
        return ResponseEntity.ok(siteService.create(dto));
    }

    @DeleteMapping(DELETE)
    public ResponseEntity<Void> deleteSiteById(@PathVariable Long id) {
        siteService.deleteSite(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping(UPDATE)
    public ResponseEntity<SiteResponse> updateSiteById(@RequestBody SiteRequest dto, @PathVariable Long id) {
        return ResponseEntity.ok(siteService.updateSite(dto, id));
    }
}
