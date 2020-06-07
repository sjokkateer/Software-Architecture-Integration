package client.gui;


import client.model.TravelRefundReply;
import client.model.TravelRefundRequest;

public class ClientListLine {

    private TravelRefundRequest request;
    private TravelRefundReply reply;

    public ClientListLine(TravelRefundRequest request, TravelRefundReply reply) {
        this.request = request;
        this.reply = reply;
    }

    public TravelRefundRequest getRequest() {
        return request;
    }

    private void setRequest(TravelRefundRequest request) {
        this.request = request;
    }

    public TravelRefundReply getReply() {
        return reply;
    }

    public void setReply(TravelRefundReply reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        return request.toString() + "  --->  " + ((reply != null) ? reply.toString() : "waiting...");
    }

}
