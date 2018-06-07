package com.duckduckgogogo.services.impl;

import com.duckduckgogogo.services.TraceService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("traceService")
@Transactional
public abstract class TraceServiceImpl implements TraceService {
}
