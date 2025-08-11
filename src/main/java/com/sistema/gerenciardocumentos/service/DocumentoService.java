package com.sistema.gerenciardocumentos.service;

import com.sistema.gerenciardocumentos.model.Documento;
import com.sistema.gerenciardocumentos.repository.DocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class DocumentoService {

    private final DocumentoRepository repository;

    @Value("${app.upload-dir}")
    private String pastaUpload;

    public DocumentoService(DocumentoRepository repository) {
        this.repository = repository;
    }

    public Documento salvarDocumento(String nome, MultipartFile arquivo) throws IOException {
        // Criar diretório se não existir
        Path pasta = Paths.get(System.getProperty("user.dir"), pastaUpload);
        if (!Files.exists(pasta)) {
            Files.createDirectories(pasta);
        }

        // Extrair extensão original (se existir)
        String original = arquivo.getOriginalFilename();
        String extension = "";
        if (original != null && original.contains(".")) {
            extension = original.substring(original.lastIndexOf("."));
        }

        // Criar nome único para evitar conflito de nomes
        String nomeArquivoUnico = nome + "_" +System.currentTimeMillis() + extension;

        // Caminho físico final
        Path destino = pasta.resolve(nomeArquivoUnico);

        // Salvar no disco
        arquivo.transferTo(destino.toFile());

        // Salvar no banco
        Documento doc = new Documento();
        doc.setNome(nome);
        doc.setCaminhoArquivo(destino.toAbsolutePath().toString());
        doc.setTamanho(arquivo.getSize());
        doc.setDataCriacao(LocalDateTime.now());

        return repository.save(doc);
    }


    public byte[] buscarArquivo(Long id) throws IOException {
        Documento doc = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado"));

        return Files.readAllBytes(Paths.get(doc.getCaminhoArquivo()));
    }

    public Documento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado"));
    }


    public Documento atualizarDocumento(Long id, MultipartFile novoArquivo) throws IOException {
        Documento doc = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado"));

        Path pasta = Paths.get(System.getProperty("user.dir"), pastaUpload);
        if (!Files.exists(pasta)) {
            Files.createDirectories(pasta);
        }

        String original = novoArquivo.getOriginalFilename();
        String extension = "";
        if (original != null && original.contains(".")) {
            extension = original.substring(original.lastIndexOf("."));
        }

        String novoNomeArquivo = doc.getNome() + "_" +System.currentTimeMillis() + extension;
        Path novoCaminho = pasta.resolve(novoNomeArquivo);

        //apaga arquivo antigo do diretório
        deleteFile(doc.getCaminhoArquivo());

        novoArquivo.transferTo(novoCaminho.toFile());

        doc.setCaminhoArquivo(novoCaminho.toString());
        doc.setTamanho(novoArquivo.getSize());
        doc.setDataCriacao(LocalDateTime.now());

        return repository.save(doc);
    }

    public void deletarDocumento(Long id) throws IOException {
        Documento doc = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado"));

        //Files.deleteIfExists(Paths.get(doc.getCaminhoArquivo()));
        deleteFile(doc.getCaminhoArquivo());
        repository.deleteById(id);
    }


    /**
     * Deleta um arquivo do sistema de arquivos usando o ResourceLoader do Spring.
     *
     * O ResourceLoader fornece abstração para manipulação de recursos (arquivos,
     * classpath, URLs) de forma consistente no ecossistema Spring.
     *
     * @param path Caminho completo do arquivo a ser deletado
     */
    @Autowired
    private ResourceLoader resourceLoader;

    public void deleteFile(String path) {
        try {
            Resource resource = resourceLoader.getResource("file:" + path);
            File file = resource.getFile();
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            System.out.println("Erro ao deletar o diretório");
        }
    }
}
