package com.amoa.server.domain.term.repository;

import com.amoa.server.domain.term.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermRepository extends JpaRepository<Term, Long> {
}