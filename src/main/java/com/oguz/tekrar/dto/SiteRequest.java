package com.oguz.tekrar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SiteRequest {

    private String name;
    private String port;
    private Long userId;
}
