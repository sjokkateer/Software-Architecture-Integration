package gateway;

import approval.model.ApprovalReply;
import approval.model.ApprovalRequest;
import routing.Aggregation;
import routing.Aggregator;
import routing.RecipientList;

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
            public void onReplyReceived(ApprovalReply approvalReply, String correlationId) {
                if (approvalReplyListener != null) {
                    approvalReplyListener.onReplyReceived(approvalReply, correlationId);
                }
            }
        });
    }

    public void sendApprovalRequest(ApprovalRequest approvalRequest, String messageId) {
        // Pass the responsibility of message related activities to the recipient list.
        Aggregation aggregation = recipientList.send(approvalRequest, messageId);

        // Pass the required information to the aggregator.
        aggregator.add(aggregation);
    }

    // ApprvalReplyListener should now be handled by the aggregator.
    public void setApprovalReplyListener(ApprovalReplyListener approvalReplyListener) {
        this.approvalReplyListener = approvalReplyListener;
    }
}
