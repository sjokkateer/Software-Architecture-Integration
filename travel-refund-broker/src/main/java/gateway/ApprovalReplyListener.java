package gateway;

import approval.model.ApprovalReply;

public interface ApprovalReplyListener {
    void onReplyReceived(ApprovalReply approvalReply, String correlationId);
}
