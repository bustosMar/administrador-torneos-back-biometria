package com.sistema.torneos.biometria.app.service;

import com.sistema.torneos.biometria.app.facade.HuellaFacade;
import com.sistema.torneos.biometria.app.web.model.response.HuellaResponse;
import com.sistema.torneos.biometria.app.web.model.response.LectorResponse;
import org.springframework.stereotype.Service;

@Service
public class HuellaService {

    private final HuellaFacade huellaFacade;

    public HuellaService(HuellaFacade huellaFacade) {
        this.huellaFacade = huellaFacade;
    }

    public LectorResponse escucharLector() {
        return huellaFacade.escucharLector();
    }

    public HuellaResponse obtenerHuella() {
        return huellaFacade.obtenerHuella();
    }

    public LectorResponse escucharLectorVerificacion() {
	return huellaFacade.escucharLectorVerificacion();
    }

    public HuellaResponse obtenerHuellaVerificacion() {
	return huellaFacade.obtenerHuellaVerificacion();
    }

    public LectorResponse detenerLector() {
	return huellaFacade.detenerLector();
    }
}