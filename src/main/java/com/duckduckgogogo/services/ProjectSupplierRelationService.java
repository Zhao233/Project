package com.duckduckgogogo.services;

import com.duckduckgogogo.domain.ProjectSupplierRelation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectSupplierRelationService
        extends JpaRepository<ProjectSupplierRelation, ProjectSupplierRelation.Embeddabled> {

    ProjectSupplierRelation findById(ProjectSupplierRelation.Embeddabled id);
}
