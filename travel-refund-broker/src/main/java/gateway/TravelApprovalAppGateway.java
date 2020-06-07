package gateway;

import approval.model.ApprovalRequest;
import com.google.gson.Gson;

import javax.jms.JMSException;
import javax.jms.Message;

public class TravelApprovalAppGateway {
    private Gson gson = new Gson();

    private MessageSenderGateway senderGateway;

    public TravelApprovalAppGateway() {
        senderGateway = new MessageSenderGateway("administrationRequestQueue");
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
}
