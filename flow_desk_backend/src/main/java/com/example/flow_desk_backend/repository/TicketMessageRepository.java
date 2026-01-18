package com.example.flow_desk_backend.repository;

import com.example.flow_desk_backend.entity.AuthorType;
import com.example.flow_desk_backend.entity.TicketMessageEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketMessageRepository extends JpaRepository<TicketMessageEntity, Long> {
    List<TicketMessageEntity> findByTicketTicketId(Long ticketId);

    @Query("""
            update TicketMessageEntity tm
            set tm.read = true
            where tm.ticket.ticketId = :ticketId
            and tm.authorType = :authorType
            and tm.read = false
            """)
    List<TicketMessageEntity> markAsRead(@Param("ticketId") Long ticketId, @Param("authorType") AuthorType authorType);
}
