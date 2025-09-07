package com.di2win.desafio.controller;

import com.di2win.desafio.model.Conta;
import com.di2win.desafio.model.Transacao;
import com.di2win.desafio.service.ContaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping
    public ResponseEntity<Conta> criarConta(@RequestParam String cpf, @RequestParam String agencia, @RequestParam String numeroConta) {
        return ResponseEntity.ok(contaService.criarConta(cpf, agencia, numeroConta));
    }

    @PostMapping("/{numeroConta}/deposito")
    public ResponseEntity<Conta> deposito(@PathVariable String numeroConta, @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(contaService.deposito(numeroConta, valor));
    }

    @PostMapping("/{numeroConta}/saque")
    public ResponseEntity<Conta> saque(@PathVariable String numeroConta, @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(contaService.saque(numeroConta, valor));
    }

    @PostMapping("/{numeroConta}/bloquear")
    public ResponseEntity<Void> bloquearConta(@PathVariable String numeroConta) {
        contaService.bloquearConta(numeroConta);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idConta}/extrato")
    public ResponseEntity<List<Transacao>> extrato(@PathVariable Long idConta,
                                                   @RequestParam LocalDateTime inicio,
                                                   @RequestParam LocalDateTime fim) {
        return ResponseEntity.ok(contaService.extratoPorPeriodo(idConta, inicio, fim));
    }
}
