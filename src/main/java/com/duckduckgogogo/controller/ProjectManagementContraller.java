package com.duckduckgogogo.controller;

import com.duckduckgogogo.domain.Project;
import com.duckduckgogogo.domain.ProjectSupplierRelation;
import com.duckduckgogogo.domain.User;
import com.duckduckgogogo.services.ProjectService;
import com.duckduckgogogo.services.ProjectSupplierRelationService;
import com.duckduckgogogo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@Controller
@RequestMapping("/console/project_management")
public class ProjectManagementContraller {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectSupplierRelationService projectSupplierRelationService;

    @RequestMapping("/search")
    @ResponseBody
    public List<Project> search() {
        List<Project> projects = projectService.findAll();
        for (Project task : projects) {
            Set<User> supper = new HashSet<>();

            Set<User> suppliers = task.getSuppliers();
            for (User supplier : suppliers) {
                ProjectSupplierRelation.Embeddabled embeddabled = new  ProjectSupplierRelation.Embeddabled();
                embeddabled.setProject_id(task.getId());
                embeddabled.setUser_id(supplier.getId());
                ProjectSupplierRelation psr = projectSupplierRelationService.findById(embeddabled);
                if (psr != null) {
                    if (psr.getState() == null || psr.getState() != 0) {
                        supper.add(supplier);
                    }
                }
            }
            task.setSuppliers(supper);
        }

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
        project.setSuppliers(null);
        return project;
    }

    @PostMapping("/assign_to")
    @ResponseBody
    public Map<String, Object> assignTo(@Valid Project project, @RequestParam Integer supplier) throws Exception {
        Map<String, Object> r = new HashMap<>();
        Map<String, String> message = new HashMap<>();
        Project mark = projectService.findById(project.getId());

        if (mark != null && (mark.getState().equals(Project.NOT_ASSIGN) || mark.getState().equals(Project.ASSIGNED))) {
            User pm = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            mark.setVersion(project.getVersion());
            mark.setProjectManager(userService.findById(pm.getId()));
            Set<User> suppliers = mark.getSuppliers();
            User user = userService.findById(supplier);
            if (!suppliers.contains(user)) suppliers.add(user);
            mark.setSuppliers(suppliers);
            mark.setAssinged(new Date());
            mark.setState(Project.ASSIGNED);

            ProjectSupplierRelation.Embeddabled embeddabled = new  ProjectSupplierRelation.Embeddabled();
            embeddabled.setProject_id(mark.getId());
            embeddabled.setUser_id(user.getId());
            ProjectSupplierRelation projectSupplierRelation = projectSupplierRelationService.findById(embeddabled);
            if (projectSupplierRelation != null) {
                if (projectSupplierRelation.getState() != null && projectSupplierRelation.getState() == 0) {
                    projectSupplierRelation.setState(null);
                }
                projectSupplierRelationService.save(projectSupplierRelation);
            }
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
        List<Project> tasks = new ArrayList<>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user != null) {
            user = userService.findById(user.getId());
            if (user != null && user.getRole().equals(User.ROLE_SUPPLIER)) {
                tasks = projectService.findBySupplier(user.getId());
                for (Project task : tasks) {
                    Set<User> supper = new HashSet<>();

                    Set<User> suppliers = task.getSuppliers();
                    for (User supplier : suppliers) {
                        ProjectSupplierRelation.Embeddabled embeddabled = new  ProjectSupplierRelation.Embeddabled();
                        embeddabled.setProject_id(task.getId());
                        embeddabled.setUser_id(supplier.getId());
                        ProjectSupplierRelation psr = projectSupplierRelationService.findById(embeddabled);
                        if (psr != null) {
                            if (psr.getState() == null || psr.getState() != 0) {
                                supper.add(supplier);
                            }
                        }
                    }
                    task.setSuppliers(supper);
                }
            }
        }

        return tasks;
    }

    @PostMapping("/rejet")
    @ResponseBody
    public Map<String, Object> rejet(@RequestParam Integer id) throws Exception {
        Map<String, Object> r = new HashMap<>();
        Map<String, String> message = new HashMap<>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user != null) {
            Project mark = projectService.findById(id);

            if (mark != null && (mark.getState().equals(Project.ASSIGNED))) {
                ProjectSupplierRelation.Embeddabled embeddabled = new  ProjectSupplierRelation.Embeddabled();
                embeddabled.setProject_id(mark.getId());
                embeddabled.setUser_id(user.getId());
                ProjectSupplierRelation projectSupplierRelation = projectSupplierRelationService.findById(embeddabled);
                projectSupplierRelation.setState(0);

                projectSupplierRelationService.save(projectSupplierRelation);
            } else {
                message.put("WARNING", "Current state is not editable.");
            }
            if (message.isEmpty()) {
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

    @PostMapping("/commit")
    @ResponseBody
    public Map<String, Object> commit(@RequestParam Integer id) throws Exception {
        Map<String, Object> r = new HashMap<>();
        Map<String, String> message = new HashMap<>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user != null) {
            Project mark = projectService.findById(id);

            if (mark != null && mark.getState().equals(Project.ASSIGNED)) {
                mark.setState(Project.IN_PROGRESS);
                mark.setSuppliers(null);
                mark.setSupplier(userService.findById(user.getId()));
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
