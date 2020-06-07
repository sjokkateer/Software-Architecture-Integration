package approval;

import approval.model.ApprovalRequest;

public interface ApprovalRequestListener {
    void onRequestReceived(ApprovalRequest approvalRequest);
}
