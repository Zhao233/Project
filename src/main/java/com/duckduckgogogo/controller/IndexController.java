package com.duckduckgogogo.controller;

import com.duckduckgogogo.domain.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class IndexController {

    @RequestMapping(value = {"/", "/login"})
    public ModelAndView toHome(HttpServletRequest request, HttpServletResponse response) {
        if (request.getSession().getAttribute("user") != null) {
            return new ModelAndView("redirect:/console");
        }

        return new ModelAndView("login");
    }

    @RequestMapping("/logged")
    public String logged(HttpServletRequest request, HttpServletResponse response) throws Exception {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user != null) { //将账户密码置空
            user.setPassword(null);
            request.getSession().setAttribute("user", user);
        }

        switch (user.getRole()) {
            case "PM":
                return "redirect:/console/task_allocation";
            case "S":
                return "redirect:/console/task_management";
            case "C":
                return "redirect:/console/task_schedule_tracking";
            case "A":
                return "redirect:/console/user_management";

            default:
                return "redirect:/console";
        }

        /*if(user.getRole().equals("PM"))
        {
        	return "redirect:/console/task_allocation";
        }else if(user.getRole().equals("S")){
        	return "redirect:/console/task_management";
        }else if(user.getRole().equals("C")){
        	return "redirect:/console/task_schedule_tracking";
        }else if(user.getRole().equals("A")){

        }else {
        	return "redirect:/console";
        }*/

    }
}
