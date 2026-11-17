package com.projeto.emprestimos;

import com.projeto.emprestimos.dto.EmprestimoRequest;
import com.projeto.emprestimos.exception.ConflitoException;
import com.projeto.emprestimos.model.Colaborador;
import com.projeto.emprestimos.model.Equipamento;
import com.projeto.emprestimos.model.TipoEquipamento;
import com.projeto.emprestimos.repository.ColaboradorRepository;
import com.projeto.emprestimos.repository.EmprestimoRepository;
import com.projeto.emprestimos.repository.EquipamentoRepository;
import com.projeto.emprestimos.service.EmprestimoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

/** Lara e Lucas: teste de integração garante a regra R1 no Service com banco H2 isolado. */
@SpringBootTest
@ActiveProfiles("test")
class EmprestimoServiceTest {
    @Autowired private EmprestimoService emprestimoService;
    @Autowired private EquipamentoRepository equipamentoRepository;
    @Autowired private ColaboradorRepository colaboradorRepository;
    @Autowired private EmprestimoRepository emprestimoRepository;

    private Long equipamentoId;
    private Long colaboradorId;

    @BeforeEach
    void prepararDados() {
        emprestimoRepository.deleteAll();
        equipamentoRepository.deleteAll();
        colaboradorRepository.deleteAll();

        Equipamento equipamento = equipamentoRepository.save(
                new Equipamento("NB-TESTE-01", "Notebook de teste", TipoEquipamento.NOTEBOOK));
        Colaborador colaborador = new Colaborador();
        colaborador.setNome("Pessoa de Teste");
        colaborador.setEmail("teste@empresa.com");
        colaborador.setCep("01310100");
        colaborador.setNumero("100");
        colaboradorRepository.save(colaborador);
        equipamentoId = equipamento.getId();
        colaboradorId = colaborador.getId();
    }

    @Test
    void naoPermiteEmprestarEquipamentoQueJaEstaEmprestado() {
        EmprestimoRequest pedido = new EmprestimoRequest(equipamentoId, colaboradorId);
        emprestimoService.emprestar(pedido);
        ConflitoException erro = assertThrows(ConflitoException.class,
                () -> emprestimoService.emprestar(pedido));
        assertEquals("Equipamento indisponível", erro.getMessage());
    }
}
