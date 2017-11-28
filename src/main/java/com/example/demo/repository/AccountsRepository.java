package com.example.demo.repository;

import com.example.demo.domain.Account;
import com.example.demo.exception.DuplicateAccountIdException;

public interface AccountsRepository {

  void createAccount(Account account) throws DuplicateAccountIdException;

  Account getAccount(String accountId);

  void clearAccounts();
}
