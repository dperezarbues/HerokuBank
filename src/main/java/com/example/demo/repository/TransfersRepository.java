package com.example.demo.repository;

import com.example.demo.domain.Transfer;
import com.example.demo.exception.DuplicateTransferIdException;

import java.util.List;

public interface TransfersRepository {

  void createTransfer(Transfer transfer) throws DuplicateTransferIdException;

  Transfer getTransfer(Long transferId);

  List<Transfer> getTransfer();

  void clearTransfers();
}
