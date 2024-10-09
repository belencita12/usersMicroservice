package com.miapp.usersmicroservice.service;

import com.miapp.sistemasdistribuidos.dto.ClienteDTO;

import com.miapp.sistemasdistribuidos.dto.TrabajadorDTO;
import com.miapp.sistemasdistribuidos.entity.Cliente;
import com.miapp.sistemasdistribuidos.entity.Trabajador;
import com.miapp.usersmicroservice.dao.IClienteDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private IClienteDAO clienteDAO;

    // Método para obtener todos los clientes y convertirlos a DTOs
    public List<ClienteDTO> getAllClientes() {
        List<Cliente> clientes = clienteDAO.findAll();
        return clientes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Método para obtener un cliente por su ID y convertirlo a DTO
    public ClienteDTO getClienteById(Integer id) {
        Cliente cliente = clienteDAO.findById(id).orElse(null);
        return convertToDTO(cliente);
    }

    // Método para crear un nuevo cliente
    public ClienteDTO createCliente(ClienteDTO clienteDTO) {
        Cliente cliente = convertToEntity(clienteDTO);
        Cliente savedCliente = clienteDAO.save(cliente);
        return convertToDTO(savedCliente);
    }

    public void deleteCliente(Integer clienteId) {
        Cliente cliente = clienteDAO.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        clienteDAO.delete(cliente);
    }

    public ClienteDTO updateCliente(Integer id, ClienteDTO clienteDTO) {
        Cliente clienteExistente = clienteDAO.findById(id).orElse(null);
        if (clienteExistente == null) {
            return null;
        }
        Cliente clienteActualizado = clienteDAO.save(clienteExistente);
        return convertToDTO(clienteActualizado);
    }



    public Page<ClienteDTO> getAllClientes(Pageable pageable) {
        Page<Cliente> clientes = clienteDAO.findAll(pageable);
        List<ClienteDTO> clienteDTOS = clientes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(clienteDTOS, pageable, clientes.getTotalElements());
    }

    private ClienteDTO convertToDTO(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        ClienteDTO dto = new ClienteDTO();
        dto.setClienteId(cliente.getClienteId());
        dto.setUsuarioId(cliente.getUsuario().getUsuarioId());
        return dto;
    }


    private Cliente convertToEntity(ClienteDTO dto) {
        if (dto == null) {
            return null;
        }
        Cliente cliente = new Cliente();
        cliente.setClienteId(dto.getClienteId());
        return cliente;
    }
}