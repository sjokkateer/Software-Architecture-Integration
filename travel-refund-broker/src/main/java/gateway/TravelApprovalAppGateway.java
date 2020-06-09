package gateway;

import approval.model.ApprovalReply;
import approval.model.ApprovalRequest;
import routing.Aggregation;
import routing.Aggregator;
import routing.RecipientList;

/**
 * Gateway that is responsible for handling the travel approval client side of the
 * travel broker system.
 */
public class TravelApprovalAppGateway {
    private RecipientList recipientList;
    private Aggregator aggregator;

    private ApprovalReplyListener approvalReplyListener;

    public TravelApprovalAppGateway() {
        recipientList = new RecipientList();

        aggregator = new Aggregator();
        aggregator.setApprovalReplyListener(new ApprovalReplyListener() {
            @Override
            // Officially it's not just on reply anymore, but a little more complex.
            // As an aggregator is implemented.
            public void onReplyReceived(ApprovalReply approvalReply, String correlationId) {
                if (approvalReplyListener != null) {
                    approvalReplyListener.onReplyReceived(approvalReply, correlationId);
                }
            }
        });
    }

    /**
     * Forwards the obtained approval request through the recipient list.
     *
     * @param approvalRequest
     * @param messageId
     */
    public void sendApprovalRequest(ApprovalRequest approvalRequest, String messageId) {
        // Pass the responsibility of message related activities to the recipient list.
        Aggregation aggregation = recipientList.send(approvalRequest, messageId);

        // Pass the required information to the aggregator, to keep track of number of messages
        // and when to trigger an event.
        aggregator.add(aggregation);
    }

    /**
     * Sets a listener which is called when the conditions are met by the aggregator
     * thus not on a single approval reply anymore, but once the aggregator determines
     * that all replies are received.
     *
     * @param approvalReplyListener
     */
    public void setApprovalReplyListener(ApprovalReplyListener approvalReplyListener) {
        this.approvalReplyListener = approvalReplyListener;
    }
}
