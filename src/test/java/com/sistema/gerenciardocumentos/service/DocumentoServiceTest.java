package com.sistema.gerenciardocumentos.service;

import com.sistema.gerenciardocumentos.model.Documento;
import com.sistema.gerenciardocumentos.repository.DocumentoRepository;
import com.sistema.gerenciardocumentos.service.DocumentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DocumentoServiceTest {

    //mockando repository para nao usar o BD diretamente
    @Mock
    private DocumentoRepository repository;

    @Mock
    private ResourceLoader resourceLoader;

    private DocumentoService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new DocumentoService(repository, resourceLoader);
        service.setPastaUpload("/uploads-test");
    }

    @Test
    void salvarDocumento_deveSalvarEretornarDocumento() throws IOException {
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "arquivo-teste.txt",
                "text/plain",
                "conteudo do arquivo".getBytes()
        );

        Documento docSalvo = new Documento();
        docSalvo.setId(1L);
        docSalvo.setNome("teste");
        docSalvo.setCaminhoArquivo("/tmp/uploads/arquivo-teste.txt");
        docSalvo.setTamanho(arquivo.getSize());

        //configurando o que o mock deve devolver, no caso o arquivo que criei
        when(repository.save(any())).thenReturn(docSalvo);

        Documento resultado = service.salvarDocumento("teste", arquivo);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("teste", resultado.getNome());
        assertTrue(resultado.getCaminhoArquivo().contains("teste"));
        assertEquals(arquivo.getSize(), resultado.getTamanho());

        verify(repository, times(1)).save(any());
    }
}
