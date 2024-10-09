package com.miapp.usersmicroservice.service;

import com.miapp.usersmicroservice.dao.IRolDAO;
import com.miapp.usersmicroservice.dao.IUserDAO;
import com.miapp.sistemasdistribuidos.dto.UsuarioCreateDTO;
import com.miapp.sistemasdistribuidos.dto.UsuarioResponseDTO;
import com.miapp.sistemasdistribuidos.entity.Rol;
import com.miapp.sistemasdistribuidos.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private IUserDAO userDAO;

    @Autowired
    private IRolDAO rolDao;

    public UsuarioResponseDTO createUser(UsuarioCreateDTO usuarioCreateDTO) {
        Usuario usuario = convertToEntity(usuarioCreateDTO);
        usuario.setCreatedAt(LocalDateTime.now());
        usuario.setUpdatedAt(LocalDateTime.now());
        try {
            Usuario userSaved = userDAO.save(usuario);
            logger.info("Usuario creado correctamente con ID: {}", userSaved.getUsuarioId());
            return convertToDTO(userSaved);
        } catch (Exception e) {
            logger.error("Error al crear el usuario: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear el usuario, por favor intente nuevamente.");
        }
    }

    @Cacheable(value = "usuarios")
    public List<UsuarioResponseDTO> getAllUsers() {
        logger.info("Recuperando todos los usuarios");
        List<Usuario> usuarios = userDAO.findAll();
        logger.info("Se recuperaron {} usuarios", usuarios.size());
        return usuarios.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Cacheable(value = "usuarios", key = "'user_' + #id")
    public UsuarioResponseDTO getUserById(Integer id) {
        logger.info("Buscando usuario con ID: {}", id);
        Usuario usuario = userDAO.findById(id).orElse(null);
        if (usuario != null) {
            logger.info("Usuario encontrado con ID: {}", id);
        } else {
            logger.warn("Usuario no encontrado con ID: {}", id);
        }
        return convertToDTO(usuario);
    }

    public Page<UsuarioResponseDTO> getAllUsuarios(Pageable pageable) {
        Page<Usuario> usuarios = userDAO.findAll(pageable);
        List<UsuarioResponseDTO> usuariosDTO = usuarios.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(usuariosDTO, pageable, usuarios.getTotalElements());
    }

    @CacheEvict(value = "usuarios", key = "'user_' + #usuarioId")
    public void deleteUser(Integer usuarioId) {
        logger.info("Eliminando usuario con ID: {}", usuarioId);
        try {
            userDAO.deleteById(usuarioId);
            logger.info("Usuario eliminado correctamente con ID: {}", usuarioId);
        } catch (Exception e) {
            logger.error("Error al eliminar el usuario con ID: {} - Error: {}", usuarioId, e.getMessage(), e);
            throw new RuntimeException("Error al eliminar el usuario.");
        }
    }


    @CachePut(value = "usuarios", key = "'user_' + #id")
    public UsuarioResponseDTO updateUser(Integer id, UsuarioCreateDTO usuarioCreateDTO) {
        // Buscar el usuario existente por su ID
        Usuario usuario = userDAO.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar los campos del usuario
        usuario.setNombre(usuarioCreateDTO.getNombre());
        usuario.setEmail(usuarioCreateDTO.getEmail());
        usuario.setTelefono(usuarioCreateDTO.getTelefono());
        usuario.setRolId( rolDao.findById(Integer.parseInt(usuarioCreateDTO.getRolId()))
                .orElseThrow(() -> new RuntimeException("Rol no encontrado")));
        usuario.setDireccion(usuarioCreateDTO.getDireccion());
        usuario.setImgPerfil(usuarioCreateDTO.getImgPerfil());
        usuario.setBio(usuarioCreateDTO.getBio());
        usuario.setActivo(usuarioCreateDTO.getActivo());

        // Si no se proporciona una nueva contraseña, mantener la existente
        if (usuarioCreateDTO.getContrasena() != null && !usuarioCreateDTO.getContrasena().isEmpty()) {
            usuario.setContrasena(usuarioCreateDTO.getContrasena());
        }

        // Guardar el usuario actualizado
        Usuario usuarioActualizado = userDAO.save(usuario);

        // Retornar el DTO de respuesta
        return convertToDTO(usuarioActualizado);
    }

    @CachePut(value = "usuarios", key = "'userEmail_' + #id")
    public UsuarioResponseDTO getUserByEmail(String email) {
        logger.info("Buscando usuario con email: {}", email);

        Usuario usuario = userDAO.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado con el email: {}", email);
                    return new NoSuchElementException("Usuario no encontrado con el email: " + email);
                });

        logger.info("Usuario encontrado: {}", usuario.getEmail());

        // Convertir Usuario a UsuarioResponseDTO
        UsuarioResponseDTO usuarioResponseDTO = convertToDTO(usuario);
        logger.info("Usuario convertido a UsuarioResponseDTO: {}", usuarioResponseDTO);

        return usuarioResponseDTO;
    }

    public void resetPassword(String email, String antiguaContrasena, String nuevaContrasena) {
        logger.info("Restableciendo contraseña para el email: {}", email);

        // Buscar el usuario por email
        Usuario usuario = userDAO.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con el email: " + email));

        // Verificar que la contraseña antigua coincida
        if (!usuario.getContrasena().equals(antiguaContrasena)) {
            logger.error("La contraseña antigua no coincide para el email: {}", email);
            throw new RuntimeException("La contraseña antigua no coincide.");
        }

        // Cambiar la contraseña
        usuario.setContrasena(nuevaContrasena);
        userDAO.save(usuario);
        logger.info("Contraseña restablecida para el usuario con email: {}", email);
    }



    private Usuario convertToEntity(UsuarioCreateDTO usuarioCreateDTO) {
        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioCreateDTO.getNombre());
        usuario.setEmail(usuarioCreateDTO.getEmail());
        usuario.setContrasena(usuarioCreateDTO.getContrasena());
        usuario.setTelefono(usuarioCreateDTO.getTelefono());
        usuario.setDireccion(usuarioCreateDTO.getDireccion());
        usuario.setImgPerfil(usuarioCreateDTO.getImgPerfil());
        usuario.setBio(usuarioCreateDTO.getBio());
        usuario.setActivo(usuarioCreateDTO.getActivo());
        // Mapear el rolId al Rol en la entidad
        if (usuarioCreateDTO.getRolId() != null) {
            Integer rolId = Integer.parseInt(usuarioCreateDTO.getRolId());
            Rol rol = rolDao.findById(rolId)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + rolId));
            usuario.setRolId(rol);
        }
        return usuario;
    }


    private UsuarioResponseDTO convertToDTO(Usuario usuario) {
        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO();
        usuarioResponseDTO.setUsuarioId(usuario.getUsuarioId());
        usuarioResponseDTO.setNombre(usuario.getNombre());
        usuarioResponseDTO.setEmail(usuario.getEmail());
        usuarioResponseDTO.setTelefono(usuario.getTelefono());
        usuarioResponseDTO.setDireccion(usuario.getDireccion());
        usuarioResponseDTO.setImgPerfil(usuario.getImgPerfil());
        usuarioResponseDTO.setBio(usuario.getBio());
        usuarioResponseDTO.setActivo(usuario.getActivo());
        usuarioResponseDTO.setCreatedAt(usuario.getCreatedAt());
        usuarioResponseDTO.setUpdatedAt(usuario.getUpdatedAt());
        // Mapear el Rol a rolId en el DTO
        if (usuario.getRolId() != null) {
            usuarioResponseDTO.setRolId(usuario.getRolId().getRolId().toString());
        }
        return usuarioResponseDTO;
    }

}
