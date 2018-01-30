package com.duckduckgogogo.services.impl;

import com.duckduckgogogo.services.ProjectService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("projectService")
@Transactional
public abstract class ProjectServiceImpl implements ProjectService {
}
