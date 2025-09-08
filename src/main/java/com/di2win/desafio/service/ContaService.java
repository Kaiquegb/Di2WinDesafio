package com.di2win.desafio.service;

import com.di2win.desafio.model.Cliente;
import com.di2win.desafio.model.Conta;
import com.di2win.desafio.model.Transacao;
import com.di2win.desafio.repository.ClienteRepository;
import com.di2win.desafio.repository.ContaRepository;
import com.di2win.desafio.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ContaService {

    private final ContaRepository contaRepo;
    private final ClienteRepository clienteRepo;
    private final TransacaoRepository transacaoRepo;
    private final BigDecimal limiteDiario;

    public ContaService(ContaRepository contaRepo, ClienteRepository clienteRepo,
                        TransacaoRepository transacaoRepo,
                        @Value("${app.saques.limite-diario:2000.00}") BigDecimal limiteDiario) {
        this.contaRepo = contaRepo;
        this.clienteRepo = clienteRepo;
        this.transacaoRepo = transacaoRepo;
        this.limiteDiario = limiteDiario;
    }

    private String gerarNumeroConta() {
        return String.format("%08d", ThreadLocalRandom.current().nextInt(0, 100_000_000));
    }

    @Transactional
    public Conta criarContaPorCpf(String cpf) {
        Cliente cliente = clienteRepo.findByCpf(cpf.replaceAll("\\D", ""))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado para CPF"));
        Conta conta = new Conta();
        conta.setCliente(cliente);
        conta.setAgencia("0001");
        conta.setNumeroConta(gerarNumeroConta());
        conta.setSaldo(BigDecimal.ZERO);
        conta.setBloqueada(false);
        return contaRepo.save(conta);
    }

    public Conta buscarPorNumero(String numero) {
        Conta c = contaRepo.findByNumeroConta(numero);
        if (c == null) throw new RecursoNaoEncontradoException("Conta não encontrada");
        return c;
    }

    @Transactional
    public Transacao deposito(String numeroConta, BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0)
            throw new RegraNegocioException("Valor de depósito deve ser positivo");

        Conta conta = buscarPorNumero(numeroConta);
        if (conta.isBloqueada()) throw new RegraNegocioException("Conta bloqueada");

        conta.setSaldo(conta.getSaldo().add(valor));
        contaRepo.save(conta);

        Transacao t = new Transacao();
        t.setTipo("DEPOSITO");
        t.setValor(valor);
        t.setDataHora(LocalDateTime.now());
        t.setConta(conta);
        transacaoRepo.save(t);
        return t;
    }

    @Transactional
    public Transacao saque(String numeroConta, BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0)
            throw new RegraNegocioException("Valor de saque deve ser positivo");

        Conta conta = buscarPorNumero(numeroConta);
        if (conta.isBloqueada()) throw new RegraNegocioException("Conta bloqueada");

        // limite diário (somatório de hoje + este saque)
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fim = LocalDate.now().atTime(LocalTime.MAX);

        BigDecimal totalSacadoHoje = transacaoRepo.findByContaIdAndDataHoraBetween(conta.getId(), inicio, fim)
                .stream()
                .filter(t -> "SAQUE".equalsIgnoreCase(t.getTipo()))
                .map(t -> t.getValor() == null ? BigDecimal.ZERO : t.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalSacadoHoje.add(valor).compareTo(limiteDiario) > 0) {
            throw new RegraNegocioException("Limite diário de saque excedido. Limite: " + limiteDiario);
        }

        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new RegraNegocioException("Saldo insuficiente");
        }

        conta.setSaldo(conta.getSaldo().subtract(valor));
        contaRepo.save(conta);

        Transacao t = new Transacao();
        t.setTipo("SAQUE");
        t.setValor(valor);
        t.setDataHora(LocalDateTime.now());
        t.setConta(conta);
        transacaoRepo.save(t);
        return t;
    }

    @Transactional
    public void bloquear(String numeroConta) {
        Conta c = buscarPorNumero(numeroConta);
        c.setBloqueada(true);
        contaRepo.save(c);
    }

    @Transactional
    public void desbloquear(String numeroConta) {
        Conta c = buscarPorNumero(numeroConta);
        c.setBloqueada(false);
        contaRepo.save(c);
    }

    public BigDecimal consultarSaldo(String numeroConta) {
        return buscarPorNumero(numeroConta).getSaldo();
    }

    public List<Transacao> extrato(String numeroConta, LocalDateTime inicio, LocalDateTime fim) {
        Conta c = buscarPorNumero(numeroConta);
        return transacaoRepo.findByContaIdAndDataHoraBetween(c.getId(), inicio, fim);
    }
}
