package com.duckduckgogogo.services;

import com.duckduckgogogo.domain.ConfigInfo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigInfoService extends JpaRepository<ConfigInfo, Long> {
	ConfigInfo findById(long id);
}
