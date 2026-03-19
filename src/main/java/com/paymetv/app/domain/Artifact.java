package com.paymetv.app.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "artifact")
public class Artifact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String model; // ML Model once trained

    @ManyToOne
    private Users user;
    private Boolean status;
}
