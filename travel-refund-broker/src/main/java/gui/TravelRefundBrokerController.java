package gui;

import approval.model.ApprovalRequest;
import client.model.TravelRefundRequest;
import gateway.TravelApprovalAppGateway;
import gateway.TravelClientAppGateway;
import gateway.TravelRequestListener;
import routing.TravelRefundContentEnricher;

public class TravelRefundBrokerController {
    private TravelClientAppGateway travelClientAppGateway;
    private TravelRequestListener travelRequestListener;

    private TravelRefundContentEnricher travelRefundContentEnricher;

    //
    private TravelApprovalAppGateway travelApprovalAppGateway;

    public TravelRefundBrokerController() {
        travelClientAppGateway = new TravelClientAppGateway();
        travelRefundContentEnricher = new TravelRefundContentEnricher();
        travelApprovalAppGateway = new TravelApprovalAppGateway();

        travelClientAppGateway.setTravelRequestListener(new TravelRequestListener() {
            @Override
            public void onRequestReceived(TravelRefundRequest travelRefundRequest, String originalMessageId) {
                ApprovalRequest approvalRequest = travelRefundContentEnricher.enrich(travelRefundRequest);

                // Need to forward to recepient list that should determine to which queue(s) the approvalRequest is sent
                // For now with one travel approval application (INTERNSHIP) which is always to be involved.
                travelApprovalAppGateway.sendApprovalRequest(approvalRequest, originalMessageId);

                if (travelRequestListener != null) {
                    // Is the GUI's listener, which will handle updating the gui with the info from travel refund request.
                    travelRequestListener.onRequestReceived(travelRefundRequest, originalMessageId);
                }
            }
        });
    }

    public void setTravelRequestListener(TravelRequestListener travelRequestListener) {
        this.travelRequestListener = travelRequestListener;
    }
}
