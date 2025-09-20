package com.aurora.ledger.infrastructure.projection.repository;

import com.aurora.ledger.infrastructure.projection.document.AccountBalanceProjectionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountBalanceProjectionRepository extends MongoRepository<AccountBalanceProjectionDocument, String> {
}
