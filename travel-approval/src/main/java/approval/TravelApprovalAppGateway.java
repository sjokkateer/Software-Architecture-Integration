package approval;

import approval.model.ApprovalReply;
import approval.model.ApprovalRequest;
import com.google.gson.Gson;
import gateway.ConcreteMessageReceiverGateway;
import gateway.MessageSenderGateway;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashMap;

/**
 * Gateway that is responsible for handling the travel approval side of the
 * travel approval system.
 */
public class TravelApprovalAppGateway {
    private Gson gson = new Gson();

    private ConcreteMessageReceiverGateway messageReceiverGateway;
    private MessageSenderGateway messageSenderGateway;

    private ApprovalRequestListener approvalRequestListener;

    private HashMap<String, Integer> idToAggregationId;

    private static final String AGGREGATION_PROPERTY = "aggregationID";

    public TravelApprovalAppGateway(String queueName) {
        idToAggregationId = new HashMap<>();

        messageReceiverGateway = new ConcreteMessageReceiverGateway(queueName);

        // Is called when a new approval request is obtained.
        messageReceiverGateway.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    String messageContent = ((TextMessage)message).getText();
                    ApprovalRequest approvalRequest = gson.fromJson(messageContent, ApprovalRequest.class);

                    String correlationId = message.getJMSCorrelationID();
                    // Stores the aggregation id under the correlation id to be used later (aggregation id) when sending a reply
                    idToAggregationId.put(correlationId, message.getIntProperty(AGGREGATION_PROPERTY));

                    approvalRequest.setId(correlationId);

                    if (approvalRequestListener != null) {
                        approvalRequestListener.onRequestReceived(approvalRequest);
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

        messageSenderGateway = new MessageSenderGateway("travel-refund-broker-reply");
    }

    /**
     * Sets a listener to be called when an approval request is obtained.
     *
     * @param approvalRequestListener
     */
    public void setApprovalRequestListener(ApprovalRequestListener approvalRequestListener) {
        this.approvalRequestListener = approvalRequestListener;
    }

    /**
     * Method used to send approval reply objects back to the broker, including additional message setup.
     *
     * @param reply
     * @param correlationId
     */
    public void sendApprovalReply(ApprovalReply reply, String correlationId) {
        try {
            String replyJson = gson.toJson(reply);
            Message message = messageSenderGateway.createTextMessage(replyJson);
            message.setJMSCorrelationID(correlationId);

            // Add back the aggregation id.
            int aggregationId = idToAggregationId.get(correlationId);
            message.setIntProperty(AGGREGATION_PROPERTY, aggregationId);

            messageSenderGateway.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
