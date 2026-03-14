package com.paymetv.app.domain;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter @Setter @ToString
@Table(name = "artifact")
public class Artifact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String model; // ML Model once trained

    @OneToOne
    private ImageFace image_faces;

    @ManyToOne
    private Users user;
    private Boolean status;
}
