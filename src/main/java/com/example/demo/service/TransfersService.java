package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.Transfer;
import com.example.demo.exception.AccountNotFoundException;
import com.example.demo.repository.TransfersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransfersService {

  private final AccountsService accountsService;

  private final TransfersRepository transfersRepository;

  private final NotificationService notificationService;

  @Autowired
  public TransfersService(AccountsService accountsService, TransfersRepository transfersRepository, NotificationService notificationService) {
    this.accountsService = accountsService;
    this.transfersRepository = transfersRepository;
    this.notificationService = notificationService;
  }

  public Transfer executeTransfer(Transfer transfer) {

    Account senderAccount = this.accountsService.getAccount(transfer.getSenderAccountId());
    Account receiverAccount = this.accountsService.getAccount(transfer.getReceiverAccountId());

    try {
      if (transfer.getStatus() == Transfer.Status.PENDING) {
        if (senderAccount == null) {
          throw new AccountNotFoundException("Not account found with accountID: " + transfer.getSenderAccountId());
        }
        if (receiverAccount == null) {
          throw new AccountNotFoundException("Not account found with accountID: " + transfer.getReceiverAccountId());
        }
        senderAccount.withdraw(transfer.getAmount());
        receiverAccount.deposit(transfer.getAmount());
        transfer.setStatus(Transfer.Status.COMPLETED);
      } else {
        transfer.setFailureCause("Retrying an already " + transfer.getStatus().getDescription() + " transfer");
      }
    } catch (RuntimeException r) {
      transfer.setFailureCause(r.getMessage());
      throw r;
    } finally {
      if (transfer.getStatus() != Transfer.Status.COMPLETED) {
        transfer.setStatus(Transfer.Status.FAILED);
      }
      this.transfersRepository.updateTransfer(transfer);
    }
    
    if (transfer.getStatus() == Transfer.Status.COMPLETED) {
      notificationService.notifyAboutTransfer(senderAccount, "You have sent a transfer " +
        "to Account: " + transfer.getReceiverAccountId() + " for an amount of " + transfer.getAmount());

      notificationService.notifyAboutTransfer(receiverAccount, "You have received a transfer " +
        "from Account: " + transfer.getSenderAccountId() + " for an amount of " + transfer.getAmount());
    }
    
    return this.transfersRepository.updateTransfer(transfer);
  }

  public Transfer createTransfer(Transfer transfer) {
    return this.transfersRepository.createTransfer(transfer);
  }

  public Transfer getTransfer(Long transferId) {
    return this.transfersRepository.getTransfer(transferId);
  }

  public List<Transfer> getTransfer() {
    return this.transfersRepository.getTransfer();
  }

  public void clearTransfers() {
    this.transfersRepository.clearTransfers();
  }

}
