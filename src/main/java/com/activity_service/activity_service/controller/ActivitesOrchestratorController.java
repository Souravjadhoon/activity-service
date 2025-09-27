package com.activity_service.activity_service.controller;

import com.activity.avro.ActivityIngestionResponse;
import com.activity_service.activity_service.Constants.ActivitiesRequestOrchestratorConstants;
import com.activity_service.activity_service.logger.LoggingAggregator;
import com.activity_service.activity_service.model.ActivitiesStatus;
import com.activity_service.activity_service.model.request.ativities.ActivitiesRequest;
import com.activity_service.activity_service.model.ActivitiesRequestMessage;
import com.activity_service.activity_service.model.request.ativities.EventMetadata;
import com.activity_service.activity_service.repository.ActivitiesStatusRepo;
import com.activity_service.activity_service.service.ActivitiesOrchestratorService;
import com.activity_service.activity_service.util.ActivityOrchestratorServiceUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/v1/risk")
@RestController
public class ActivitesOrchestratorController {

    @Autowired
    private LoggingAggregator loggingAggregator;
    @Autowired
    private ActivitiesOrchestratorService activitiesOrchestratorService;
    @Autowired
    private ActivitiesStatusRepo activitiesStatusRepo;
    @Autowired
    private ActivityOrchestratorServiceUtil activityOrchestratorServiceUtil;

    @PostMapping(value = "/activities" ,produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> activityIngestion(
            @RequestHeader(value = "x-svb-correlation-id", required = false) String apigeeCorrelationId,
         @Valid @RequestBody ActivitiesRequest activityRequest){

        String methodName = "activityIngestion";

        String activitiesRequestId = UUID.randomUUID().toString()
                +  ActivitiesRequestOrchestratorConstants.ACTIVITIES_REQUEST_ID_SUFFIX;

        loggingAggregator.logInfoMessage(activitiesRequestId,ActivitesOrchestratorController.class.getName(),
                methodName,"Activity-Orchestrator","APIGEE Correlation ID -"+ apigeeCorrelationId);

        loggingAggregator.logDebugMessage(activitiesRequestId,ActivitesOrchestratorController.class.getName(),
                methodName,"Activity-Orchestrator","APIGEE Correlation ID -"+ apigeeCorrelationId);


            ResponseEntity<String> responseEntity=null;
            String id = null;

            try{
                if(activityRequest!=null && activityRequest.getData() !=null){
                    id = activityRequest.getId();
                    EventMetadata eventMetadata = activityRequest.getData().getEventMetadata();
                    List<ActivitiesRequestMessage> requestMessageList = null;
                    if(StringUtils.isNotEmpty(id)){
                        requestMessageList = activityOrchestratorServiceUtil.getRequestMessageByEndToEndRefId(id);
                    }

                    if(requestMessageList!=null && !requestMessageList.isEmpty()){
                        //duplicate request logic
                    }else {
                        responseEntity = processActivitiesRequest(activitiesRequestId,activityRequest,apigeeCorrelationId,
                                eventMetadata,id);
                    }
                }
                else{
                    ActivityIngestionResponse activityIngestionResponse =
                            activityOrchestratorServiceUtil.prepareOrchFailResponse(id,activitiesRequestId,
                                    ActivitiesRequestOrchestratorConstants.REQUEST_INVALID,
                                    ActivitiesRequestOrchestratorConstants.REQUEST_INVALID_DESC
                                    ,"activity_request");
                    responseEntity = new ResponseEntity<>(activityIngestionResponse.toString(),HttpStatus.BAD_REQUEST);
                }

            }catch (Exception e){

                    loggingAggregator.logErrorMessage(activitiesRequestId,ActivitesOrchestratorController.
                                    class.getName(),methodName,
                            ActivitiesRequestOrchestratorConstants.SERVICE_FAILURE,e.toString());

                    ActivityIngestionResponse activitiesFailResponse =
                            activityOrchestratorServiceUtil.prepareOrchFailResponse(id,activitiesRequestId,
                                    ActivitiesRequestOrchestratorConstants.SCREENING_SYSTEM_ERROR,
                                    id+ActivitiesRequestOrchestratorConstants.SERVICE_FAILURE,"activity_Request"
                                    );
                responseEntity = new ResponseEntity<>(activitiesFailResponse.toString(),HttpStatus.INTERNAL_SERVER_ERROR);


            }
            return responseEntity;
    }

    private ResponseEntity<String> processActivitiesRequest(String requestId, ActivitiesRequest activityRequest, String apigeeCorrelationId, EventMetadata eventMetadata, String id) throws JsonProcessingException {
        String activitiesRequestId;
        ResponseEntity<String> responseEntity = null;

        activitiesRequestId = activityOrchestratorServiceUtil.saveRequest(requestId,activityRequest,eventMetadata);
        ActivityIngestionResponse activityIngestionResponse =activityOrchestratorServiceUtil.
                mapResponsePayload(requestId,activityRequest,null,null);

        responseEntity = new ResponseEntity<>(activityIngestionResponse.toString(),HttpStatus.ACCEPTED);

        ActivitiesStatus screenStatus = activityOrchestratorServiceUtil.setScreenStatus("ACK",activitiesRequestId,responseEntity.getBody(),"SUCCESS");
        activitiesStatusRepo.save(screenStatus);

        activitiesOrchestratorService.processActivitiesRequest(activitiesRequestId,apigeeCorrelationId,activityRequest);

        return responseEntity;
    }
}
