package com.fixmycity.repository;

import com.fixmycity.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findAllByOrderByCreatedAtDesc();
    List<Issue> findByStatus(String status);
}
