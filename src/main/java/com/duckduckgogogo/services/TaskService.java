package com.duckduckgogogo.services;

import com.duckduckgogogo.domain.Task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskService extends JpaRepository<Task, Long> {
    Task findById(long id);

    @Query("select task from Task task where task.enabled = 1 and (task.rtspUrl like %?1% "
            + " or task.receiveUrl like %?1% or task.dbId like %?1% or task.score like %?1%)")
    Page<Task> findAll(String q, Pageable pageable);

    @Query("select task from Task task where task.enabled = 1")
    Page<Task> findAll(Pageable pageable);
}
