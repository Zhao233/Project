package com.duckduckgogogo.controller;

import com.duckduckgogogo.domain.User;
import com.duckduckgogogo.services.UserService;
import com.duckduckgogogo.utils.PasswordEncodeAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/console/profile")
public class ProfileContriller {

    @Autowired
    private UserService userService;

    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> save(@Valid User user,
                                    @RequestParam String confirmPassword,
                                    HttpServletRequest request)
            throws Exception {
        Map<String, Object> r = new HashMap<>();

        Map<String, String> message = new HashMap<>();
        user = user.converter();
        // "FAILED" "SUCCEED"
        User mark = userService.findById(user.getId());
        User logged = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (mark == null || (mark.getId() != logged.getId())) {
            message.put("WARNING", "Oh snap! You can only modify yourself.");
        } else {
            user.setRole(mark.getRole());
            user.setEnabled(mark.isEnabled());

            if (user.getPassword().isEmpty() && confirmPassword.isEmpty()) {
                user.setPassword(mark.getPassword());
            } else {
                if (user.getPassword() != null && !user.getPassword().isEmpty()
                        && confirmPassword != null && !confirmPassword.trim().isEmpty()) {
                    String regEx = "[\\s\\S]{5,31}";
                    boolean ok = Pattern.compile(regEx).matcher(user.getPassword()).matches();
                    if (ok) {
                        if (!user.getPassword().equals(confirmPassword)) {
                            message.put("password", "Oh snap! Inconsistency of ciphers.");
                        }
                    } else {
                        message.put("password", "Oh snap! 6-30 letters.");
                    }
                } else {
                    message.put("password", "Oh snap! Can't be empty.");
                }
                user.setPassword(PasswordEncodeAssistant.encode(user.getPassword().toCharArray()));
            }
            if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                String regEx = "[A-Za-z0-9_-][A-Za-z0-9_-]{3,31}";
                boolean ok = Pattern.compile(regEx).matcher(user.getUsername()).matches();
                if (ok) {
                    mark = userService.findByUsername(user.getUsername());
                    if (mark != null && user.getId() != mark.getId())
                        message.put("username", "Oh snap! Already existed.");
                } else {
                    message.put("username", "Oh snap! 4-30 letters,and must be A-Z,a-z,0-9 or _ or -");
                }
            } else {
                message.put("username", "Oh snap! Can't be empty.");
            }
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                String regEx = "(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+){1,200}";
                boolean ok = Pattern.compile(regEx).matcher(user.getEmail()).matches();
                if (ok) {
                    mark = userService.findByEmail(user.getEmail());
                    if (mark != null && user.getId() != mark.getId()) message.put("email", "Oh snap! Already existed.");
                } else {
                    message.put("email", "Oh snap! Not a valid mailbox(<200).");
                }
            } else {
                message.put("email", "Oh snap! Can't be empty.");
            }
            if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
                String regEx = "[A-Za-z0-9_-][A-Za-z0-9_-]{0,31}";
                boolean ok = Pattern.compile(regEx).matcher(user.getFirstName()).matches();
                if (!ok) {
                    message.put("firstName", "Oh snap! 1-30 letters,and must be A-Z,a-z,0-9 or _ or -");
                }
            } else {
                message.put("firstName", "Oh snap! Can't be empty.");
            }
            if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                String regEx = "[A-Za-z0-9_-][A-Za-z0-9_-]{0,31}";
                boolean ok = Pattern.compile(regEx).matcher(user.getLastName()).matches();
                if (!ok) {
                    message.put("lastName", "Oh snap! 1-30 letters,and must be A-Z,a-z,0-9 or _ or -");
                }
            } else {
                message.put("lastName", "Oh snap! Can't be empty.");
            }
        }

        if (message.isEmpty()) {
            userService.save(user);
            request.getSession().setAttribute("user", user);
            r.put("status", "SUCCEED");
        } else {
            r.put("status", "FAILED");
            r.put("message", message);
        }

        return r;
    }
}
