package com.sistema.gerenciardocumentos.controller;

import com.sistema.gerenciardocumentos.model.Documento;
import com.sistema.gerenciardocumentos.service.DocumentoService;
//import lombok.extern.slf4j.Slf4j; declarei manualmente o log4j, dava pra usar debaixo dos panos também com lombok
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/documentos")
public class DocumentoController {

    private final DocumentoService service;

    public DocumentoController(DocumentoService service) {
        this.service = service;
    }

    private static final Logger logger = LogManager.getLogger(DocumentoService.class);

    @PostMapping
    public ResponseEntity<Documento> criar(@RequestParam String nome, @RequestParam MultipartFile arquivo) throws IOException {
        logger.info("Recebendo requisição para criar documento: {}", nome);
        Documento doc = service.salvarDocumento(nome, arquivo);
        logger.info("Documento criado com sucesso: id={}, nome={}", doc.getId(), doc.getNome());
        return ResponseEntity.ok(doc);
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<ByteArrayResource> preview(@PathVariable Long id) throws IOException {
        logger.info("Gerando preview do documento id={}", id);
        Documento doc = service.buscarPorId(id);
        byte[] dados = Files.readAllBytes(Paths.get(doc.getCaminhoArquivo()));

        String contentTypeString = Files.probeContentType(Paths.get(doc.getCaminhoArquivo()));
        MediaType contentType = (contentTypeString != null) ?
                MediaType.parseMediaType(contentTypeString) : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(contentType)
                .body(new ByteArrayResource(dados));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<ByteArrayResource> download(@PathVariable Long id) throws IOException {
        logger.info("Iniciando download do documento id={}", id);
        Documento doc = service.buscarPorId(id);
        byte[] dados = Files.readAllBytes(Paths.get(doc.getCaminhoArquivo()));
        String nomeArquivo = Paths.get(doc.getCaminhoArquivo()).getFileName().toString();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .body(new ByteArrayResource(dados));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Documento> atualizar(@PathVariable Long id, @RequestParam MultipartFile arquivo) throws IOException {
        logger.info("Atualizando documento id={}", id);
        Documento atualizado = service.atualizarDocumento(id, arquivo);
        logger.info("Documento atualizado com sucesso: id={}", id);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws IOException {
        logger.warn("Solicitação para deletar documento id={}", id);
        service.deletarDocumento(id);
        logger.info("Documento id={} deletado com sucesso", id);
        return ResponseEntity.noContent().build();
    }
}
