package com.oguz.tekrar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;
    private String name;
    private String surname;
    private String email;
    private Double maas;
    private Integer age;
    private List<RoleDto> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
