package approval;

import approval.model.ApprovalRequest;

/**
 * Interface used to implement as listener for when an approval request is received.
 */
public interface ApprovalRequestListener {
    void onRequestReceived(ApprovalRequest approvalRequest);
}
