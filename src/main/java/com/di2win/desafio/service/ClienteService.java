package com.di2win.desafio.service;

import com.di2win.desafio.model.Cliente;
import com.di2win.desafio.repository.ClienteRepository;
import com.di2win.desafio.util.ValidadorCPF;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class ClienteService {

    private final ClienteRepository repo;

    public ClienteService(ClienteRepository repo) { this.repo = repo; }

    public Cliente criarCliente(String nome, String cpf, LocalDate dataNascimento) {
        String soDigitos = ValidadorCPF.somenteDigitos(cpf);
        if (!ValidadorCPF.isValido(soDigitos)) {
            throw new RegraNegocioException("CPF inválido");
        }
        repo.findByCpf(soDigitos).ifPresent(c -> {
            throw new RegraNegocioException("CPF já cadastrado");
        });

        Cliente c = new Cliente();
        c.setNome(nome);
        c.setCpf(soDigitos);
        c.setDataNascimento(dataNascimento);
        return repo.save(c);
    }

    public void removerCliente(Long id) {
        if (!repo.existsById(id)) throw new RecursoNaoEncontradoException("Cliente não encontrado");
        repo.deleteById(id);
    }

    public Cliente buscarPorCpfObrigatorio(String cpf) {
        return repo.findByCpf(ValidadorCPF.somenteDigitos(cpf))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado pelo CPF"));
    }
}
