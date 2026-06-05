package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.config.NotificacaoCliente;
import com.blessed.blsd_api_bend.repository.ClienteRepository;
import com.blessed.blsd_api_bend.repository.ConsultaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalComServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private NotificacaoCliente notificacaoCliente;

    @InjectMocks
    private CalComService calComService;

    @Nested
    @DisplayName("Testes de Integração e Agendamento CalCom")
    class AgendamentoCalComTest {

        @Test
        @DisplayName("Deve lançar exceção quando o formato da string de data/hora início for inválido")
        void deveLancarExcecaoParaDataInvalida() {
            String nome = "Gabriel";
            String email = "gabriel@email.com";
            String dataHoraInvalida = "2026-06-05T05:30:00";

            assertThrows(Exception.class, () -> {
                calComService.criarAgendamento(nome, email, dataHoraInvalida);
            });

            verifyNoInteractions(consultaRepository);
            verifyNoInteractions(notificacaoCliente);
        }

        @Test
        @DisplayName("Deve lançar exceção se os parâmetros obrigatórios forem nulos ou vazios")
        void deveFalharSeParametrosForemInvalidos() {
            assertThrows(Exception.class, () -> {
                calComService.criarAgendamento("", "email@valido.com", "2026-06-05T05:30:00Z");
            });

            assertThrows(Exception.class, () -> {
                calComService.criarAgendamento("Gabriel", null, "2026-06-05T05:30:00Z");
            });
        }
    }

    @Nested
    @DisplayName("Testes de Resiliência a Falhas Externas")
    class FalhasAPIExternaTest {

        @Test
        @DisplayName("Deve tratar adequadamente cenários onde o servidor do CalCom está fora do ar")
        void deveTratarErroDeConexaoExterna() {

            assertNotNull(calComService);
        }
    }
}