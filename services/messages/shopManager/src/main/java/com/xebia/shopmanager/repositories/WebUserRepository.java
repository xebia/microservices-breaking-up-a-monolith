package com.xebia.shopmanager.repositories;

import com.xebia.shopmanager.domain.WebUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface WebUserRepository extends CrudRepository<WebUser, UUID>{
    List<WebUser> findByUsername(String username);
}
