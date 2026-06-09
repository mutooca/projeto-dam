package com.uan.anunciosloc.infrastructura_server.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoordenadaWifiInfo {
    private String ssid;
}