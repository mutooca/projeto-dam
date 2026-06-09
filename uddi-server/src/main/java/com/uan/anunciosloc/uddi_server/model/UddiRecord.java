package com.uan.anunciosloc.uddi_server.model;

import java.time.LocalDateTime;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UddiRecord {

    private String serviceName;
    private String serviceUrl;
    private LocalDateTime registadoEm;
    private LocalDateTime ultimoPing;
    
}
