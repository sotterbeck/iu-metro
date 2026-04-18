package de.sotterbeck.iumetro.app.retail;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface RetailTicketRepository {

    Collection<RetailTicketDto> getAll();

    Collection<String> getAllCategories();

    Optional<RetailTicketDto> getById(UUID id);

    Map<String, Collection<RetailTicketDto>> getAllGroupedByCategory();

    void save(RetailTicketDto ticket);

    boolean existsById(UUID id);

    boolean existsByName(String name);

    void delete(UUID id);

}
