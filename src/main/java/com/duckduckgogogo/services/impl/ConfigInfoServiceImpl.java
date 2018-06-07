package com.duckduckgogogo.services.impl;

import com.duckduckgogogo.services.ConfigInfoService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("configInfoService")
@Transactional
public abstract class ConfigInfoServiceImpl implements ConfigInfoService {
}
