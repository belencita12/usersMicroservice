package com.miapp.usersmicroservice.service;

import com.miapp.usersmicroservice.dao.IRolDAO;
import com.miapp.sistemasdistribuidos.dto.RolDTO;
import com.miapp.sistemasdistribuidos.entity.Rol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class RolService {

    private static final Logger logger = LoggerFactory.getLogger(RolService.class);

    @Autowired
    private IRolDAO rolDAO;

    public RolDTO createRol(RolDTO rolDTO) {
        Rol rol = convertToEntity(rolDTO);
        try {
            Rol rolGuardado = rolDAO.save(rol);
            logger.info("Rol creado correctamente con ID: {}", rolGuardado.getRolId());
            return convertToDTO(rolGuardado);
        } catch (Exception e) {
            logger.error("Error al crear el rol: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear el rol, por favor intente nuevamente.");
        }
    }

    public RolDTO getRolById(String id) {
        logger.info("Buscando rol con ID: {}", id);
        Rol rol = rolDAO.findById(Integer.parseInt(id))
                .orElseThrow(() -> new NoSuchElementException("Rol no encontrado con ID: " + id));
        return convertToDTO(rol);
    }

    public List<RolDTO> getAllRoles() {
        logger.info("Recuperando todos los roles");
        List<Rol> roles = rolDAO.findAll();
        return roles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public void deleteRol(String id) {
        logger.info("Eliminando rol con ID: {}", id);
        try {
            rolDAO.deleteById(Integer.parseInt(id));
            logger.info("Rol eliminado correctamente con ID: {}", id);
        } catch (Exception e) {
            logger.error("Error al eliminar el rol con ID: {} - Error: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error al eliminar el rol.");
        }
    }

    private Rol convertToEntity(RolDTO rolDTO) {
        Rol rol = new Rol();
        rol.setRolId(Integer.parseInt(rolDTO.getRolId()));
        rol.setNombreRol(rolDTO.getNombreRol());
        rol.setDescripcionRol(rolDTO.getDescripcionRol());
        return rol;
    }

    private RolDTO convertToDTO(Rol rol) {
        RolDTO rolDTO = new RolDTO();
        rolDTO.setRolId(String.valueOf(rol.getRolId()));
        rolDTO.setNombreRol(rol.getNombreRol());
        rolDTO.setDescripcionRol(rol.getDescripcionRol());
        return rolDTO;
    }
}
