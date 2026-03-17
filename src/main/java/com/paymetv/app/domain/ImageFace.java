package com.paymetv.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "image_face")
public class ImageFace {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "front", nullable = false)
    private String front;

    @ManyToOne
    @JoinColumn(name = "artifact_id", nullable = false)
    private Artifact artifact;

    //    @NotNull
//    private int artifact_id;
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
}
