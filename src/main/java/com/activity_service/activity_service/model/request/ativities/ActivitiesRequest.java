package com.activity_service.activity_service.model.request.ativities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@JsonPropertyOrder({"id","time","data"})
@Valid
public class ActivitiesRequest {
    @JsonProperty("id")
    @Schema(
            name = "id",
            type = "string",
            example = "0098wehjgewuiu-skjdfs",
            required = true
    )
    private @NotEmpty(message = "id:id is required")
    String id;


    @JsonProperty("time")
    @Schema(
            name = "time",
            type = "string",
            example = "2025-03-17T15:27:24.456811Z"

    )
    private String time;

    @Valid
    @JsonProperty("data")
    @Schema(
            name = "data"
    )
    private @NotNull(message = "mandatory:data is required") Data data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ActivityRequest{" +
                "id='" + id + '\'' +
                ", time='" + time + '\'' +
                ", data=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivitiesRequest that = (ActivitiesRequest) o;
        return Objects.equals(id, that.id) && Objects.equals(time, that.time) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, data);
    }

    public ActivitiesRequest() {
    }
}
