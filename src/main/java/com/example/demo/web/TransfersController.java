package com.example.demo.web;

import com.example.demo.domain.Transfer;
import com.example.demo.exception.AccountNotFoundException;
import com.example.demo.exception.InsufficientFundsException;
import com.example.demo.service.TransfersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import javax.validation.Valid;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/v1/transfers")
@Slf4j
public class TransfersController {

  private final TransfersService transfersService;

  @Autowired
  public TransfersController(TransfersService transfersService) {
    this.transfersService = transfersService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody
  ResponseEntity<Resource<Transfer>> executeTransfer(@RequestBody @Valid Transfer transfer) {
    log.info("Creating transfer {}", transfer);

    try {
      transfer = this.transfersService.createTransfer(transfer);
      transfer = this.transfersService.executeTransfer(transfer);
    } catch (InsufficientFundsException | AccountNotFoundException te) {
      return new ResponseEntity<>(buildTransferResource(transfer), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(buildTransferResource(transfer), HttpStatus.CREATED);

  }

  @GetMapping(path = "/{transferId}")
  public @ResponseBody
  Resource<Transfer> getTransfer(@PathVariable Long transferId) {
    log.info("Retrieving transfer for id {}", transferId);

    return buildTransferResource(this.transfersService.getTransfer(transferId));
  }

  @GetMapping
  public @ResponseBody 
  List<Resource<Transfer>> getTransfers() {
    List<Resource<Transfer>> resources = new ArrayList<>();
    for (Transfer transfer : this.transfersService.getTransfer()) {
      resources.add(buildTransferResource(transfer));
    }
    return resources;
  }
  
    private Resource<Transfer> buildTransferResource(Transfer transfer) {
        Resource<Transfer> resource = new Resource<>(transfer);
        resource.add(linkTo(methodOn(TransfersController.class).getTransfer(transfer.getTransferId())).withSelfRel());
        resource.add(linkTo(methodOn(AccountsController.class).getAccount(transfer.getReceiverAccountId())).withRel("receiver"));
        resource.add(linkTo(methodOn(AccountsController.class).getAccount(transfer.getSenderAccountId())).withRel("sender"));

        return resource;
    }

}
