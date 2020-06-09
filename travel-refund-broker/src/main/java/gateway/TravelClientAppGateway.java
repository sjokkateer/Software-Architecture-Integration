package gateway;

import client.model.TravelRefundReply;
import client.model.TravelRefundRequest;
import com.google.gson.Gson;

import javax.jms.*;
import java.util.HashMap;

/**
 * Gateway that is responsible for handling the travel client side of the
 * travel broker system.
 */
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
        // Listener to be called when a travel refund request is obtained.
        messageReceiverGatewayClient.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    String messageContent = ((TextMessage) message).getText();
                    TravelRefundRequest travelRefundRequest = gson.fromJson(messageContent, TravelRefundRequest.class);

                    // Stores the destination queue under the original message id, which eventually we obtain by
                    // correlation id to return a reply.
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

    /**
     * Sets a listener to be called when a travel refund request is obtained.
     * @param travelRequestListener
     */
    public void setTravelRequestListener(TravelRequestListener travelRequestListener) {
        this.travelRequestListener = travelRequestListener;
    }

    /**
     * Method is used to send back a refund reply to the client belonging to correlation id.
     *
     * Obtains the destination queue by correlation id from the cache and sends back the corresponding
     * travel refund reply.
     *
     * @param travelRefundReply
     * @param correlationId
     */
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
