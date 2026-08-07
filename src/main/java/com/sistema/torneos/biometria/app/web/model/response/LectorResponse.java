package com.sistema.torneos.biometria.app.web.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LectorResponse {

    private Boolean success;

    private String mensaje;
    
    private Boolean escuchando;


}
