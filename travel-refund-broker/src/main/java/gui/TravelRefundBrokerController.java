package gui;

import approval.model.ApprovalReply;
import approval.model.ApprovalRequest;
import client.model.TravelRefundReply;
import client.model.TravelRefundRequest;
import gateway.ApprovalReplyListener;
import gateway.TravelApprovalAppGateway;
import gateway.TravelClientAppGateway;
import gateway.TravelRequestListener;
import routing.TravelRefundContentEnricher;

public class TravelRefundBrokerController {
    private TravelClientAppGateway travelClientAppGateway;
    private TravelRequestListener travelRequestListener;

    private TravelRefundContentEnricher travelRefundContentEnricher;

    private TravelApprovalAppGateway travelApprovalAppGateway;
    private ApprovalReplyListener approvalReplyListener;

    public TravelRefundBrokerController() {
        travelRefundContentEnricher = new TravelRefundContentEnricher();

        travelClientAppGateway = new TravelClientAppGateway();
        travelClientAppGateway.setTravelRequestListener(new TravelRequestListener() {
            @Override
            public void onRequestReceived(TravelRefundRequest travelRefundRequest, String originalMessageId) {
                travelRefundRequest = travelRefundContentEnricher.enrich(travelRefundRequest);

                // Need to forward to recepient list that should determine to which queue(s) the approvalRequest is sent

                // For now with one travel approval application (INTERNSHIP) which is always to be involved.
                ApprovalRequest approvalRequest = new ApprovalRequest(travelRefundRequest.getTeacher(), travelRefundRequest.getStudent(), travelRefundRequest.getCosts());
                travelApprovalAppGateway.sendApprovalRequest(approvalRequest, originalMessageId);

                if (travelRequestListener != null) {
                    // Is the GUI's listener, which will handle updating the gui with the info from travel refund request.
                    travelRequestListener.onRequestReceived(travelRefundRequest, originalMessageId);
                }
            }
        });

        travelApprovalAppGateway = new TravelApprovalAppGateway();
        travelApprovalAppGateway.setApprovalReplyListener(new ApprovalReplyListener() {
            @Override
            public void onReplyReceived(ApprovalReply approvalReply, String correlationId) {
                // Will update the GUI through a listener.
                if (approvalReplyListener != null) {
                    approvalReplyListener.onReplyReceived(approvalReply, correlationId);
                }

                // Forwards the reply to the client through the client app gateway.
                double costs = 0.0;
                TravelRefundReply travelRefundReply = new TravelRefundReply(approvalReply.isApproved(), approvalReply.getReasonRejected(), costs);
                travelClientAppGateway.sendReply(travelRefundReply, correlationId);
            }
        });
    }

    public void setTravelRequestListener(TravelRequestListener travelRequestListener) {
        this.travelRequestListener = travelRequestListener;
    }

    public void setApprovalReplyListener(ApprovalReplyListener approvalReplyListener) {
        this.approvalReplyListener = approvalReplyListener;
    }
}
