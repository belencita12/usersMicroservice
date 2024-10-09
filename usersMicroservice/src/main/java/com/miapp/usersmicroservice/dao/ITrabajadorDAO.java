package com.miapp.usersmicroservice.dao;

import com.miapp.sistemasdistribuidos.entity.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITrabajadorDAO extends JpaRepository<Trabajador, Integer> {
}
