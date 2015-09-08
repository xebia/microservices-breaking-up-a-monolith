package com.xebia.msa.repositories;

import com.xebia.msa.domain.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, String> {
}
