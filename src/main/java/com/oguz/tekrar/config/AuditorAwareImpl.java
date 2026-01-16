package com.oguz.tekrar.config;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        // Security olmadığı için geçici olarak sabit bir ID dönüyoruz.
        // İleride buraya SecurityContextHolder mantığını ekleyeceğiz.
        // Şimdilik veritabanına her işlemi "1" numaralı ID yapmış gibi kaydedecek.
        return Optional.of(1L);
    }
}