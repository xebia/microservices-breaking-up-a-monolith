package com.xebia.shop.repositories;

import org.springframework.data.repository.CrudRepository;

import com.xebia.shop.domain.WebUser;

import java.util.List;
import java.util.UUID;

public interface WebUserRepository extends CrudRepository<WebUser, String>{
    List<WebUser> findByUsername(String username);
    WebUser findByUuid(UUID uuid);
}
