package de.sotterbeck.iumetro.dataprovider.postgres.ticket;

import de.sotterbeck.iumetro.usecase.ticket.TicketDsGateway;
import de.sotterbeck.iumetro.usecase.ticket.TicketDsModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class JpaTicketDao implements TicketDsGateway {

    private final EntityManager entityManager;

    public JpaTicketDao(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public void save(TicketDsModel ticket) {
        insideTransaction(em -> {
            TicketMapper ticketMapper = toMapper(ticket);
            em.persist(ticketMapper);
        });
    }

    @Override
    public Optional<TicketDsModel> get(UUID id) {
        TicketMapper ticketMapper;
        ticketMapper = entityManager.find(TicketMapper.class, id);
        if (ticketMapper == null) {
            return Optional.empty();
        }
        return Optional.of(toDsModel(ticketMapper));
    }

    @Override
    public List<TicketDsModel> getAll() {
        List<TicketMapper> tickets = entityManager.createQuery("select t from Ticket t", TicketMapper.class)
                .getResultList();
        return tickets.stream()
                .map(JpaTicketDao::toDsModel)
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return entityManager.createQuery("select id from Ticket where id = :id")
                .setParameter("id", id)
                .getResultList().size() == 1;
    }

    @Override
    public void deleteById(UUID id) {
        TicketMapper ticket;
        ticket = entityManager.find(TicketMapper.class, id);
        insideTransaction(em -> em.remove(ticket));
    }

    private void insideTransaction(Consumer<EntityManager> action) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            action.accept(entityManager);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }
    }

    private TicketMapper toMapper(TicketDsModel ticket) {
        return new TicketMapper(ticket.id(),
                ticket.name(),
                fetchMaxUsages(ticket),
                fetchTimeLimit(ticket));
    }

    private TicketUsageLimitMapper fetchMaxUsages(TicketDsModel ticket) {
        return entityManager.createQuery("from TicketUsageLimit where maxUsages = :maxUsages", TicketUsageLimitMapper.class)
                .setParameter("maxUsages", ticket.usageLimit()).getResultStream()
                .findAny()
                .orElse(new TicketUsageLimitMapper(ticket.usageLimit()));
    }

    private TicketTimeLimitMapper fetchTimeLimit(TicketDsModel ticket) {
        return entityManager.createQuery("from TicketTimeLimit where timeLimit = :timeLimit", TicketTimeLimitMapper.class)
                .setParameter("timeLimit", ticket.timeLimit()).getResultStream()
                .findAny()
                .orElse(new TicketTimeLimitMapper(ticket.timeLimit()));
    }

    private static TicketDsModel toDsModel(TicketMapper ticketMapper) {
        return new TicketDsModel(
                ticketMapper.getId(),
                ticketMapper.getName(),
                ticketMapper.getUsageLimit().getMaxUsages(),
                ticketMapper.getTimeLimit().getTimeLimit());
    }

}
