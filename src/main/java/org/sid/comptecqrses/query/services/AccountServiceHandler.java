package org.sid.comptecqrses.query.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.sid.comptecqrses.commonApi.enums.AccountStatus;
import org.sid.comptecqrses.commonApi.events.AccountActivatedEvent;
import org.sid.comptecqrses.commonApi.events.AccountCreatedEvent;
import org.sid.comptecqrses.commonApi.events.AccountCreditedEvent;
import org.sid.comptecqrses.commonApi.events.AccountDebitedEvent;
import org.sid.comptecqrses.commonApi.queries.GetAccountByIdQuery;
import org.sid.comptecqrses.commonApi.queries.GetAllAccountQuery;
import org.sid.comptecqrses.query.entities.Account;
import org.sid.comptecqrses.query.entities.Operation;
import org.sid.comptecqrses.query.enums.OperationType;
import org.sid.comptecqrses.query.repositories.AccountRepository;
import org.sid.comptecqrses.query.repositories.OperationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class AccountServiceHandler {
    private AccountRepository accountRepository;
    private OperationRepository operationRepository;
    @EventHandler
    public void on(AccountCreatedEvent event){
        Account account = new Account();
        log.info("***********************");
        log.info("AccountCreatedEvent received");
        account.setId(event.getId());
        account.setBalance(event.getInitialBalance());
        account.setCurrency(event.getCurrency());
        account.setStatus(event.getStatus());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountActivatedEvent event){

        log.info("***********************");
        log.info("AccountActivatedEvent received");

        Account account = accountRepository.findById(event.getId()).get();
        account.setStatus(event.getStatus());
        accountRepository.save(account);
    }
    @EventHandler
    public void on(AccountDebitedEvent event){

        log.info("***********************");
        log.info("AccountDebitedEvent received");

        Account account = accountRepository.findById(event.getId()).get();
        Operation operation = new Operation();
        operation.setAmount(event.getAmount());
        operation.setDate(new Date());
        operation.setType(OperationType.DEBIT);
        operation.setAccount(account);
        operationRepository.save(operation);
        account.setBalance(account.getBalance()+event.getAmount());
        accountRepository.save(account);

    }

    @EventHandler
    public void on(AccountCreditedEvent event){

        log.info("***********************");
        log.info("AccountCreditedEvent received");

        Account account = accountRepository.findById(event.getId()).get();
        Operation operation = new Operation();
        operation.setAmount(event.getAmount());
        operation.setDate(new Date());
        operation.setType(OperationType.CREDIT);
        operation.setAccount(account);
        operationRepository.save(operation);
        account.setBalance(account.getBalance()-event.getAmount());
        accountRepository.save(account);

    }

    @QueryHandler
    public List<Account> on(GetAllAccountQuery query){
        return accountRepository.findAll();
    }

    @QueryHandler
    public Account on(GetAccountByIdQuery query){
        return accountRepository.findById(query.getId()).get();
    }


}
