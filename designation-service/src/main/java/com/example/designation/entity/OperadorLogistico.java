package com.example.designation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class OperadorLogistico {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq_gen")
    @SequenceGenerator(name = "global_seq_gen", sequenceName = "hibernate_sequence", allocationSize = 1, initialValue = 1000)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome; // Ex: "FedEx", "Correios", "Loggi"
}
