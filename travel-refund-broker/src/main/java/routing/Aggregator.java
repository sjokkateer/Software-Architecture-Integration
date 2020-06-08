package routing;

import approval.model.ApprovalReply;
import com.google.gson.Gson;
import gateway.ApprovalReplyListener;
import gateway.ConcreteMessageReceiverGateway;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Aggregator {
    private static final String AGGREGATION_PROPERTY = "aggregationID";

    private Gson gson = new Gson();

    private HashMap<Integer, Integer> aggregations;
    private HashMap<Integer, List<ApprovalReply>> aggregatedApprovals;

    private ConcreteMessageReceiverGateway receiverGateway;

    private ApprovalReplyListener approvalReplyListener;

    public Aggregator() {
        aggregations = new HashMap<>();
        aggregatedApprovals = new HashMap<>();

        receiverGateway = new ConcreteMessageReceiverGateway("travel-refund-broker-reply");

        receiverGateway.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    String messageContent = ((TextMessage)message).getText();
                    ApprovalReply approvalReply = gson.fromJson(messageContent, ApprovalReply.class);

                    int aggregationId = message.getIntProperty(AGGREGATION_PROPERTY);
                    int numberOfApprovalReplies = addApprovalReply(approvalReply, aggregationId);
                    int numberOfMessagesSent = aggregations.get(aggregationId);

                    if (numberOfApprovalReplies == numberOfMessagesSent) {
                        // Create new approval.
                        ApprovalReply finalReply = createApprovalReply(aggregationId);
                        // Inform our listener that it's time.
                        if (approvalReplyListener != null) {
                            // All messages should have the same correlation id by our logic.
                            approvalReplyListener.onReplyReceived(finalReply, message.getJMSCorrelationID());
                        }
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private ApprovalReply createApprovalReply(int aggregationId) {
        List<ApprovalReply> approvalReplies = aggregatedApprovals.get(aggregationId);

        ApprovalReply newReply = new ApprovalReply();
        boolean first = true;

        for (ApprovalReply approvalReply: approvalReplies) {
            if (!first && !approvalReply.isApproved()) {
                boolean isApproved = newReply.isApproved() && approvalReply.isApproved();

                newReply.setApproved(isApproved);

                String reasonRejected = newReply.getReasonRejected();

                if (!reasonRejected.equals("")) {
                    reasonRejected += " & ";
                }

                reasonRejected += approvalReply.getReasonRejected();

                newReply.setReasonRejected(reasonRejected);
            }

            // Always take the data of the first reply in the collection.
            if (first) {
                newReply.setApproved(approvalReply.isApproved());
                // Assuming that reason rejected will always be empty if approved was true.
                newReply.setReasonRejected(approvalReply.getReasonRejected());
                first = false;
            }
        }

        return newReply;
    }

    // returns the number of items in the list.
    private int addApprovalReply(ApprovalReply approvalReply, int aggregationId) {
        List<ApprovalReply> approvalReplies = aggregatedApprovals.getOrDefault(aggregationId, new ArrayList<>());
        approvalReplies.add(approvalReply);
        aggregatedApprovals.put(aggregationId, approvalReplies);

        return approvalReplies.size();
    }

    public void add(Aggregation aggregation) {
        aggregations.put(aggregation.getAggregationId(), aggregation.getNumberOfMessages());
    }

    public void setApprovalReplyListener(ApprovalReplyListener approvalReplyListener) {
        this.approvalReplyListener = approvalReplyListener;
    }

    // then on receival of reply add the approval to the list and match the count of the approvals
    // versus the number in aggregations.

    // Once this number matches we could notify the controller to get our newly formed approval and do something with it.

}
