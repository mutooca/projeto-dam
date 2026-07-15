package com.anunciosloc.anunciosloc_server.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminInfoDTO {
    private String nome;
    private String email;
    private String role;
}