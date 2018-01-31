package com.duckduckgogogo.services.impl;

import com.duckduckgogogo.services.UserService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("userService")
@Transactional
public abstract class UserServiceImpl implements UserService {
}
