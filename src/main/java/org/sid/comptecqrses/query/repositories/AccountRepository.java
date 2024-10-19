package org.sid.comptecqrses.query.repositories;

import org.sid.comptecqrses.query.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account ,String> {
}
