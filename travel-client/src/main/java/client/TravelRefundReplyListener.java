package client;

import client.model.TravelRefundReply;
import client.model.TravelRefundRequest;

public interface TravelRefundReplyListener {
    void onReplyReceived(TravelRefundReply travelRefundReply, TravelRefundRequest travelRefundRequest);
}
