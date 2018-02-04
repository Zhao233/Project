package com.duckduckgogogo.services;

import com.duckduckgogogo.domain.AzureFileHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AzureFileHistoryServer extends JpaRepository<AzureFileHistory, Long> {
}
