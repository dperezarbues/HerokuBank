package com.example.demo.repository;

import com.example.demo.domain.Transfer;
import com.example.demo.exception.DuplicateTransferIdException;
import org.springframework.data.repository.CrudRepository;
import com.example.demo.exception.AccountNotFoundException;

import java.util.List;
import java.util.stream.StreamSupport;
import java.util.stream.Collectors;

public interface TransfersRepository extends CrudRepository <Transfer,Long> {

  default Transfer createTransfer(Transfer transfer) throws DuplicateTransferIdException {
      return save(transfer);
  }
  
  default Transfer updateTransfer(Transfer transfer) throws DuplicateTransferIdException {
      return save(transfer);
  }

  default Transfer getTransfer(Long transferId) {
      Transfer transfer = findOne(transferId);
      if (transfer == null) {
          throw new AccountNotFoundException("Not transfer found with ID: " + transferId);
      } else {
          return transfer;
      }
  }

  default List<Transfer> getTransfer() {

      return StreamSupport.stream(findAll().spliterator(),false).collect(Collectors.toList());

  }

  default void clearTransfers() {
      deleteAll();
  };
}
