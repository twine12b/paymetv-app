package com.paymetv.app.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

//    @OneToOne
//    @JsonManagedReference
//    private ImageFace image_faces;

    @ManyToOne
    private Users user;
    private Boolean status;
}
