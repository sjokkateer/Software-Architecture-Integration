package gateway;

import client.model.TravelRefundRequest;

/**
 * Interface used to implement as listener for when travel refund requests are received.
 */
public interface TravelRequestListener {
    void onRequestReceived(TravelRefundRequest travelRefundRequest, String originalMessageId);
}
