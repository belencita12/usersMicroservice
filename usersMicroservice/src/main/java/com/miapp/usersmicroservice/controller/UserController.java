package com.miapp.usersmicroservice.controller;

import com.miapp.usersmicroservice.service.UserService;
import com.miapp.sistemasdistribuidos.dto.UsuarioCreateDTO;
import com.miapp.sistemasdistribuidos.dto.UsuarioResponseDTO;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@RequestBody UsuarioCreateDTO usuarioCreateDTO) {
        try {
            UsuarioResponseDTO nuevoUsuario = userService.createUser(usuarioCreateDTO);
            return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Puedes personalizar el manejo de errores
        }
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> gerAllUsers() {
        List<UsuarioResponseDTO> usuarios = userService.getAllUsers();
        if (usuarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioById(@PathVariable Integer id) {
        UsuarioResponseDTO usuarioDTO = userService.getUserById(id);
        if (usuarioDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuarioDTO);
    }

    @GetMapping("/page")
    public Page<UsuarioResponseDTO> obtenerUsuarios(
            @PageableDefault(size = 15) Pageable pageable) { // Aquí se usará el tamaño predeterminado configurado
        return userService.getAllUsuarios(pageable);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponseDTO> getUserByEmail(@PathVariable String email) {
        logger.info("Solicitud para obtener usuario por email: {}", email);

        UsuarioResponseDTO usuarioResponseDTO = userService.getUserByEmail(email);

        logger.info("Devolviendo respuesta con el usuario encontrado para el email: {}", email);
        return ResponseEntity.ok(usuarioResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Integer usuarioId) {
        try {
            userService.deleteUser(usuarioId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public UsuarioResponseDTO updateUser(@PathVariable Integer id, @RequestBody UsuarioCreateDTO usuarioCreateDTO) {
        return userService.updateUser(id, usuarioCreateDTO);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email,
                                                @RequestParam String antiguaContrasena,
                                                @RequestParam String nuevaContrasena) {
        try {
            userService.resetPassword(email, antiguaContrasena, nuevaContrasena);
            logger.info("Contraseña restablecida con éxito para el email: {}", email);
            return ResponseEntity.ok("Contraseña restablecida con éxito.");
        } catch (NoSuchElementException e) {
            logger.error("Error al restablecer la contraseña: {}", e.getMessage());
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Error al restablecer la contraseña: {}", e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage()); // 400 para errores de cliente
        } catch (Exception e) {
            logger.error("Error al restablecer la contraseña: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al restablecer la contraseña. Por favor, intente nuevamente.");
        }
    }
}