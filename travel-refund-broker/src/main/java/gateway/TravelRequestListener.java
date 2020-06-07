package gateway;

import client.model.TravelRefundRequest;

public interface TravelRequestListener {
    void onRequestReceived(TravelRefundRequest loanRequest, String originalMessageId);
}
