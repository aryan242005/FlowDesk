package com.example.flow_desk_backend.entity;

public enum TicketStatus {
    OPEN,
    IN_PROGRESS,
    REJECTED,
    CLOSED;

    public boolean canMoveTo(TicketStatus next){
        return switch(this){
            case OPEN -> next == IN_PROGRESS;
            case IN_PROGRESS -> next == REJECTED;
            case REJECTED -> next == CLOSED;
            case CLOSED -> false;
        };
    }
}
