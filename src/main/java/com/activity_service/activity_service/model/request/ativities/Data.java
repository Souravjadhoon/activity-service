package com.activity_service.activity_service.model.request.ativities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Valid
@JsonPropertyOrder({"userId", "userActivity", "eventMetadata"})
public class Data {
    @JsonProperty("eventMetadata")
    @Valid
    @Schema(
            name = "eventMetadata"
    )
    private @NotNull(message = "mandatory:event_metadata is mandatory")
    EventMetadata eventMetadata;

    @JsonProperty("userActivity")
    @Schema(
            name = "userActivity"
    )
    private @NotEmpty(message = "mandatory:user_activity is mandatory")
    String userActivity;

    @JsonProperty("userId")
    @Schema(
            name = "userId"
    )
    private @NotEmpty(message = "mandatory:user_id is mandatory")
    String userId;

    @Override
    public String toString() {
        return "Data{" +
                "eventMetadata=" + eventMetadata +
                ", userActivity='" + userActivity + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return Objects.equals(eventMetadata, data.eventMetadata) && Objects.equals(userActivity, data.userActivity) && Objects.equals(userId, data.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventMetadata, userActivity, userId);
    }

    public EventMetadata getEventMetadata() {
        return eventMetadata;
    }

    public void setEventMetadata(EventMetadata eventMetadata) {
        this.eventMetadata = eventMetadata;
    }

    public String getUserActivity() {
        return userActivity;
    }

    public void setUserActivity(String userActivity) {
        this.userActivity = userActivity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Data() {
    }
}
