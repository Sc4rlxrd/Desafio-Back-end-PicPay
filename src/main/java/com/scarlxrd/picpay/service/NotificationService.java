package com.scarlxrd.picpay.service;

import com.scarlxrd.picpay.client.NotificationClient;
import com.scarlxrd.picpay.entities.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationClient notificationClient;
    private final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    public void  setNotification(Transfer transfer){
        try {
            logger.info("Sending notification...");

            var resp = notificationClient.sendNotification(transfer);

            if (resp.getStatusCode().isError()) {
                logger.error("Error while sending notification, status code is not OK");
            }

        } catch (Exception e) {
            logger.error("Error while sending notification", e);
        }
    }
}

