package com.paymetv.app.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing the six faces of an image linked to an Artifact.
 *
 * @author Twine12b
 */
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

    /* The `front` face is mandatory,
    * as it typically represents the primary view of the artifact. */
    @Column(name = "front", nullable = false)
    private String front;

    /* The `back` face is optional,
     * as not all artifacts may have a distinct back image. */
    @Column(name = "`back`", nullable = false)
    private String back;

    /* The `left` face is optional,
     * as not all artifacts may have a distinct left image. */
    @Column(name = "`left`", nullable = false)
    private String left;

    /* The `right` face is optional,
     * as not all artifacts may have a distinct right image. */
    @Column(name = "`right`", nullable = false)
    private String right;

    /* The `top` face is optional,
     * as not all artifacts may have a distinct top image. */
    @Column(name = "`top`", nullable = true)
    private String top;

    /** The `bottom` face is optional,
     * as not all artifacts may have a distinct bottom image. */
    @Column(name = "`bottom`", nullable = true)
    private String bottom;

    /**
     * Many-to-One relationship with Artifact.
     * Each ImageFace is associated with one Artifact,
     * but an Artifact can have multiple ImageFaces (if needed in the future).
     */
    @ManyToOne
    @JoinColumn(name = "artifact_id", nullable = false)
    @JsonBackReference
    private Artifact artifact;
}
