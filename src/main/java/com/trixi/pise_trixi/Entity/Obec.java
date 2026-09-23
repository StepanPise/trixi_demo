package com.trixi.pise_trixi.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Obec")
@Getter @Setter
@NoArgsConstructor
public class Obec {

    @Id
    private Long kod;

    private String nazev;

    @OneToMany(mappedBy = "obec", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CastObce> castiObce = new ArrayList<>();
}
