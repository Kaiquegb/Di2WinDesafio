package com.di2win.desafio.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ClienteDTOs {
    public static class CriarClienteReq {
        @NotBlank public String nome;
        @NotBlank public String cpf;              // pode vir com máscara
        @NotNull public LocalDate dataNascimento;
    }
    public static class ClienteResp {
        public Long id; public String nome; public String cpf; public LocalDate dataNascimento;
    }
}
