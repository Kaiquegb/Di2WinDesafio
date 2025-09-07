package com.di2win.desafio.repository;

import com.di2win.desafio.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaRepository extends JpaRepository<Conta, Long> {
    Conta findByNumeroConta(String numeroConta);
}
