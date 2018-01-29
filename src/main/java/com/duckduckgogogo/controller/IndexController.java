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

        if (user != null) {
            user.setPassword(null);
            request.getSession().setAttribute("user", user);
        }

        return "redirect:/console";
    }
}
