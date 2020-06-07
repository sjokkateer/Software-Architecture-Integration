package routing;

import approval.model.ApprovalRequest;
import client.model.TravelRefundRequest;

public class TravelRefundContentEnricher {

    public ApprovalRequest enrich(TravelRefundRequest travelRefundRequest) {
        ApprovalRequest approvalRequest = new ApprovalRequest(travelRefundRequest.getTeacher(), travelRefundRequest.getStudent(), travelRefundRequest.getCosts());

        switch(travelRefundRequest.getMode()) {
            case CAR:
                // Request current price per km and add to travelRefundRequest.
                // double price = ...
                // approvalRequest.setCosts(price);
                break;
            default:
                break;
        }

        return approvalRequest;
    }
}
