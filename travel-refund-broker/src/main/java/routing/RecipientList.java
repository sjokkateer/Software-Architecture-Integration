package routing;

import approval.model.ApprovalRequest;
import com.google.gson.Gson;
import gateway.MessageSenderGateway;

import javax.jms.JMSException;
import javax.jms.Message;

public class RecipientList {
    // This will be part of the TravelApprovalAppGateway, and will communicate through this.
    // Gets two messageSenders, one to each queue.
    // Will always send off the approval request to the internship admin
    // if condition matched also to the financial service.
    //
    private static final String AGGREGATION_PROPERTY = "aggregationID";

    private MessageSenderGateway administrationSenderGateway;
    private MessageSenderGateway financeSenderGateway;


    public RecipientList() {
        administrationSenderGateway = new MessageSenderGateway("administrationRequestQueue");
        financeSenderGateway = new MessageSenderGateway("financeRequestQueue");
    }

    public Aggregation send(ApprovalRequest approvalRequest, String messageId) {
        Gson gson = new Gson();

        String jsonAR = gson.toJson(approvalRequest, ApprovalRequest.class);
        Message message = administrationSenderGateway.createTextMessage(jsonAR);

        Aggregation aggregation = new Aggregation();

        try {
            message.setJMSCorrelationID(messageId);

            int numberOfMessagesSent = 1;
            message.setIntProperty(AGGREGATION_PROPERTY, aggregation.getAggregationId());

            if (approvalRequest.getCosts() >= 50) {
                numberOfMessagesSent++;
                financeSenderGateway.send(message);
            }

            administrationSenderGateway.send(message);
            aggregation.setNumberOfMessages(numberOfMessagesSent);
        } catch (JMSException e) {
            e.printStackTrace();
        }

        return aggregation;
    }
}
