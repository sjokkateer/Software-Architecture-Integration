package gui;

import gateway.TravelClientAppGateway;
import gateway.TravelRequestListener;

public class TravelRefundBrokerController {
    TravelClientAppGateway gateway;

    public TravelRefundBrokerController() {
        gateway = new TravelClientAppGateway();
    }

    public void setTravelRequestListener(TravelRequestListener travelRequestListener) {
        gateway.setTravelRequestListener(travelRequestListener);
    }
}
