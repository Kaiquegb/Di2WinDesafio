package com.di2win.desafio.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ContaDTOs {

    public static class CriarContaReq { @NotBlank public String cpfCliente; }

    public static class ContaResp {
        public Long id; public String numero; public String agencia;
        public BigDecimal saldo; public boolean bloqueada; public Long clienteId;
    }

    public static class TransacaoReq { @NotNull public BigDecimal valor; }

    public static class SaldoResp { public BigDecimal saldo; public SaldoResp(BigDecimal s){ this.saldo=s; } }

    public static class TransacaoResp {
        public Long id; public String tipo; public BigDecimal valor;
        public LocalDateTime dataHora; public BigDecimal saldoApos;
    }
}
