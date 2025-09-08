package com.di2win.desafio.controller;

import com.di2win.desafio.DTO.ClienteDTOs;
import com.di2win.desafio.model.Cliente;
import com.di2win.desafio.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService serv;

    public ClienteController(ClienteService serv) { this.serv = serv; }

    @PostMapping
    public ResponseEntity<ClienteDTOs.ClienteResp> criar(@Valid @RequestBody ClienteDTOs.CriarClienteReq req) {
        Cliente c = serv.criarCliente(req.nome, req.cpf, req.dataNascimento);
        ClienteDTOs.ClienteResp resp = new ClienteDTOs.ClienteResp();
        resp.id = c.getId(); resp.nome = c.getNome(); resp.cpf = c.getCpf(); resp.dataNascimento = c.getDataNascimento();
        return ResponseEntity.status(201).body(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        serv.removerCliente(id);
        return ResponseEntity.noContent().build();
    }
}
