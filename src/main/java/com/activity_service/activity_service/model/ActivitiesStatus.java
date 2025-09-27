package com.activity_service.activity_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ActivitiesStatus {

        @Id
        @Column(name = "Status_ID")
        private String statusId;

        @Column(name = "REQUEST_ID")
        private String requestId;

        @Column(name = "PAYLOAD")
        private String payload;

        @Column(name = "RESPONSE_STATUS")
        private String responseStatus;

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }
}
