package com.uan.anunciosloc.infrastructura_server.soap.dto;

import jakarta.validation.constraints.*;
import jakarta.xml.bind.annotation.*;
import lombok.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EditarLocalRequest")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EditarLocalRequest {

    @NotBlank(message = "ID do local é obrigatório")
    private String idLocal;

    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    private Double latitude;

    private Double longitude;

    private Double raio;

    private String ssidWifi;

    @NotBlank(message = "Email do utilizador é obrigatório")
    @Email(message = "Formato de email inválido")
    private String emailUtilizador;
}
