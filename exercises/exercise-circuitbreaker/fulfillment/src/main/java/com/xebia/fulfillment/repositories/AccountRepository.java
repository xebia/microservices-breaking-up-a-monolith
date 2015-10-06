package com.xebia.fulfillment.repositories;

import com.xebia.fulfillment.domain.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, String> {
}
