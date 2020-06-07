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

public class TravelApprovalAppGateway {
    private Gson gson = new Gson();

    private ConcreteMessageReceiverGateway messageReceiverGateway;
    private MessageSenderGateway messageSenderGateway;

    private ApprovalRequestListener approvalRequestListener;

    public TravelApprovalAppGateway(String queueName) {
        messageReceiverGateway = new ConcreteMessageReceiverGateway(queueName);

        messageReceiverGateway.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    String messageContent = ((TextMessage)message).getText();
                    ApprovalRequest approvalRequest = gson.fromJson(messageContent, ApprovalRequest.class);

                    // Use the correlation id as id for the object such that we can communicate back to the broker.
                    approvalRequest.setId(message.getJMSCorrelationID());

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

    public void setApprovalRequestListener(ApprovalRequestListener approvalRequestListener) {
        this.approvalRequestListener = approvalRequestListener;
    }

    public void sendApprovalReply(ApprovalReply reply, String correlationId) {
        try {
            String replyJson = gson.toJson(reply);
            Message message = messageSenderGateway.createTextMessage(replyJson);
            message.setJMSCorrelationID(correlationId);
            messageSenderGateway.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
