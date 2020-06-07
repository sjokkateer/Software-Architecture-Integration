package gateway;

import approval.model.ApprovalReply;
import approval.model.ApprovalRequest;
import com.google.gson.Gson;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class TravelApprovalAppGateway {
    private Gson gson = new Gson();

    private MessageSenderGateway senderGateway;
    private ConcreteMessageReceiverGateway receiverGateway;

    private ApprovalReplyListener approvalReplyListener;

    public TravelApprovalAppGateway() {
        senderGateway = new MessageSenderGateway("administrationRequestQueue");
        receiverGateway = new ConcreteMessageReceiverGateway("travel-refund-broker-reply");

        receiverGateway.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    String messageContent = ((TextMessage)message).getText();
                    ApprovalReply approvalReply = gson.fromJson(messageContent, ApprovalReply.class);

                    if (approvalReplyListener != null) {
                        approvalReplyListener.onReplyReceived(approvalReply, message.getJMSCorrelationID());
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendApprovalRequest(ApprovalRequest approvalRequest, String messageId) {
        String jsonAR = gson.toJson(approvalRequest, ApprovalRequest.class);
        Message message = senderGateway.createTextMessage(jsonAR);

        try {
            message.setJMSCorrelationID(messageId);
        } catch (JMSException e) {
            e.printStackTrace();
        }

        senderGateway.send(message);
    }

    public void setApprovalReplyListener(ApprovalReplyListener approvalReplyListener) {
        this.approvalReplyListener = approvalReplyListener;
    }
}
