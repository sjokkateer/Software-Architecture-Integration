package gui;

import approval.model.ApprovalReply;
import client.model.TravelRefundRequest;

public class RefundRequestReply {
    public TravelRefundRequest travelRefundRequest;
    public ApprovalReply approvalReply;

    public RefundRequestReply(TravelRefundRequest travelRefundRequest) {
        this.travelRefundRequest = travelRefundRequest;
    }

    @Override
    public String toString() {
        return travelRefundRequest.toString() + "  --->  " + ((approvalReply !=null)? approvalReply.toString() : "waiting for approval reply...");
    }
}
