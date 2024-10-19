package org.sid.comptecqrses.command.aggregates;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.sid.comptecqrses.commonApi.commands.CreateAccountCommand;
import org.sid.comptecqrses.commonApi.commands.CreditAccountCommand;
import org.sid.comptecqrses.commonApi.commands.DebitAccountCommand;
import org.sid.comptecqrses.commonApi.enums.AccountStatus;
import org.sid.comptecqrses.commonApi.events.AccountActivatedEvent;
import org.sid.comptecqrses.commonApi.events.AccountCreatedEvent;
import org.sid.comptecqrses.commonApi.events.AccountCreditedEvent;
import org.sid.comptecqrses.commonApi.events.AccountDebitedEvent;
import org.sid.comptecqrses.commonApi.exceptions.AmountNegativeException;
import org.sid.comptecqrses.commonApi.exceptions.InsufficientBalanceException;


@Aggregate
public class AccountAggregate {
    @AggregateIdentifier
    private String id;
    private double balance;
    private String currency;
    private AccountStatus status;

    public AccountAggregate() {
        // Required by Axon Framework
    }

    @CommandHandler
    public AccountAggregate(CreateAccountCommand createAccountCommand) {
        if ( createAccountCommand.getInitialBalance() < 0) throw new RuntimeException(("Initial balance cannot be less than 0"));
        AggregateLifecycle.apply(new AccountCreatedEvent(
                createAccountCommand.getId(),
                createAccountCommand.getInitialBalance(),
                createAccountCommand.getCurrency(),
                AccountStatus.CREATED));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.id = event.getId();
        this.balance = event.getInitialBalance();
        this.currency = event.getCurrency();
        this.status = AccountStatus.CREATED;
        AggregateLifecycle.apply(new AccountActivatedEvent(
                event.getId(),
                AccountStatus.ACTIVATED
        ));
    }
    @EventSourcingHandler
    public void on(AccountActivatedEvent event){
        this.status = event.getStatus();
    }

    @CommandHandler
    public void handle(CreditAccountCommand command) {
        if ( command.getAmount() < 0) throw new AmountNegativeException("Amount should not be negative");
        AggregateLifecycle.apply(new AccountCreditedEvent(
                command.getId(),
                command.getAmount(),
                command.getCurrency()
        ));
    }

    @EventSourcingHandler
    public void on(AccountCreditedEvent event){
        this.balance += event.getAmount();
    }

    @CommandHandler
    public void handle(DebitAccountCommand command) {
        if ( command.getAmount() < 0) throw new AmountNegativeException("Amount should not be negative");
        if(this.balance<command.getAmount()) throw new InsufficientBalanceException("Insufficient balance =>"+balance);
        AggregateLifecycle.apply(new AccountDebitedEvent(
                command.getId(),
                command.getAmount(),
                command.getCurrency()
        ));
    }

    @EventSourcingHandler
    public void on(AccountDebitedEvent event){
        this.balance -= event.getAmount();
    }


}
