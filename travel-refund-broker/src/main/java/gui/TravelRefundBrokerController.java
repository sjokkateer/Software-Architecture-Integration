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

import java.util.HashMap;

/**
 * Class is responsible for managing the communication between gateways and the applications.
 * It is also responsible for some administration of travel refund requests and object creation.
 */
public class TravelRefundBrokerController {
    private TravelClientAppGateway travelClientAppGateway;
    private TravelRequestListener travelRequestListener;

    private TravelRefundContentEnricher travelRefundContentEnricher;

    private TravelApprovalAppGateway travelApprovalAppGateway;
    private ApprovalReplyListener approvalReplyListener;

    private HashMap<String, TravelRefundRequest> cache;

    public TravelRefundBrokerController() {
        cache = new HashMap<>();

        travelRefundContentEnricher = new TravelRefundContentEnricher();

        travelClientAppGateway = new TravelClientAppGateway();
        // Listener will be called when a new refund request is received.
        travelClientAppGateway.setTravelRequestListener(new TravelRequestListener() {
            @Override
            public void onRequestReceived(TravelRefundRequest travelRefundRequest, String originalMessageId) {
                travelRefundRequest = travelRefundContentEnricher.enrich(travelRefundRequest);
                // Cache the originally enriched travel refund request such that we can obtain the price later on.
                cache.put(originalMessageId, travelRefundRequest);

                ApprovalRequest approvalRequest = new ApprovalRequest(travelRefundRequest.getTeacher(), travelRefundRequest.getStudent(), travelRefundRequest.getCosts());
                travelApprovalAppGateway.sendApprovalRequest(approvalRequest, originalMessageId);

                if (travelRequestListener != null) {
                    // Is the GUI's listener, which will handle updating the gui with the info from travel refund request.
                    travelRequestListener.onRequestReceived(travelRefundRequest, originalMessageId);
                }
            }
        });

        travelApprovalAppGateway = new TravelApprovalAppGateway();
        // Listener will be called when a new approval reply is received.
        travelApprovalAppGateway.setApprovalReplyListener(new ApprovalReplyListener() {
            @Override
            public void onReplyReceived(ApprovalReply approvalReply, String correlationId) {
                // Will update the GUI through a listener.
                if (approvalReplyListener != null) {
                    approvalReplyListener.onReplyReceived(approvalReply, correlationId);
                }

                // Forwards the reply to the client through the client app gateway.
                double costs = 0.0;
                TravelRefundRequest originalRequest = cache.get(correlationId);

                if (originalRequest != null) {
                    // Update the costs for our overview since this is not present on the reply.
                    costs = originalRequest.getCosts();
                }

                // Create a new refund reply object and send it back to the corresponding client.
                TravelRefundReply travelRefundReply = new TravelRefundReply(approvalReply.isApproved(), approvalReply.getReasonRejected(), costs);
                travelClientAppGateway.sendReply(travelRefundReply, correlationId);
            }
        });
    }

    /**
     * Assigns a listener to be called when a request is received.
     *
     * @param travelRequestListener
     */
    public void setTravelRequestListener(TravelRequestListener travelRequestListener) {
        this.travelRequestListener = travelRequestListener;
    }

    /**
     * Assigns a listener to be called when a reply is received.
     *
     * @param approvalReplyListener
     */
    public void setApprovalReplyListener(ApprovalReplyListener approvalReplyListener) {
        this.approvalReplyListener = approvalReplyListener;
    }
}
