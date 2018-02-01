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

    /**
     * ADMINISTRATOR)
     */
    @RequestMapping("/user_management")
    public ModelAndView toUserManagement() {
        return new ModelAndView("console/user_management");
    }

    /**
     * CUSTOMER
     */
    @RequestMapping("/task_schedule_tracking")
    public ModelAndView toTaskScheduleTracking() {
        return new ModelAndView("console/task_schedule_tracking");
    }

    /**
     * SUPPLIER
     */
    @RequestMapping("/task_management")
    public ModelAndView toTaskManagement() {
        return new ModelAndView("console/task_management");
    }

}
