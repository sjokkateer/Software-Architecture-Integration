package gateway;

import approval.model.ApprovalReply;

/**
 * Interface used to implement as listener for when travel refund approval replies are received.
 */
public interface ApprovalReplyListener {
    void onReplyReceived(ApprovalReply approvalReply, String correlationId);
}
