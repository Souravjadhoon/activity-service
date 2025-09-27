package com.activity_service.activity_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ActivitiesRequestMessage {
    @Id
    @Column(name = "REQUEST_ID")
    private String requestId;

    @Column(name = "END_TO_END_REFERENCE_ID")
    private String endToEndReferenceId;

    @Column(name = "PAYLOAD")
    private String payload;

    @Column(name = "EVENT_TYPE")
    private String eventType;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Column(name = "EVENT_ID")
    private String eventId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getEndToEndReferenceId() {
        return endToEndReferenceId;
    }

    public void setEndToEndReferenceId(String endToEndReferenceId) {
        this.endToEndReferenceId = endToEndReferenceId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
