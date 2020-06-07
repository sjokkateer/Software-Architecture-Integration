package routing;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.ClientConfig;

import java.net.URI;

public class HttpClient {
    private String protocol;
    private String host;
    private int port;

    private WebTarget serviceTarget;

    public HttpClient(String protocol, String host, int port) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        URI baseURI = UriBuilder.fromUri(getBaseUrl()).build();
        serviceTarget = client.target(baseURI);
    }

    public HttpClient() {
        this("http", "localhost", 8080);
    }

    private String getBaseUrl() {
        return protocol + "://" + host + ":" + port;
    }

    public WebTarget getServiceTarget() {
        return serviceTarget;
    }
}
