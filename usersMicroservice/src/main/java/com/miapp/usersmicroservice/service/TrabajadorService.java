package com.miapp.usersmicroservice.service;

import com.miapp.sistemasdistribuidos.dto.TrabajadorDTO;
import com.miapp.sistemasdistribuidos.dto.UsuarioResponseDTO;
import com.miapp.sistemasdistribuidos.entity.Trabajador;
import com.miapp.sistemasdistribuidos.entity.Usuario;
import com.miapp.usersmicroservice.dao.ITrabajadorDAO;
import com.miapp.usersmicroservice.dao.ITrabajadorDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrabajadorService {

    @Autowired
    private ITrabajadorDAO trabajadorDAO;

    // Método para obtener todos los trabajadores y convertirlos a DTOs
    public List<TrabajadorDTO> getAllTrabajadores() {
        List<Trabajador> trabajadores = trabajadorDAO.findAll();
        return trabajadores.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Método para obtener un trabajador por su ID y convertirlo a DTO
    public TrabajadorDTO getTrabajadorById(Integer id) {
        Trabajador trabajador = trabajadorDAO.findById(id).orElse(null);
        return convertToDTO(trabajador);
    }

    // Método para crear un nuevo trabajador
    public TrabajadorDTO createTrabajador(TrabajadorDTO trabajadorDTO) {
        Trabajador trabajador = convertToEntity(trabajadorDTO);
        Trabajador savedTrabajador = trabajadorDAO.save(trabajador);
        return convertToDTO(savedTrabajador);
    }

    public void deleteTrabajador(Integer trabajadorId) {
        Trabajador trabajador = trabajadorDAO.findById(trabajadorId)
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado"));
        trabajadorDAO.delete(trabajador); // Elimina el trabajador si existe
    }

    public TrabajadorDTO updateTrabajador(Integer id, TrabajadorDTO trabajadorDTO) {
        Trabajador trabajadorExistente = trabajadorDAO.findById(id).orElse(null);

        if (trabajadorExistente == null) {
            return null;
        }

        trabajadorExistente.setNombreTrabajo(trabajadorDTO.getNombreTrabajo());
        trabajadorExistente.setDescripcionTrabajo(trabajadorDTO.getDescripcionTrabajo());
        Trabajador trabajadorActualizado = trabajadorDAO.save(trabajadorExistente);

        return convertToDTO(trabajadorActualizado);
    }

    public Page<TrabajadorDTO> getAllTrabajadores(Pageable pageable) {
        Page<Trabajador> trabajadores = trabajadorDAO.findAll(pageable);
        List<TrabajadorDTO> trabajadoresDTOs = trabajadores.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(trabajadoresDTOs, pageable, trabajadores.getTotalElements());
    }

    // Conversión de Trabajador a TrabajadorDTO
    private TrabajadorDTO convertToDTO(Trabajador trabajador) {
        if (trabajador == null) {
            return null;
        }
        TrabajadorDTO dto = new TrabajadorDTO();
        dto.setTrabajadorId(trabajador.getTrabajadorId());
        dto.setNombreTrabajo(trabajador.getNombreTrabajo());
        dto.setDescripcionTrabajo(trabajador.getDescripcionTrabajo());
        dto.setUsuarioId(trabajador.getUsuarioId().getUsuarioId());
        return dto;
    }

    private Trabajador convertToEntity(TrabajadorDTO dto) {
        if (dto == null) {
            return null;
        }
        Trabajador trabajador = new Trabajador();
        trabajador.setTrabajadorId(dto.getTrabajadorId());
        trabajador.setNombreTrabajo(dto.getNombreTrabajo());
        trabajador.setDescripcionTrabajo(dto.getDescripcionTrabajo());
        return trabajador;
    }
}
