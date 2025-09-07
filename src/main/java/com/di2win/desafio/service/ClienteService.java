package com.di2win.desafio.service;

import com.di2win.desafio.model.Cliente;
import com.di2win.desafio.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente criarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void removerCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf);
    }
}
