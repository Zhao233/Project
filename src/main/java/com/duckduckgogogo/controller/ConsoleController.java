package com.duckduckgogogo.controller;

import com.duckduckgogogo.domain.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/console")
public class ConsoleController {

    @RequestMapping
    public ModelAndView toIndex() {
        return new ModelAndView("console/index");
    }

    @RequestMapping("/profile")
    public ModelAndView toProfile() {
        return new ModelAndView("console/profile");
    }

    @RequestMapping("/user_management")
    public ModelAndView toUserManagement() {
        return new ModelAndView("console/user_management");
    }

    @RequestMapping("/task_schedule_tracking")
    public ModelAndView toTaskScheduleTracking() {
        return new ModelAndView("console/task_schedule_tracking");
    }
}
