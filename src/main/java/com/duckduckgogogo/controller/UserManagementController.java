package com.duckduckgogogo.controller;

import com.duckduckgogogo.domain.User;
import com.duckduckgogogo.services.UserService;
import com.duckduckgogogo.utils.PasswordEncodeAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/console/user_management")
public class UserManagementController {
    @Autowired
    private UserService userService;

    @RequestMapping("/search")
    @ResponseBody
    public Page<User> search(@RequestParam(value = "q", defaultValue = "") String q,
                             @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                             @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        Pageable pageable = new PageRequest(offset, limit, new Sort(Sort.Direction.DESC, "id"));
        Page<User> pg;
        if ("".equals(q.trim())) {
            pg = userService.findAll(pageable);
        } else {
            pg = userService.findAll(q, pageable);
        }

        return pg;
    }

    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> save(@Valid User user, @RequestParam String confirmPassword) throws Exception {
        Map<String, Object> r = new HashMap<>();

        Map<String, String> message = new HashMap<>();
        user = user.converter();
        // "FAILED" "SUCCEED"
        User mark = userService.findById(user.getId());
        User logged = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (mark != null && mark.getId() == logged.getId()) {
            message.put("WARNING", "Oh snap! You can't modify yourself.");
        } else {
            if (mark != null && user.getPassword().isEmpty() && confirmPassword.isEmpty())  {
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
                String regEx = "[A-Za-z][A-Za-z0-9_-]{3,31}";
                boolean ok = Pattern.compile(regEx).matcher(user.getUsername()).matches();
                if (ok) {
                    mark = userService.findByUsername(user.getUsername());
                    if (mark != null && user.getId() != mark.getId()) message.put("username", "Oh snap! Already existed.");
                } else {
                    message.put("username", "Oh snap! 4-30 letters.");
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
                String regEx = "[A-Za-z][A-Za-z0-9_-]{0,31}";
                boolean ok = Pattern.compile(regEx).matcher(user.getFirstName()).matches();
                if (!ok) {
                    message.put("firstName", "Oh snap! 1-30 letters.");
                }
            } else {
                message.put("firstName", "Oh snap! Can't be empty.");
            }
            if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                String regEx = "[A-Za-z][A-Za-z0-9_-]{0,31}";
                boolean ok = Pattern.compile(regEx).matcher(user.getLastName()).matches();
                if (!ok) {
                    message.put("lastName", "Oh snap! 1-30 letters.");
                }
            } else {
                message.put("lastName", "Oh snap! Can't be empty.");
            }
        }


        if (message.isEmpty()) {
            userService.save(user);

            r.put("status", "SUCCEED");
        } else {
            r.put("status", "FAILED");
            r.put("message", message);
        }

        return r;
    }

    @RequestMapping("/get/{id}")
    @ResponseBody
    public User get (@PathVariable Integer id) {
        User user = userService.findById(id.longValue());
        if (user != null) user.setPassword("");
        return user;
    }
}
