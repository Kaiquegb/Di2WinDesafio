package com.di2win.desafio.service;

import com.di2win.desafio.model.Cliente;
import com.di2win.desafio.model.Conta;
import com.di2win.desafio.model.Transacao;
import com.di2win.desafio.repository.ClienteRepository;
import com.di2win.desafio.repository.ContaRepository;
import com.di2win.desafio.repository.TransacaoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;
    private final TransacaoRepository transacaoRepository;

    public ContaService(ContaRepository contaRepository, ClienteRepository clienteRepository, TransacaoRepository transacaoRepository) {
        this.contaRepository = contaRepository;
        this.clienteRepository = clienteRepository;
        this.transacaoRepository = transacaoRepository;
    }

    public Conta criarConta(String cpfCliente, String agencia, String numeroConta) {
        Optional<Cliente> cliente = clienteRepository.findByCpf(cpfCliente);
        if (cliente.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado");
        }

        Conta conta = new Conta();
        conta.setCliente(cliente.get());
        conta.setAgencia(agencia);
        conta.setNumeroConta(numeroConta);
        conta.setSaldo(BigDecimal.ZERO);
        conta.setBloqueada(false);

        return contaRepository.save(conta);
    }

    public Conta deposito(String numeroConta, BigDecimal valor) {
        Conta conta = contaRepository.findByNumeroConta(numeroConta);
        if (conta.isBloqueada()) throw new RuntimeException("Conta bloqueada");

        conta.setSaldo(conta.getSaldo().add(valor));
        contaRepository.save(conta);

        Transacao transacao = new Transacao();
        transacao.setTipo("DEPÓSITO");
        transacao.setValor(valor);
        transacao.setDataHora(LocalDateTime.now());
        transacao.setConta(conta);
        transacaoRepository.save(transacao);

        return conta;
    }

    public Conta saque(String numeroConta, BigDecimal valor) {
        Conta conta = contaRepository.findByNumeroConta(numeroConta);
        if (conta.isBloqueada()) throw new RuntimeException("Conta bloqueada");
        if (conta.getSaldo().compareTo(valor) < 0) throw new RuntimeException("Saldo insuficiente");

        conta.setSaldo(conta.getSaldo().subtract(valor));
        contaRepository.save(conta);

        Transacao transacao = new Transacao();
        transacao.setTipo("SAQUE");
        transacao.setValor(valor);
        transacao.setDataHora(LocalDateTime.now());
        transacao.setConta(conta);
        transacaoRepository.save(transacao);

        return conta;
    }

    public void bloquearConta(String numeroConta) {
        Conta conta = contaRepository.findByNumeroConta(numeroConta);
        conta.setBloqueada(true);
        contaRepository.save(conta);
    }

    public List<Transacao> extratoPorPeriodo(Long contaId, LocalDateTime inicio, LocalDateTime fim) {
        return transacaoRepository.findByContaIdAndDataHoraBetween(contaId, inicio, fim);
    }
}
