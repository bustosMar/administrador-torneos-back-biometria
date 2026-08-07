package com.sistema.torneos.biometria.app.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sistema.torneos.biometria.app.web.model.response.HuellaResponse;
import com.sistema.torneos.biometria.app.web.model.response.LectorResponse;
import com.sistema.torneos.biometria.app.service.HuellaService;

@RestController
@RequestMapping("/api/huellas")
@CrossOrigin(origins = {"http://localhost:4200"})
public class HuellaController {

	@Autowired
    private final HuellaService huellaService;
    
    public HuellaController(HuellaService huellaService) {
        this.huellaService = huellaService;
    }

    @PostMapping("/lector/escuchar")
    public LectorResponse escucharLector() {
        return huellaService.escucharLector();
    }

    @PostMapping("/verificacion/lector/escuchar")
    public LectorResponse escucharLectorVerificacion() {
	return huellaService.escucharLectorVerificacion();
    }

    @PostMapping("/lector/detener")
    public LectorResponse detenerLector() {
	return huellaService.detenerLector();
    }

    @GetMapping("/obtener")
    public HuellaResponse obtenerHuella() {
        return huellaService.obtenerHuella();
    }

    @GetMapping("/verificacion/obtener")
    public HuellaResponse obtenerHuellaVerificacion() {
	return huellaService.obtenerHuellaVerificacion();
    }

}
