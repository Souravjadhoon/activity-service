package com.activity_service.activity_service.Constants;

import org.apache.hc.core5.http2.frame.StreamIdGenerator;

public interface ActivitiesRequestOrchestratorConstants {

    public static final String ACTIVITIES_REQUEST_ID_SUFFIX = "reqsvc";
    String SERVICE_FAILURE = "Activity Service Failure";

    String SCREENING_SYSTEM_ERROR= "SCREENING_SYSTEM_ERROR";

    String REQUEST_INVALID = "REQUEST_INVALID";
    String REQUEST_INVALID_DESC = "Mandatory information is missing";
}
