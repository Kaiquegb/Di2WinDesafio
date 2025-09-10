package com.di2win.desafio.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import com.di2win.desafio.model.Cliente;
import com.di2win.desafio.model.Conta;
import com.di2win.desafio.model.Transacao;
import com.di2win.desafio.repository.ClienteRepository;
import com.di2win.desafio.repository.ContaRepository;
import com.di2win.desafio.repository.TransacaoRepository;
import com.di2win.desafio.service.RecursoNaoEncontradoException;
import com.di2win.desafio.service.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

class ContaServiceTest {

    @Mock
    private ContaRepository contaRepo;

    @Mock
    private ClienteRepository clienteRepo;

    @Mock
    private TransacaoRepository transacaoRepo;

    @InjectMocks
    private ContaService contaService;

    private Cliente cliente;
    private Conta conta;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Define o limite diário manualmente para os testes
        ReflectionTestUtils.setField(contaService, "limiteDiario", BigDecimal.valueOf(2000.00));

        cliente = new Cliente();
        cliente.setCpf("12345678900");

        conta = new Conta();
        conta.setCliente(cliente);
        conta.setNumeroConta("00012345");
        conta.setAgencia("0001");
        conta.setSaldo(BigDecimal.valueOf(1000));
        conta.setBloqueada(false);
    }

    @Test
    void deveCriarContaParaClienteExistente() {
        when(clienteRepo.findByCpf("12345678900")).thenReturn(Optional.of(cliente));
        when(contaRepo.save(any(Conta.class))).thenReturn(conta);

        Conta criada = contaService.criarContaPorCpf("123.456.789-00");

        assertNotNull(criada);
        assertEquals("0001", criada.getAgencia());
        assertFalse(criada.isBloqueada());
        verify(contaRepo, times(1)).save(any(Conta.class));
    }

    @Test
    void deveLancarErroAoDepositarValorNegativo() {
        when(contaRepo.findByNumeroConta(anyString())).thenReturn(conta);
        assertThrows(RegraNegocioException.class,
                () -> contaService.deposito("00012345", BigDecimal.valueOf(-100)));
    }

    @Test
    void deveRealizarDepositoComSucesso() {
        when(contaRepo.findByNumeroConta(anyString())).thenReturn(conta);
        when(contaRepo.save(any(Conta.class))).thenReturn(conta);
        when(transacaoRepo.save(any(Transacao.class))).thenReturn(new Transacao());

        Transacao t = contaService.deposito("00012345", BigDecimal.valueOf(200));

        assertEquals("DEPOSITO", t.getTipo());
        assertEquals(BigDecimal.valueOf(1200), conta.getSaldo());
        verify(transacaoRepo, times(1)).save(any(Transacao.class));
    }

    @Test
    void deveLancarErroAoSacarSemSaldo() {
        when(contaRepo.findByNumeroConta(anyString())).thenReturn(conta);
        assertThrows(RegraNegocioException.class,
                () -> contaService.saque("00012345", BigDecimal.valueOf(1200)));
    }

    @Test
    void deveConsultarSaldoCorretamente() {
        when(contaRepo.findByNumeroConta(anyString())).thenReturn(conta);
        BigDecimal saldo = contaService.consultarSaldo("00012345");
        assertEquals(BigDecimal.valueOf(1000), saldo);
    }

    @Test
    void deveRetornarExtrato() {
        when(contaRepo.findByNumeroConta(anyString())).thenReturn(conta);
        when(transacaoRepo.findByContaIdAndDataHoraBetween(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(Collections.emptyList());

        var extrato = contaService.extrato("00012345",
                LocalDateTime.now().minusDays(1), LocalDateTime.now());

        assertTrue(extrato.isEmpty());
    }

    @Test
    void deveBloquearConta() {
        when(contaRepo.findByNumeroConta(anyString())).thenReturn(conta);
        when(contaRepo.save(any(Conta.class))).thenReturn(conta);
        contaService.bloquear("00012345");
        assertTrue(conta.isBloqueada());
        verify(contaRepo, times(1)).save(conta);
    }

    @Test
    void deveDesbloquearConta() {
        conta.setBloqueada(true);
        when(contaRepo.findByNumeroConta(anyString())).thenReturn(conta);
        when(contaRepo.save(any(Conta.class))).thenReturn(conta);
        contaService.desbloquear("00012345");
        assertFalse(conta.isBloqueada());
        verify(contaRepo, times(1)).save(conta);
    }

    @Test
    void devePermitirSaqueDentroDoLimiteDiario() {
        when(contaRepo.findByNumeroConta(anyString())).thenReturn(conta);
        when(transacaoRepo.findByContaIdAndDataHoraBetween(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(contaRepo.save(any(Conta.class))).thenReturn(conta);
        when(transacaoRepo.save(any(Transacao.class))).thenReturn(new Transacao());

        Transacao t = contaService.saque("00012345", BigDecimal.valueOf(500));

        assertEquals("SAQUE", t.getTipo());
        assertEquals(BigDecimal.valueOf(500), conta.getSaldo());
        verify(transacaoRepo, times(1)).save(any(Transacao.class));
    }
}