package gateway;

import client.model.TravelRefundRequest;

public interface TravelRequestListener {
    void onRequestReceived(TravelRefundRequest travelRefundRequest, String originalMessageId);
}
