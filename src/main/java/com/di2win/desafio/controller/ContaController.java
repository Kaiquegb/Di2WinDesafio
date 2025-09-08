package com.di2win.desafio.controller;

import com.di2win.desafio.DTO.ContaDTOs;
import com.di2win.desafio.model.Transacao;
import com.di2win.desafio.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService serv;

    public ContaController(ContaService serv) { this.serv = serv; }

    // criar conta pelo CPF
    @PostMapping
    public ResponseEntity<ContaDTOs.ContaResp> criar(@Valid @RequestBody ContaDTOs.CriarContaReq req) {
        var conta = serv.criarContaPorCpf(req.cpfCliente);
        var resp = new ContaDTOs.ContaResp();
        resp.id = conta.getId(); resp.numero = conta.getNumeroConta(); resp.agencia = conta.getAgencia();
        resp.saldo = conta.getSaldo(); resp.bloqueada = conta.isBloqueada(); resp.clienteId = conta.getCliente().getId();
        return ResponseEntity.status(201).body(resp);
    }

    @PostMapping("/{numero}/deposito")
    public ResponseEntity<ContaDTOs.TransacaoResp> deposito(@PathVariable String numero,
                                                            @Valid @RequestBody ContaDTOs.TransacaoReq req) {
        Transacao t = serv.deposito(numero, req.valor);
        var resp = new ContaDTOs.TransacaoResp();
        resp.id = t.getId(); resp.tipo = t.getTipo(); resp.valor = t.getValor(); resp.dataHora = t.getDataHora();
        resp.saldoApos = serv.consultarSaldo(numero);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{numero}/saque")
    public ResponseEntity<ContaDTOs.TransacaoResp> saque(@PathVariable String numero,
                                                         @Valid @RequestBody ContaDTOs.TransacaoReq req) {
        Transacao t = serv.saque(numero, req.valor);
        var resp = new ContaDTOs.TransacaoResp();
        resp.id = t.getId(); resp.tipo = t.getTipo(); resp.valor = t.getValor(); resp.dataHora = t.getDataHora();
        resp.saldoApos = serv.consultarSaldo(numero);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{numero}/saldo")
    public ResponseEntity<ContaDTOs.SaldoResp> saldo(@PathVariable String numero) {
        return ResponseEntity.ok(new ContaDTOs.SaldoResp(serv.consultarSaldo(numero)));
    }

    @PostMapping("/{numero}/bloquear")
    public ResponseEntity<Void> bloquear(@PathVariable String numero) {
        serv.bloquear(numero);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{numero}/desbloquear")
    public ResponseEntity<Void> desbloquear(@PathVariable String numero) {
        serv.desbloquear(numero);
        return ResponseEntity.noContent().build();
    }

    // extrato por período (diferencial)
    @GetMapping("/{numero}/extrato")
    public ResponseEntity<List<Transacao>> extrato(@PathVariable String numero,
                                                   @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
                                                   @RequestParam("fim")    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(serv.extrato(numero, inicio, fim));
    }
}
