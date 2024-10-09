package com.miapp.usersmicroservice.dao;

import com.miapp.sistemasdistribuidos.entity.Rol;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRolDAO extends JpaRepository<Rol, Integer> {
}
