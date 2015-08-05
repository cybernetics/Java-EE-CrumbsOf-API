package com.javaee.crumbsOfAPI.jms.mdb;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *
 * @author Constantin Alin
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:global/jms/testQueue2"),
    @ActivationConfigProperty(propertyName = "connectionFactory", propertyValue = "java:global/jms/testQueueFactory2")
})
public class QueueMDBListener implements MessageListener {

    public QueueMDBListener() {
    }

    @Override
    public void onMessage(Message message) {
        String msg;
        try {
            msg = ((TextMessage) message).getText();
            System.out.println("Message from Queue: \"" + msg + "\"");
        } catch (JMSException ex) {
            Logger.getLogger(QueueMDBListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
