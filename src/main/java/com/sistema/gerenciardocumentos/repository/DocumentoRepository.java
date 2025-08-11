package com.sistema.gerenciardocumentos.repository;

import com.sistema.gerenciardocumentos.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
}