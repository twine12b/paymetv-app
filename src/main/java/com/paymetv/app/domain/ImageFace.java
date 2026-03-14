package com.paymetv.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor  @AllArgsConstructor
@Entity
@Getter @Setter @ToString
@Table(name = "image_face")
public class ImageFace {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    private String front;
    @NotNull
    private int artifact_id;
//    @NonNull
//    private String back_aspect;
//    @NonNull
//    private String left_aspect;
//    @NonNull
//    private String right_aspect;
//    @NonNull
//    private String top_aspect;
//    @NonNull
//    private String bottom_aspect;

//    @NonNull @OneToOne
//    private Artifact product;
}
