package com.miapp.usersmicroservice.controller;

import com.miapp.sistemasdistribuidos.dto.ClienteDTO;
import com.miapp.sistemasdistribuidos.dto.TrabajadorDTO;
import com.miapp.usersmicroservice.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> getAllClientes() {
        List<ClienteDTO> clientes = clienteService.getAllClientes();
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> getClienteById(@PathVariable("id") Integer id) {
        ClienteDTO cliente = clienteService.getClienteById(id);
        if (cliente != null) {
            return new ResponseEntity<>(cliente, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping
    public ResponseEntity<ClienteDTO> createCliente(@RequestBody ClienteDTO clienteDTO) {
        ClienteDTO nuevoCliente = clienteService.createCliente(clienteDTO);
        return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> updateCliente(
            @PathVariable("id") Integer id, @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO clienteActualizado = clienteService.updateCliente(id, clienteDTO);
        if (clienteActualizado != null) {
            return new ResponseEntity<>(clienteActualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable("id") Integer id) {
        try {
            clienteService.deleteCliente(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/page")
    public Page<ClienteDTO> getTrabajadores(
            @PageableDefault(size = 15) Pageable pageable) { // Aquí se usará el tamaño predeterminado configurado
        return clienteService.getAllClientes(pageable);
    }


}