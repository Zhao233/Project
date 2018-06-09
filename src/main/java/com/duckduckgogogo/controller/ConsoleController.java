package com.duckduckgogogo.controller;

import com.duckduckgogogo.domain.User;
import com.duckduckgogogo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/console")
public class ConsoleController {

    @Autowired
    private UserService userService;

    @RequestMapping
    public ModelAndView toIndex() {
        return new ModelAndView("console/index");
    }

    @RequestMapping("/profile")
    public ModelAndView toProfile(HttpServletRequest request) throws Exception {
        Map<String, Object> model = new HashMap<>();

        User current = (User) request.getSession().getAttribute("user");
        if (current != null) {
            User user = userService.findById(current.getId());
            if (user != null) user.setPassword("");

            model.put("user", user);
        }

        return new ModelAndView("console/profile", model);
    }

    /**
     * PROJECT MANAGEMENT
     */
    @RequestMapping("/task_allocation")
    public ModelAndView toTaskAllocation() {
        return new ModelAndView("console/task_allocation");
    }

    @RequestMapping("/task_allocation_complete")
    public ModelAndView toTaskAllocationComplete() {
        return new ModelAndView("console/task_allocation_complete");
    }

    /**
     * ADMINISTRATOR)
     */
    @RequestMapping("/user_management")
    public ModelAndView toUserManagement() {
        return new ModelAndView("console/user_management");
    }

    /**
     * ADMINISTRATOR)
     */
    @RequestMapping("/employee_management")
    public ModelAndView toEmployeeManagement() {
        return new ModelAndView("console/employee_management");
    }


    /**
     * CUSTOMER
     */
    @RequestMapping("/task_schedule_tracking")
    public ModelAndView toTaskScheduleTracking() {
        return new ModelAndView("console/task_schedule_tracking");
    }

    @RequestMapping("/task_schedule_tracking_complete")
    public ModelAndView toTaskScheduleTrackingComplete() {
        return new ModelAndView("console/task_schedule_tracking_complete");
    }

    /**
     * SUPPLIER
     */
    @RequestMapping("/task_management")
    public ModelAndView toTaskManagement() {
        return new ModelAndView("console/task_management");
    }

    @RequestMapping("/task_management_complete")
    public ModelAndView toTaskManagementComplete() {
        return new ModelAndView("console/task_management_complete");
    }

    @RequestMapping("/auto_task_assignment")
    public ModelAndView toAutoTaskAssignment() {
        return new ModelAndView("console/auto_task_assignment");
    }


    @RequestMapping("/face_upload")
    public ModelAndView toupload_picture() {
        return new ModelAndView("console/face_upload");
    }

    @RequestMapping("/face_register")
    public ModelAndView toface_register() {
        return new ModelAndView("console/face_register");
    }

    @RequestMapping("/face_detect")
    public ModelAndView todetect_picture() {
        return new ModelAndView("console/face_detect");
    }

    @RequestMapping("/face_task")
    public ModelAndView toface_task() {
        return new ModelAndView("console/face_task");
    }

    @RequestMapping("/face_config")
    public ModelAndView toface_config() {
        return new ModelAndView("console/face_config");
    }

    @RequestMapping("/charts")
    public ModelAndView tocharts() {
        return new ModelAndView("console/charts");
    }

    @RequestMapping("/chart")
    public ModelAndView tochart() {
        return new ModelAndView("console/chart");
    }
}
