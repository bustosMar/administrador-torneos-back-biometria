package com.sistema.torneos.biometria.app.web.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class HuellaResponse {

	private boolean success;
    private String mensaje;
    private String dedo;
    private String templateBase64;

}
