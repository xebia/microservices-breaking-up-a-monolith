package com.xebia.msa.repositories;

import com.xebia.msa.domain.WebUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface WebUserRepository extends CrudRepository<WebUser, String>{
    List<WebUser> findByUsername(String username);
    WebUser findByUuid(UUID uuid);
}
