package com.di2win.desafio.repository;

import com.di2win.desafio.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByContaIdAndDataHoraBetween(Long contaId, LocalDateTime inicio, LocalDateTime fim);
}
