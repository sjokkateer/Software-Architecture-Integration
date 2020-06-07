package routing;

import approval.model.ApprovalRequest;
import client.model.TravelRefundRequest;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class TravelRefundContentEnricher {
    private HttpClient client;

    public TravelRefundContentEnricher() {
        client = new HttpClient();
    }

    public TravelRefundRequest enrich(TravelRefundRequest travelRefundRequest) {
        switch(travelRefundRequest.getMode()) {
            case CAR:
                Response response = client.getServiceTarget().path("priceperkm/rest/price").request().accept(MediaType.TEXT_PLAIN).get();
                Double price = response.readEntity(Double.class);
                travelRefundRequest.setCosts(price);
                break;
            default:
                break;
        }

        return travelRefundRequest;
    }
}
