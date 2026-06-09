package com.anunciosloc.anunciosloc_server.uddi.dto;

import java.time.LocalDateTime;

import lombok.*;

@Data
    @NoArgsConstructor
    @AllArgsConstructor
    public  class UddiRecord {
        private String serviceName;
        private String serviceUrl;
        private LocalDateTime registadoEm;
        private LocalDateTime ultimoPing;
    }