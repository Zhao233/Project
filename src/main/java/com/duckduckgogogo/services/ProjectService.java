package com.duckduckgogogo.services;

import com.duckduckgogogo.domain.Project;
import com.duckduckgogogo.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectService extends JpaRepository<Project, Long> {
    Project findById(long id);

    List<Project> findBySupplier(User supplier);

    @Query("select project from Project project "
            + " where project.name like %?1% or project.worldId like %?1% "
            + " or project.sourceName like %?1% or project.translateName like %?1% "
            + " or project.state like %?1%")
    Page<Project> findAll(String q, Pageable pageable);
}
