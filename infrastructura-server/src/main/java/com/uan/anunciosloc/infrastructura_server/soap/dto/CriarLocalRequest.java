package com.uan.anunciosloc.infrastructura_server.soap.dto;


import jakarta.validation.constraints.*;
import jakarta.xml.bind.annotation.*;
import lombok.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CriarLocalRequest")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CriarLocalRequest {
   @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Email do utilizador é obrigatório")
    @Email(message = "Formato de email inválido")
    private String emailUtilizador;

    private Double latitude;

    private Double longitude;
    
    private Double raio;
    @NotNull(message = "Latitude do utilizador é obrigatória")
    private Double latUtilizador;
    @NotNull(message = "Longitude do utilizador é obrigatória") 
    private Double lonUtilizador;
    private String ssidWifi;
}