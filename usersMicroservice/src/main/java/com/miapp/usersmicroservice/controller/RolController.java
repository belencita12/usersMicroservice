package com.miapp.usersmicroservice.controller;

import com.miapp.usersmicroservice.service.RolService;
import com.miapp.sistemasdistribuidos.dto.RolDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private static final Logger logger = LoggerFactory.getLogger(RolController.class);

    @Autowired
    private RolService rolService;

    @PostMapping
    public ResponseEntity<RolDTO> createRol(@RequestBody RolDTO rolDTO) {
        try {
            RolDTO nuevoRol = rolService.createRol(rolDTO);
            return new ResponseEntity<>(nuevoRol, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            logger.error("Error al crear el rol: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> getRolById(@PathVariable String id) {
        try {
            RolDTO rolDTO = rolService.getRolById(id);
            logger.info("Rol encontrado con ID: {}", id);
            return new ResponseEntity<>(rolDTO, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error("Rol no encontrado con ID: {}", id);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<RolDTO>> getAllRoles() {
        List<RolDTO> roles = rolService.getAllRoles();
        if (roles.isEmpty()) {
            logger.info("No se encontraron roles.");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        logger.info("Roles encontrados: {}", roles.size());
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRol(@PathVariable String id) {
        try {
            rolService.deleteRol(id);
            logger.info("Rol eliminado con ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            logger.error("Error al eliminar el rol con ID: {}", id);
            return new ResponseEntity<String>("Error al eliminar el rol", HttpStatus.BAD_REQUEST);

        }
    }
}
