package com.sistema.gerenciardocumentos.service;

import com.sistema.gerenciardocumentos.model.Documento;
import com.sistema.gerenciardocumentos.repository.DocumentoRepository;
//import lombok.extern.slf4j.Slf4j; declarei manualmente o log4j, dava pra usar debaixo dos panos também com lombok
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

@Service
public class DocumentoService {

    private final DocumentoRepository repository;
    private final ResourceLoader resourceLoader;
    private static final Logger logger = LogManager.getLogger(DocumentoService.class);

    @Setter
    @Value("${app.upload-dir}")
    private String pastaUpload;

    public DocumentoService(DocumentoRepository repository, ResourceLoader resourceLoader) {
        this.repository = repository;
        this.resourceLoader = resourceLoader;
    }

    public Documento salvarDocumento(String nome, MultipartFile arquivo) throws IOException {
        Path pasta = Paths.get(System.getProperty("user.dir"), pastaUpload);
        if (!Files.exists(pasta)) {
            Files.createDirectories(pasta);
            logger.info("Diretório de upload criado em {}", pasta.toAbsolutePath());
        }

        String original = arquivo.getOriginalFilename();
        String extension = (original != null && original.contains(".")) ?
                original.substring(original.lastIndexOf(".")) : "";

        String nomeArquivoUnico = nome + "_" + System.currentTimeMillis() + extension;
        Path destino = pasta.resolve(nomeArquivoUnico);

        arquivo.transferTo(destino.toFile());
        logger.info("Arquivo salvo em {}", destino.toAbsolutePath());

        Documento doc = new Documento();
        doc.setNome(nome);
        doc.setCaminhoArquivo(destino.toAbsolutePath().toString());
        doc.setTamanho(arquivo.getSize());
        doc.setDataCriacao(LocalDateTime.now());

        return repository.save(doc);
    }

    public byte[] buscarArquivo(Long id) throws IOException {
        Documento doc = buscarPorId(id);
        logger.debug("Lendo arquivo do documento id={}", id);
        return Files.readAllBytes(Paths.get(doc.getCaminhoArquivo()));
    }

    public Documento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Documento id={} não encontrado", id);
                    return new RuntimeException("Documento não encontrado");
                });
    }

    public Documento atualizarDocumento(Long id, MultipartFile novoArquivo) throws IOException {
        Documento doc = buscarPorId(id);

        Path pasta = Paths.get(System.getProperty("user.dir"), pastaUpload);
        if (!Files.exists(pasta)) {
            Files.createDirectories(pasta);
        }

        String original = novoArquivo.getOriginalFilename();
        String extension = (original != null && original.contains(".")) ?
                original.substring(original.lastIndexOf(".")) : "";

        String novoNomeArquivo = doc.getNome() + "_" + System.currentTimeMillis() + extension;
        Path novoCaminho = pasta.resolve(novoNomeArquivo);

        deleteFile(doc.getCaminhoArquivo());
        novoArquivo.transferTo(novoCaminho.toFile());

        doc.setCaminhoArquivo(novoCaminho.toString());
        doc.setTamanho(novoArquivo.getSize());
        doc.setDataCriacao(LocalDateTime.now());

        logger.info("Documento id={} atualizado com novo arquivo {}", id, novoNomeArquivo);
        return repository.save(doc);
    }

    public void deletarDocumento(Long id) {
        Documento doc = buscarPorId(id);
        deleteFile(doc.getCaminhoArquivo());
        repository.deleteById(id);
        logger.warn("Documento id={} removido do banco e do disco", id);
    }

    private void deleteFile(String path) {
        try {
            Resource resource = resourceLoader.getResource("file:" + path);
            File file = resource.getFile();
            if (file.exists() && file.delete()) {
                logger.info("Arquivo {} deletado com sucesso", path);
            } else {
                logger.warn("Arquivo {} não encontrado para deletar", path);
            }
        } catch (Exception e) {
            logger.error("Erro ao deletar arquivo {}", path, e);
        }
    }
}
