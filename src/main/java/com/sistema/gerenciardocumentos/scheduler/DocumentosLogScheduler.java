package com.sistema.gerenciardocumentos.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class DocumentosLogScheduler {

    private static final Logger logger = LogManager.getLogger(DocumentosLogScheduler.class);

    @Value("${app.upload-dir}")
    private String pastaUpload;
    private Path pastaDocumentos;

   @Scheduled(cron = "0 0 0 * * *")
   public void logDocumentosInfo() {
        Path pastaDocumentos = Paths.get(System.getProperty("user.dir"), pastaUpload);
        try {
            if (Files.exists(pastaDocumentos) && Files.isDirectory(pastaDocumentos)) {
                long totalArquivos = Files.list(pastaDocumentos).count();
                long totalBytes = Files.list(pastaDocumentos)
                        .filter(Files::isRegularFile)
                        .mapToLong(p -> {
                            try {
                                return Files.size(p);
                            } catch (IOException e) {
                                logger.error("Erro lendo tamanho do arquivo: " + p, e);
                                return 0L;
                            }
                        }).sum();

                logger.info("Tarefa agendada: Total arquivos = {} | Total bytes = {}", totalArquivos, totalBytes);
            } else {
                logger.warn("Diretório de documentos não encontrado: " + pastaDocumentos.toAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Erro ao executar tarefa agendada de documentos", e);
        }
    }
}
