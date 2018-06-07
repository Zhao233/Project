package com.duckduckgogogo.services.impl;

import com.duckduckgogogo.services.TaskService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("taskService")
@Transactional
public abstract class TaskServiceImpl implements TaskService {
}
