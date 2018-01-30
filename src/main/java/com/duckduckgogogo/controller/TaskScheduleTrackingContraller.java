package com.duckduckgogogo.controller;

import com.duckduckgogogo.domain.Project;
import com.duckduckgogogo.domain.User;
import com.duckduckgogogo.services.ProjectService;
import com.duckduckgogogo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/console/task_schedule_tracking")
public class TaskScheduleTrackingContraller {
    @Autowired
    private ProjectService projectService;

    @RequestMapping("/search")
    @ResponseBody
    public Page<Project> search(@RequestParam(value = "q", defaultValue = "") String q,
                                @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        Pageable pageable = new PageRequest(offset, limit, new Sort(Sort.Direction.DESC, "id"));
        Page<Project> pg;
        if ("".equals(q.trim())) {
            pg = projectService.findAll(pageable);
        } else {
            // pg = userService.findByUsernameOrFirstNameOrLastNameOrEmail(q, q, q, q, pageable);
            pg = projectService.findAll(q, pageable);
        }

        return pg;
    }
}
