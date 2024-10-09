package com.miapp.usersmicroservice.controller;

import com.miapp.sistemasdistribuidos.dto.UsuarioResponseDTO;
import com.miapp.usersmicroservice.service.TrabajadorService;
import com.miapp.sistemasdistribuidos.dto.TrabajadorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/trabajadores")
public class TrabajadorController {

    private static final Logger logger = LoggerFactory.getLogger(TrabajadorController.class);

    @Autowired
    private TrabajadorService trabajadorService;

    // Crear un nuevo trabajador
    @PostMapping
    public ResponseEntity<TrabajadorDTO> createTrabajador(@RequestBody TrabajadorDTO trabajadorDTO) {
        try {
            TrabajadorDTO nuevoTrabajador = trabajadorService.createTrabajador(trabajadorDTO);
            logger.info("Trabajador creado con éxito, ID: {}", nuevoTrabajador.getTrabajadorId());
            return new ResponseEntity<>(nuevoTrabajador, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error al crear el trabajador: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Obtener todos los trabajadores
    @GetMapping
    public ResponseEntity<List<TrabajadorDTO>> getAllTrabajadores() {
        List<TrabajadorDTO> trabajadores = trabajadorService.getAllTrabajadores();
        if (trabajadores.isEmpty()) {
            logger.info("No se encontraron trabajadores.");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        logger.info("Trabajadores encontrados: {}", trabajadores.size());
        return new ResponseEntity<>(trabajadores, HttpStatus.OK);
    }

    // Obtener un trabajador por ID
    @GetMapping("/{id}")
    public ResponseEntity<TrabajadorDTO> getTrabajadorById(@PathVariable Integer id) {
        try {
            TrabajadorDTO trabajador = trabajadorService.getTrabajadorById(id);
            logger.info("Trabajador encontrado con ID: {}", trabajador.getTrabajadorId());
            return new ResponseEntity<>(trabajador, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error("Trabajador no encontrado con ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/page")
    public Page<TrabajadorDTO> getTrabajadores(
            @PageableDefault(size = 15) Pageable pageable) { // Aquí se usará el tamaño predeterminado configurado
        return trabajadorService.getAllTrabajadores(pageable);
    }

    // Actualizar un trabajador
    @PutMapping("/{id}")
    public ResponseEntity<TrabajadorDTO> updateTrabajador(@PathVariable Integer id, @RequestBody TrabajadorDTO trabajadorDTO) {
        try {
            TrabajadorDTO trabajadorActualizado = trabajadorService.updateTrabajador(id, trabajadorDTO);
            logger.info("Trabajador actualizado con ID: {}", trabajadorActualizado.getTrabajadorId());
            return new ResponseEntity<>(trabajadorActualizado, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error("Error al actualizar el trabajador: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Eliminar un trabajador
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrabajador(@PathVariable Integer id) {
        try {
            trabajadorService.deleteTrabajador(id);
            logger.info("Trabajador eliminado con ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NoSuchElementException e) {
            logger.error("Error al eliminar el trabajador con ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
