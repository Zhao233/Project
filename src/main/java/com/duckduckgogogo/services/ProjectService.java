package com.duckduckgogogo.services;

import com.duckduckgogogo.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectService extends JpaRepository<Project, Long> {

    @Query("select project from Project project "
            + " where project.name like %?1% or project.worldId like %?1% "
            + " or project.sourceName like %?1% or project.translateName like %?1%")
    public Page<Project> findAll(String q, Pageable pageable);
}
