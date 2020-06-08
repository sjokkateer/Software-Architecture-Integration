package routing;

import approval.model.ApprovalRequest;
import com.google.gson.Gson;
import gateway.MessageSenderGateway;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.List;

public class RecipientList {
    private static final String AGGREGATION_PROPERTY = "aggregationID";

    private List<RuleBasedSender> ruleBasedSenders;

    public RecipientList() {
        ruleBasedSenders = RuleBasedSender.loadFromFile();
    }

    public Aggregation send(ApprovalRequest approvalRequest, String messageId) {
        Gson gson = new Gson();
        String jsonAR = gson.toJson(approvalRequest, ApprovalRequest.class);

        Aggregation aggregation = new Aggregation();

        try {
            int numberOfMessagesSent = 0;
            for (RuleBasedSender ruleBasedSender : ruleBasedSenders) {
                Message message = ruleBasedSender.createTextMessage(jsonAR);
                message.setJMSCorrelationID(messageId);
                message.setIntProperty(AGGREGATION_PROPERTY, aggregation.getAggregationId());

                if (ruleBasedSender.send(approvalRequest.getCosts(), message)) {
                    numberOfMessagesSent++;
                }
            }

            aggregation.setNumberOfMessages(numberOfMessagesSent);
        } catch (JMSException e) {
            e.printStackTrace();
        }

        return aggregation;
    }
}
