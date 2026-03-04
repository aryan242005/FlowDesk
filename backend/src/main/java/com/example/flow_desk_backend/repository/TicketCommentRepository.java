package com.example.flow_desk_backend.repository;

import com.example.flow_desk_backend.entity.TicketCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketCommentRepository extends JpaRepository<TicketCommentEntity, Long> {
    List<TicketCommentEntity> findByTicketTicketId(Long ticketId);

//    @Query("""
//            update TicketMessageEntity tm
//            set tm.read = true
//            where tm.ticket.ticketId = :ticketId
//            and tm.authorType = :authorType
//            and tm.read = false
//            """)
//    List<TicketCommentEntity> markAsRead(@Param("ticketId") Long ticketId, @Param("authorType") AuthorType authorType);
}
