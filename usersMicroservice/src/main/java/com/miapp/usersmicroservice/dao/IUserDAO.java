package com.miapp.usersmicroservice.dao;

import com.miapp.sistemasdistribuidos.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserDAO extends JpaRepository<Usuario, Integer> {
    Page<Usuario> findAll(Pageable pageable);
    Optional<Usuario> findByEmail(String email);

}
