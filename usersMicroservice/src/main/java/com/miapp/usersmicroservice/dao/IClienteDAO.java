package com.miapp.usersmicroservice.dao;

import com.miapp.sistemasdistribuidos.entity.Cliente;
import com.miapp.sistemasdistribuidos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IClienteDAO extends JpaRepository<Cliente, Integer> {

}
