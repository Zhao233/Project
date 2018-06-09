package com.duckduckgogogo.services;

import com.duckduckgogogo.domain.UserInfo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserInfoService extends JpaRepository<UserInfo, Long> {
    UserInfo findByImageid(String imageid);

    UserInfo findById(long id);

    @Query("select userInfo from UserInfo userInfo where userInfo.enabled = 1 and (userInfo.id like %?1% "
            + " or userInfo.name like %?1%)")
    Page<UserInfo> findAll(String q, Pageable pageable);

    @Query("select userInfo from UserInfo userInfo where userInfo.enabled = 1")
    Page<UserInfo> findAll(Pageable pageable);
}
