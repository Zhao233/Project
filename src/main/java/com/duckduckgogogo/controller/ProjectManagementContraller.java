package com.duckduckgogogo.controller;

import com.duckduckgogogo.domain.Project;
import com.duckduckgogogo.domain.User;
import com.duckduckgogogo.services.ProjectService;
import com.duckduckgogogo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/console/project_management")
public class ProjectManagementContraller {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;

    @RequestMapping("/search")
    @ResponseBody
    public List<Project> search() {
        List<Project> projects = projectService.findAll();

        return projects;
    }

    @RequestMapping("/supplier_list")
    @ResponseBody
    public List<User> supplierList() {
        List<User> suppliers = userService.findByRoleAndEnabled("S", true);

        return suppliers;
    }

    @RequestMapping("/get/{id}")
    @ResponseBody
    public Project get (@PathVariable Integer id) {
        Project project = projectService.findById(id);
        return project;
    }

    @PostMapping("/assign_to")
    @ResponseBody
    public Map<String, Object> assignTo(@Valid Project project, @RequestParam Integer supplier) throws Exception {
        Map<String, Object> r = new HashMap<>();
        Map<String, String> message = new HashMap<>();
        Project mark = projectService.findById(project.getId());

        if (mark != null && mark.getState().equals(Project.NOT_ASSIGN)) {
            User pm = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            mark.setVersion(project.getVersion());
            mark.setProjectManager(userService.findById(pm.getId()));
            mark.setSupplier(userService.findById(supplier));
            mark.setAssinged(new Date());
            mark.setState(Project.ASSIGNED);
        } else {
            message.put("WARNING", "Current state is not editable.");
        }
        if (message.isEmpty()) {
            projectService.save(mark);

            r.put("status", "SUCCEED");
        } else {
            r.put("status", "FAILED");
            r.put("message", message);
        }

        return r;
    }

    @PostMapping("/re_open")
    @ResponseBody
    public Map<String, Object> reOpen(@RequestParam Integer id) throws Exception {
        Map<String, Object> r = new HashMap<>();
        Map<String, String> message = new HashMap<>();
        Project mark = projectService.findById(id);

        if (mark != null && mark.getState().equals(Project.PENDING)) {
            mark.setState(Project.IN_PROGRESS);
        } else {
            message.put("WARNING", "Current state is not editable.");
        }
        if (message.isEmpty()) {
            projectService.save(mark);

            r.put("status", "SUCCEED");
        } else {
            r.put("status", "FAILED");
            r.put("message", message);
        }

        return r;
    }

    @PostMapping("/push_back")
    @ResponseBody
    public Map<String, Object> pushBack(@RequestParam Integer id) throws Exception {
        Map<String, Object> r = new HashMap<>();
        Map<String, String> message = new HashMap<>();
        Project mark = projectService.findById(id);

        if (mark != null && mark.getState().equals(Project.PENDING)) {
            mark.setState(Project.COMPLETED);
            mark.setPushback(new Date());
        } else {
            message.put("WARNING", "Current state is not editable.");
        }
        if (message.isEmpty()) {
            projectService.save(mark);

            r.put("status", "SUCCEED");
        } else {
            r.put("status", "FAILED");
            r.put("message", message);
        }

        return r;
    }

    @RequestMapping("/task_list")
    @ResponseBody
    public List<Project> taskList() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user != null) {
            user = userService.findById(user.getId());
            if (user != null && user.getRole().equals(User.ROLE_SUPPLIER)) {
                List<Project> tasks = projectService.findBySupplier(user);

                return tasks;
            }
        }

        return null;
    }

    @PostMapping("/commit")
    @ResponseBody
    public Map<String, Object> commit(@RequestParam Integer id) throws Exception {
        Map<String, Object> r = new HashMap<>();
        Map<String, String> message = new HashMap<>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user != null) {
            Project mark = projectService.findById(id);

            if (mark != null && mark.getSupplier().getId() == user.getId() && mark.getState().equals(Project.ASSIGNED)) {
                mark.setState(Project.IN_PROGRESS);
            } else {
                message.put("WARNING", "Current state is not editable.");
            }
            if (message.isEmpty()) {
                projectService.save(mark);

                r.put("status", "SUCCEED");
            } else {
                r.put("status", "FAILED");
                r.put("message", message);
            }
        } else {
            throw new Exception();
        }

        return r;
    }
}
