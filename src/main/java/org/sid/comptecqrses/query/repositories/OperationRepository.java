package org.sid.comptecqrses.query.repositories;

import org.sid.comptecqrses.query.entities.Account;
import org.sid.comptecqrses.query.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation,Long> {
}
