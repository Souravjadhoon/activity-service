package com.activity_service.activity_service.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoggingAggregator {
    @Value("${logging.debug.enabled:false}")
    private boolean isDebugEnabled;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAggregator.class);

    public void logInfoMessage(String uuid, String className, String methodName, String serviceName, String message){
        String logMessage = String.format("[CID: %s] [%s] [%s.%s] - %s",
                uuid, serviceName, className, methodName, message);
        LOGGER.info(logMessage);
    }

    public void logDebugMessage(String uuid, String className, String methodName, String serviceName, String message){
        if(isDebugEnabled) {
            String logMessage = String.format("[CID: %s] [%s] [%s.%s] - %s",
                    uuid, serviceName, className, methodName, message);
            LOGGER.info(logMessage);
        }
        }

    public void logErrorMessage(String uuid, String className, String methodName, String serviceName, String message){

            String logMessage = String.format("[CID: %s] [%s] [%s.%s] - %s",
                    uuid, serviceName, className, methodName, message);
            LOGGER.error(logMessage);

    }
}
