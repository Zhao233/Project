package com.duckduckgogogo.services.impl;

import com.duckduckgogogo.services.UserInfoService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("userInfoService")
@Transactional
public abstract class UserInfoServiceImpl implements UserInfoService {
}
