package com.auriga_tt.repository;

import com.auriga_tt.model.Tournament;
import com.auriga_tt.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findByStatus(Tournament.TournamentStatus status);
    List<Tournament> findByCreatedBy(User createdBy);
    Page<Tournament> findAll(Specification<Tournament> spec, Pageable pageable);
}
