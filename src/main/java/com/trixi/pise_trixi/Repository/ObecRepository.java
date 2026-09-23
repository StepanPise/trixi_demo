package com.trixi.pise_trixi.Repository;

import com.trixi.pise_trixi.Entity.Obec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObecRepository extends JpaRepository<Obec, Long> {

}
