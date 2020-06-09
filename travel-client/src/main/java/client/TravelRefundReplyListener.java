package client;

import client.model.TravelRefundReply;
import client.model.TravelRefundRequest;

/**
 * Interface used to implement as listener for when travel refund replies are received.
 */
public interface TravelRefundReplyListener {
    void onReplyReceived(TravelRefundReply travelRefundReply, TravelRefundRequest travelRefundRequest);
}
