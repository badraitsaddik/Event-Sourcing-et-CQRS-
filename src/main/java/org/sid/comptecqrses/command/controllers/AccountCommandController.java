package org.sid.comptecqrses.command.controllers;

import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.sid.comptecqrses.commonApi.commands.CreateAccountCommand;
import org.sid.comptecqrses.commonApi.commands.CreditAccountCommand;
import org.sid.comptecqrses.commonApi.commands.DebitAccountCommand;
import org.sid.comptecqrses.commonApi.dtos.CreateAccountRequestDto;
import org.sid.comptecqrses.commonApi.dtos.CreditAccountRequestDto;
import org.sid.comptecqrses.commonApi.dtos.DebitAccountRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "commands/account")
@AllArgsConstructor

public class AccountCommandController {
    private CommandGateway commandGateway;
    private EventStore eventStore;
    @PostMapping(path = "/create")
    public CompletableFuture<String> createAccountDto(@RequestBody CreateAccountRequestDto request) {
        CompletableFuture<String> commandResponse = commandGateway.send(new CreateAccountCommand(
                UUID.randomUUID().toString(),
                request.getInitialBalance(),
                request.getCurrency()));
        return commandResponse;
    }

    @PutMapping(path = "/credit")
    public CompletableFuture<String> creditAccountDto(@RequestBody CreditAccountRequestDto request) {
        CompletableFuture<String> commandResponse = commandGateway.send(new CreditAccountCommand(
                request.getId(),
                request.getAmount(),
                request.getCurrency()
        ));

        return commandResponse;
    }

    @PutMapping(path = "/debit")
    public CompletableFuture<String> debitAccountDto(@RequestBody DebitAccountRequestDto request) {
        CompletableFuture<String> commandResponse = commandGateway.send(new DebitAccountCommand(
                request.getId(),
                request.getAmount(),
                request.getCurrency()
        ));

        return commandResponse;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception e) {
        ResponseEntity<String> entity = new ResponseEntity<>(
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
        return entity;
    }

    @GetMapping("/eventStore/{id}")
    public Stream eventStore(@PathVariable String id) {
        return eventStore.readEvents(id).asStream();
    }
}
