package com.activity_service.activity_service.model.request.ativities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.Objects;

@Valid
public class EventMetadata {
    @JsonProperty("eventId")
    @Schema(
            name = "eventId"
    )
    private @NotEmpty(message = "mandatory:eventId is mandatory")
    String eventId;


    @JsonProperty("eventType")
    @Schema(
            name = "eventType"
    )
    private @NotEmpty(message = "mandatory:eventType is mandatory")
    String eventType;
    public EventMetadata() {
    }

    @Override
    public String toString() {
        return "EventMetadata{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventMetadata that = (EventMetadata) o;
        return Objects.equals(eventId, that.eventId) && Objects.equals(eventType, that.eventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventType);
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
