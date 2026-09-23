package com.trixi.pise_trixi.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CastObce")
@Getter @Setter
@NoArgsConstructor
public class CastObce {

    @Id
    private Long kod;

    private String nazev;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kod_obce", nullable = false)
    private Obec obec;
}
