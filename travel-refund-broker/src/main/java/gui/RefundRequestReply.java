package gui;

import approval.model.ApprovalReply;
import client.model.TravelRefundRequest;

/**
 * Class is responsible for displaying the refund request and approval in a certain way.
 *
 * This class is exclusively used in the GUI.
 */
public class RefundRequestReply {
    public TravelRefundRequest travelRefundRequest;
    public ApprovalReply approvalReply;

    public RefundRequestReply(TravelRefundRequest travelRefundRequest) {
        this.travelRefundRequest = travelRefundRequest;
    }

    public void setApprovalReply(ApprovalReply approvalReply) {
        this.approvalReply = approvalReply;
    }

    @Override
    public String toString() {
        return travelRefundRequest.toString() + "  --->  " + ((approvalReply !=null)? approvalReply.toString() : "waiting for approval reply...");
    }
}
