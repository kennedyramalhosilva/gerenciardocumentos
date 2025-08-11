package com.sistema.gerenciardocumentos.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "documentos")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(name = "caminho_arquivo")
    private String caminhoArquivo;

    private long tamanho;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

}