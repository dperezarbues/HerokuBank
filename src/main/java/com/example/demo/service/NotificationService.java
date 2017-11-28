package com.example.demo.service;

import com.example.demo.domain.Account;

public interface NotificationService {

  void notifyAboutTransfer(Account account, String transferDescription);
}
