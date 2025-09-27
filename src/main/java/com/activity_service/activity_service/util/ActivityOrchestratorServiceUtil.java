package com.activity_service.activity_service.util;

import com.activity.avro.ActivityIngestionResponse;
import com.activity.avro.ActivityResponse;
import com.activity.avro.Errors;
import com.activity.avro.MetaData;
import com.activity_service.activity_service.kafka.KafkaProducer;
import com.activity_service.activity_service.logger.LoggingAggregator;
import com.activity_service.activity_service.model.ActivitiesRequestMessage;
import com.activity_service.activity_service.model.ActivitiesStatus;
import com.activity_service.activity_service.model.request.ativities.ActivitiesRequest;
import com.activity_service.activity_service.model.request.ativities.EventMetadata;
import com.activity_service.activity_service.repository.ActivitiesRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import netscape.javascript.JSObject;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Component
public class ActivityOrchestratorServiceUtil {

    @Autowired
    private LoggingAggregator loggingAggregator;

    @Autowired
    private ActivitiesRepo activitiesRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaProducer kafkaProducer;
    public ActivityIngestionResponse prepareOrchFailResponse(String id,String activitiesRequestId,String errorName,String errorMessage, String in) {

        String methodName = "prepareOrchFailResponse";
        loggingAggregator.logDebugMessage(activitiesRequestId,ActivityIngestionResponse.class.getName(),
                methodName,"ACTIVITIES_ORCHESTRATOR",errorMessage);
        MetaData metaData = new MetaData();
        metaData.setEventId(id);
        metaData.setActivityRequestId(activitiesRequestId);

        com.activity.avro.Error error = new com.activity.avro.Error();
        error.setId(id);
        error.setName(errorName);
        error.setMessage(errorMessage);

        Errors errors = new Errors();
        errors.setIn(in);
        errors.setMessage(errorMessage);
        List<Errors> errorsList = new ArrayList<>();
        errorsList.add(errors);
        error.setErrors(errorsList);

        ActivityResponse activityResponse = new ActivityResponse();
        activityResponse.setMetaData(metaData);
        activityResponse.setError(error);
       // activityResponse.setScreeningStatus(errorName);
        activityResponse.setApplicationName(in);

        ActivityIngestionResponse activityIngestionResponse = new ActivityIngestionResponse();
        activityIngestionResponse.setActivityResponse(activityResponse);
        return activityIngestionResponse;
    }

    public List<ActivitiesRequestMessage> getRequestMessageByEndToEndRefId(String id) {
        return activitiesRepo.findAllByEndToEndReferenceId(id);
    }

    public String saveRequest(String activtiesRequestId, ActivitiesRequest activityRequest,
                              EventMetadata eventMetadata) throws JsonProcessingException {
        ActivitiesRequestMessage activitiesRequestMessage = mapRequestData(activtiesRequestId,activityRequest,eventMetadata);
        ActivitiesRequestMessage updateRequestMessage = activitiesRepo.save(activitiesRequestMessage);
        return updateRequestMessage.getRequestId();
    }

    private ActivitiesRequestMessage mapRequestData(String activtiesRequestId, ActivitiesRequest activityRequest,
                                                    EventMetadata eventMetadata) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(activityRequest);
        ActivitiesRequestMessage activitiesRequestMessage = new ActivitiesRequestMessage();
        activitiesRequestMessage.setRequestId(activtiesRequestId);
        activitiesRequestMessage.setEndToEndReferenceId(activityRequest.getId());
        activitiesRequestMessage.setPayload(payload);
        activitiesRequestMessage.setEventType(eventMetadata.getEventType());
        activitiesRequestMessage.setEventId(eventMetadata.getEventId());
        return activitiesRequestMessage;
    }

    public ActivityIngestionResponse mapResponsePayload(String requestId, ActivitiesRequest activityRequest,
                                                        JSONArray errorJsonArray, String failureName) {
        MetaData metaData = new MetaData();
        metaData.setEventId(activityRequest.getId());
        metaData.setActivityRequestId(requestId);

        ActivityResponse activityResponse = new ActivityResponse();
        activityResponse.setApplicationName(activityRequest.getData().getEventMetadata().getEventType());
        activityResponse.setMetaData(metaData);

        if(StringUtils.isNotEmpty(failureName)){
            com.activity.avro.Error error = new com.activity.avro.Error();
            error.setId(activityRequest.getId());
            error.setName(failureName);
            error.setMessage(failureName);

            List<Errors> errorsList = new ArrayList<>();
            if(errorJsonArray.length() != 0){
                for(int i=0;i<errorJsonArray.length();i++){
                    JSONObject optJSONObject = errorJsonArray.optJSONObject(i);
                    Errors errors = new Errors();
                    errors.setMessage(optJSONObject.optString("message"));
                    errors.setIn(optJSONObject.optString("in"));
                    errorsList.add(errors);
                }
                error.setErrors(errorsList);
            }
            activityResponse.setError(error);

        }

        ActivityIngestionResponse activityIngestionResponse = new ActivityIngestionResponse();
        activityIngestionResponse.setActivityResponse(activityResponse);
        return activityIngestionResponse;
    }

    public ActivitiesStatus setScreenStatus(String ack, String activitiesRequestId, String body, String success) {
        ActivitiesStatus activitiesStatus = new ActivitiesStatus();
        activitiesStatus.setStatusId(UUID.randomUUID().toString());
        activitiesStatus.setRequestId(activitiesRequestId);
        activitiesStatus.setResponseStatus(success);
        activitiesStatus.setPayload(body);
        return activitiesStatus;
    }

    public ActivityIngestionResponse prepareActivityRequest(String activitiesRequestId, String apigeeCorrelationId, ActivitiesRequest activityRequest) {
    ActivityIngestionResponse activityIngestionResponse = new ActivityIngestionResponse();
    ActivityResponse activityResponse = new ActivityResponse();
    activityResponse.setApplicationName(apigeeCorrelationId);
    MetaData metaData = new MetaData();
    metaData.setActivityRequestId(activityRequest.getData().getEventMetadata().getEventId());
    metaData.setEventId(activityRequest.getData().getEventMetadata().getEventId());
    activityResponse.setMetaData(metaData);
    activityIngestionResponse.setActivityResponse(activityResponse);
    return activityIngestionResponse;

    }

    public void postMessageToKafkaTopic(String postMessageToNewRequestKafkaTopic, ActivityIngestionResponse activityIngestionResponse) {
   kafkaProducer.sendMessageToKafka(activityIngestionResponse.getActivityResponse().getMetaData().getActivityRequestId(),postMessageToNewRequestKafkaTopic, activityIngestionResponse);

    }
}
