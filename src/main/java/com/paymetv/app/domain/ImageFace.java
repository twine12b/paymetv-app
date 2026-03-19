package com.paymetv.app.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "image_face")
public class ImageFace {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "front", nullable = false)
    private String front;

    @Column(name = "`back`", nullable = false)
    private String back;

    @Column(name = "`left`", nullable = false)
    private String left;

    @Column(name = "`right`", nullable = false)
    private String right;

    @Column(name = "`top`", nullable = true)
    private String top;

    @Column(name = "`bottom`", nullable = true)
    private String bottom;

    @ManyToOne
    @JoinColumn(name = "artifact_id", nullable = false)
    @JsonBackReference
    private Artifact artifact;
}
