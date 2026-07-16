package com.projects.message_worker.infra.provider;

import java.util.Random;

import com.projects.message_worker.domain.Notification;
import com.projects.message_worker.domain.NotificationProvider;
import com.projects.message_worker.domain.errors.TemporaryProviderException;

public class FakeNotificationProvider implements NotificationProvider {
    private final Random random = new Random();

    @Override
    public void send(Notification notification, String content) {
        System.out.println("SENDING: " + notification);
        
        if(random.nextInt(10) < 3) {
            throw new TemporaryProviderException();
        }

        System.out.println("Notification sent succesfully");
    }
    
}
