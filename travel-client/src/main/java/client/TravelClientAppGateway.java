package client;

import client.model.TravelRefundReply;
import client.model.TravelRefundRequest;
import com.google.gson.Gson;
import gateway.MessageReceiverDynamicQueue;
import gateway.MessageSenderGateway;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashMap;

/**
 * Gateway that is responsible for handling the travel client side of travel client system.
 */
public class TravelClientAppGateway {
    private MessageSenderGateway messageSenderGateway;
    // This is a gateway class, but will use temporary queues for return address pattern.
    private MessageReceiverDynamicQueue messageReceiverGateway;
    private TravelRefundReplyListener travelRefundReplyListener;

    private Gson gson = new Gson();
    private HashMap<String, TravelRefundRequest> cache = new HashMap<>();

    public TravelClientAppGateway() {
        messageSenderGateway = new MessageSenderGateway("travel-refund-broker-request");

        messageReceiverGateway = new MessageReceiverDynamicQueue();
        // A listener called when a travel refund reply is received.
        messageReceiverGateway.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    String messageContent = ((TextMessage)message).getText();

                    TravelRefundReply travelRefundReply = gson.fromJson(messageContent, TravelRefundReply.class);

                    if (travelRefundReplyListener != null) {
                        TravelRefundRequest originalRequest = cache.get(message.getJMSCorrelationID());
                        travelRefundReplyListener.onReplyReceived(travelRefundReply, originalRequest);
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Sets a listener which is called when a travel refund reply is received.
     *
     * @param travelRefundReplyListener
     */
    public void setReplyListener(TravelRefundReplyListener travelRefundReplyListener) {
        this.travelRefundReplyListener = travelRefundReplyListener;
    }

    /**
     * Method is used to apply for refunds, which handles of the conversion of a travel
     * refund request into a message and send it off.
     *
     * @param travelRefundRequest
     */
    public void applyForRefund(TravelRefundRequest travelRefundRequest) {
        String trr = gson.toJson(travelRefundRequest, TravelRefundRequest.class);
        Message msg = messageSenderGateway.createTextMessage(trr);

        try {
            msg.setJMSReplyTo(messageReceiverGateway.getDestinationQueue());
        } catch (JMSException e) {
            e.printStackTrace();
        }

        messageSenderGateway.send(msg);

        try {
            cache.put(msg.getJMSMessageID(), travelRefundRequest);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
