package com.aurora.ledger.infrastructure.events;

import com.aurora.ledger.domain.events.DomainEvent;
import com.aurora.ledger.domain.events.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SpringDomainEventPublisher
 * Bridges DomainEventPublisher interface to Spring's ApplicationEventPublisher.
 */
@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(SpringDomainEventPublisher.class);
    private final ApplicationEventPublisher publisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publish(DomainEvent event) {
        if (event == null) return;
        logger.debug("Publishing domain event: {}  correlation={}", event.getEventType(), event.getCorrelationId());
        publisher.publishEvent(event);
    }

    @Override
    public void publishAll(List<DomainEvent> events) {
        if (events == null || events.isEmpty()) return;
        events.forEach(this::publish);
    }
}










