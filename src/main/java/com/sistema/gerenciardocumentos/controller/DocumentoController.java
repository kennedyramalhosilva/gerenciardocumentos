package com.sistema.gerenciardocumentos.controller;

import com.sistema.gerenciardocumentos.model.Documento;
import com.sistema.gerenciardocumentos.service.DocumentoService;
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

    @PostMapping
    public ResponseEntity<Documento> criar(@RequestParam String nome, @RequestParam MultipartFile arquivo) throws IOException {
        return ResponseEntity.ok(service.salvarDocumento(nome, arquivo));
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<ByteArrayResource> preview(@PathVariable Long id) throws IOException {
        Documento doc = service.buscarPorId(id);
        byte[] dados = Files.readAllBytes(Paths.get(doc.getCaminhoArquivo()));

        String contentTypeString = Files.probeContentType(Paths.get(doc.getCaminhoArquivo()));
        MediaType contentType;

        if (contentTypeString != null) {
            contentType = MediaType.parseMediaType(contentTypeString);
        } else {
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(contentType)
                .body(new ByteArrayResource(dados));
    }

    //para baixar o arquivo, coloque a URL + ID do documento no navegador
    @GetMapping("/{id}/download")
    public ResponseEntity<ByteArrayResource> download(@PathVariable Long id) throws IOException {
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
        return ResponseEntity.ok(service.atualizarDocumento(id, arquivo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws IOException {
        service.deletarDocumento(id);
        return ResponseEntity.noContent().build();
    }
}