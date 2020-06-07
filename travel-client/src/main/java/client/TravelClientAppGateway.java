package client;

import client.model.TravelRefundReply;
import client.model.TravelRefundRequest;
import com.google.gson.Gson;
import gateway.MessageReceiverDynamicQueue;
import gateway.MessageReceiverGateway;
import gateway.MessageSenderGateway;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashMap;

public class TravelClientAppGateway {
    private MessageSenderGateway messageSenderGateway;
    // This is a gateway class, but will use temporary queues for return address pattern.
    private MessageReceiverDynamicQueue messageReceiverGateway;

    private Gson gson = new Gson();
    private HashMap<String, TravelRefundRequest> cache = new HashMap<>();

    public TravelClientAppGateway() {
        messageSenderGateway = new MessageSenderGateway("travel-refund-broker-request");
        messageReceiverGateway = new MessageReceiverDynamicQueue();
//        messageReceiverGateway.setListener(new MessageListener() {
//            @Override
//            public void onMessage(Message message) {
//                try {
//                    TravelRefundRequest travelRefundRequest = cache.get(message.getJMSCorrelationID());
//                    String trrJSON = ((TextMessage) message).getText();
//                    TravelRefundReply travelRefundReply = gson.fromJson(trrJSON, TravelRefundReply.class);
//
//                    // loanReplyListener.onReplyReceived(loanRequest, loanReply);
//                } catch (JMSException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    // Requires an interface that will be called when a reply is received.
    public void setReplyListener() {

    }

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
