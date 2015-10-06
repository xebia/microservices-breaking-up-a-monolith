package com.xebia.shop.repositories;

import org.springframework.data.repository.CrudRepository;

import com.xebia.shop.domain.Account;

public interface AccountRepository extends CrudRepository<Account, String> {
}
