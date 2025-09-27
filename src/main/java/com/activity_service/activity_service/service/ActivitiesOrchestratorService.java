package com.activity_service.activity_service.service;

import com.activity.avro.ActivityIngestionResponse;
import com.activity_service.activity_service.logger.LoggingAggregator;
import com.activity_service.activity_service.model.ActivitiesStatus;
import com.activity_service.activity_service.model.request.ativities.ActivitiesRequest;
import com.activity_service.activity_service.model.request.ativities.EventMetadata;
import com.activity_service.activity_service.repository.ActivitiesStatusRepo;
import com.activity_service.activity_service.util.ActivityOrchestratorServiceUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
@Service
public class ActivitiesOrchestratorService {
    @Autowired
    private LoggingAggregator loggingAggregator;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ActivitiesStatusRepo activitiesStatusRepo;
    @Autowired
    private ActivityOrchestratorServiceUtil activityOrchestratorServiceUtil;
    @Value("${spring.kafka.topic.orderPlaced}")
    private String orderPlacedTopic;

    public void processActivitiesRequest(String activitiesRequestId, String apigeeCorrelationId,
                                         ActivitiesRequest activityRequest) {

        String methodName = "processActivitiesService";


        loggingAggregator.logDebugMessage(activitiesRequestId,ActivitiesOrchestratorService.class.getName(),methodName,"ActivitiesOrchestrator","ACTIVITIES_REQUEST_RECEIVED"+activitiesRequestId);
        JSONObject validationResponseObject = validateActivityRequest(activitiesRequestId,activityRequest);

        String requestReferenceId = activityRequest.getId();
        EventMetadata eventMetadata = activityRequest.getData().getEventMetadata();
        String validationResponse;
        ResponseEntity<String> responseEntity;

        if(validationResponseObject!=null){
            String statusCode = validationResponseObject.optString("statusCode");
            String validationStatus = validationResponseObject.optString("validationStatus");
            JSONArray errorJsonArray = validationResponseObject.optJSONArray("errorJsonArray");
            validationResponse = activityOrchestratorServiceUtil.mapResponsePayload(activitiesRequestId,activityRequest,errorJsonArray,null).toString();
            if(statusCode.equalsIgnoreCase("VALIDATION_SUCCESS" )|| statusCode.equalsIgnoreCase("PARTIAL_SUCCESS_CODE")){
                //if (its success or partial success
                String responseStatus = "SUCCESS";
                if(statusCode.equalsIgnoreCase("PARTIAL_SUCCESS_CODE")){
                    responseStatus = "PARTIAL SUCCESS";
                   validationResponse = activityOrchestratorServiceUtil.mapResponsePayload(activitiesRequestId,activityRequest,errorJsonArray,"VALIDATION_PARTIAL_SUCCESS").toString();

                }
                ActivitiesStatus screenStatus = activityOrchestratorServiceUtil.setScreenStatus("VALIDATION",activitiesRequestId,validationResponse,responseStatus);
                activitiesStatusRepo.save(screenStatus);

                ActivityIngestionResponse activityIngestionResponse = null;

                try{
                    activityIngestionResponse = activityOrchestratorServiceUtil.prepareActivityRequest(activitiesRequestId,apigeeCorrelationId,activityRequest);
                    postMessage(activityIngestionResponse);

                }catch (Exception exception){

                }


            }
            else{

                //not partial status and success means failed
               // save status sin db
                // and dont do anything
            }

        }

    }

    private void postMessage(ActivityIngestionResponse activityIngestionResponse) {
        activityOrchestratorServiceUtil.postMessageToKafkaTopic(orderPlacedTopic,activityIngestionResponse);
    }

    private JSONObject validateActivityRequest(String activitiesRequestId, ActivitiesRequest activityRequest) {
        String methodName= "validateActivityRequest";
        loggingAggregator.logDebugMessage(activitiesRequestId,ActivitiesOrchestratorService.class.getName(),methodName,"ACTIVITIES_ORCHESTRATOR",
                "vaidation service request");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("requestId",activitiesRequestId);
        JSONObject validationResponseObject = null;

        try{
            HttpEntity<ActivitiesRequest> validationReqEntity = new HttpEntity<>(activityRequest,headers);
            //"http://validation-service/v1/risk/validation"
            ResponseEntity<String> responseEntity = restTemplate.exchange("validation.service.url=https://validation-service-production-1151.up.railway.app/v1/risk/validation", HttpMethod.POST,validationReqEntity,String.class);
            String response = responseEntity.getBody();
            validationResponseObject = new JSONObject(response);
            String validationStatus = validationResponseObject.optString("validationStatus");

            loggingAggregator.logDebugMessage(activitiesRequestId,ActivitiesOrchestratorService.class.getName(),methodName,"ACTIVITIES_ORCHESTRATOR",
                    "vaidation service status:" +validationStatus);

        }catch(HttpStatusCodeException ex) {
            loggingAggregator.logErrorMessage(activitiesRequestId,ActivitiesOrchestratorService.class.getName(),methodName,"ACTIVITIES_ORCHESTRATOR",
                    ex.toString());
        }catch(Exception e){
            loggingAggregator.logErrorMessage(activitiesRequestId,ActivitiesOrchestratorService.class.getName(),methodName,"ACTIVITIES_ORCHESTRATOR",
                    e.toString());
        }

        return validationResponseObject;
    }
}
