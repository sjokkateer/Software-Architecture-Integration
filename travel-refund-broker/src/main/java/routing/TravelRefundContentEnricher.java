package routing;

import client.model.TravelRefundRequest;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Class is responsible for obtaining additional data and enriching a travel refund request.
 */
public class TravelRefundContentEnricher {
    private HttpClient client;

    public TravelRefundContentEnricher() {
        client = new HttpClient();
    }

    /**
     * Method will enrich travel refund request.
     * If transport mode is CAR, it will obtain the price per km through a rest service
     * and add it to the travel refund request.
     *
     * @param travelRefundRequest
     * @return
     */
    public TravelRefundRequest enrich(TravelRefundRequest travelRefundRequest) {
        switch(travelRefundRequest.getMode()) {
            case CAR:
                Response response = client.getServiceTarget().path("priceperkm/rest/price").request().accept(MediaType.TEXT_PLAIN).get();
                Double price = response.readEntity(Double.class);
                // Since it is price per km, calculate total and assign it.
                double costs = price * travelRefundRequest.getKilometers();
                travelRefundRequest.setCosts(costs);
                break;
            default:
                break;
        }

        return travelRefundRequest;
    }
}
