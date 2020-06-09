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

/**
 * Class responsible for aggregating messages and managing when an event should be triggered,
 * indicating all messages are aggregated.
 */
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

        // Listener that will be executed when an approval reply is received.
        receiverGateway.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    String messageContent = ((TextMessage)message).getText();
                    ApprovalReply approvalReply = gson.fromJson(messageContent, ApprovalReply.class);

                    // Get the aggregation data and process it
                    int aggregationId = message.getIntProperty(AGGREGATION_PROPERTY);
                    int numberOfApprovalReplies = addApprovalReply(approvalReply, aggregationId);
                    int numberOfMessagesSent = aggregations.get(aggregationId);

                    // Once we received all replies we were supposed to aggregate we invoke the event.
                    if (numberOfApprovalReplies == numberOfMessagesSent) {
                        // Create a new reply based on set rules.
                        ApprovalReply finalReply = createApprovalReply(aggregationId);
                        if (approvalReplyListener != null) {
                            // Provide the listener with the new reply and correlation id for processing.
                            approvalReplyListener.onReplyReceived(finalReply, message.getJMSCorrelationID());
                        }
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Creates a new approval reply with the information of all received approval replies
     * according to specification.
     *
     * If rejected: List application that rejected the request.
     * If Approved: Leave rejection reason empty.
     *
     * @param aggregationId
     * @return
     */
    private ApprovalReply createApprovalReply(int aggregationId) {
        List<ApprovalReply> approvalReplies = aggregatedApprovals.get(aggregationId);

        ApprovalReply newReply = new ApprovalReply();
        boolean first = true;

        for (ApprovalReply approvalReply: approvalReplies) {
            //
            if (!first && !approvalReply.isApproved()) {
                boolean isApproved = newReply.isApproved() && approvalReply.isApproved();

                newReply.setApproved(isApproved);

                String reasonRejected = newReply.getReasonRejected();

                // Adds a separation symbol if the current reply contains a rejection.
                if (!reasonRejected.equals("")) {
                    reasonRejected += " & ";
                }

                // Adds the reason for rejection, either empty string or actual reason.
                reasonRejected += approvalReply.getReasonRejected();

                // Sets the compounded reason for rejection on the new reply.
                // this is thus every app that rejected the request.
                newReply.setReasonRejected(reasonRejected);
            }

            // Always take the data of the first reply in the collection.
            if (first) {
                newReply.setApproved(approvalReply.isApproved());

                newReply.setReasonRejected(approvalReply.getReasonRejected());
                first = false;
            }
        }

        return newReply;
    }

    /**
     * Adds an approval reply to the collection of received approval replies for aggregation id.
     *
     * Returns the size of the collection of received approval replies.
     *
     * @param approvalReply
     * @param aggregationId
     * @return
     */
    private int addApprovalReply(ApprovalReply approvalReply, int aggregationId) {
        List<ApprovalReply> approvalReplies = aggregatedApprovals.getOrDefault(aggregationId, new ArrayList<>());
        approvalReplies.add(approvalReply);
        aggregatedApprovals.put(aggregationId, approvalReplies);

        return approvalReplies.size();
    }

    /**
     * Adds an aggregation object to the hashmap.
     * @param aggregation
     */
    public void add(Aggregation aggregation) {
        aggregations.put(aggregation.getAggregationId(), aggregation.getNumberOfMessages());
    }

    /**
     * Sets a listener that will be called once the condition: number of messages aggregated
     * is equal to the number of sent messages is met.
     * @param approvalReplyListener
     */
    public void setApprovalReplyListener(ApprovalReplyListener approvalReplyListener) {
        this.approvalReplyListener = approvalReplyListener;
    }
}
