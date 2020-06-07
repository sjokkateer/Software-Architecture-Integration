package gateway;

import approval.model.ApprovalReply;
import client.model.TravelRefundReply;
import client.model.TravelRefundRequest;
import com.google.gson.Gson;

import javax.jms.*;
import java.util.HashMap;

public class TravelClientAppGateway {
    private Gson gson = new Gson();

    private ConcreteMessageReceiverGateway messageReceiverGatewayClient;
    private TravelRequestListener travelRequestListener;

    // Is used to determine the destination queue to return back to, based on message/correlationId
    private HashMap<String, Destination> messageIdReturnAddress;

    private MessageSenderDynamicQueue messageSenderGateway;

    public TravelClientAppGateway() {
        messageIdReturnAddress = new HashMap<>();

        messageReceiverGatewayClient = new ConcreteMessageReceiverGateway("travel-refund-broker-request");
        messageReceiverGatewayClient.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    String messageContent = ((TextMessage) message).getText();
                    TravelRefundRequest travelRefundRequest = gson.fromJson(messageContent, TravelRefundRequest.class);

                    messageIdReturnAddress.put(message.getJMSMessageID(), message.getJMSReplyTo());

                    if (travelRequestListener != null) {
                        travelRequestListener.onRequestReceived(travelRefundRequest, message.getJMSMessageID());
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

        messageSenderGateway = new MessageSenderDynamicQueue("irrelevant");
    }

    public void setTravelRequestListener(TravelRequestListener travelRequestListener) {
        this.travelRequestListener = travelRequestListener;
    }

    public void sendReply(TravelRefundReply travelRefundReply, String correlationId) {
        try {
            String travelRefundReplyJson = gson.toJson(travelRefundReply);

            Message message = messageSenderGateway.createTextMessage(travelRefundReplyJson);
            message.setJMSCorrelationID(correlationId);

            Destination destination = messageIdReturnAddress.get(correlationId);
            messageSenderGateway.send(destination, message);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
