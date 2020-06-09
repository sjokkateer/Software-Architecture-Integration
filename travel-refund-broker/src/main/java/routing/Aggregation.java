package routing;

/**
 * Class responsible for creating unique aggregation ids during the current session.
 *
 * An object holds information on how many messages were sent for that aggregation id.
 */
public class Aggregation {
    private static int id = 1;

    private int aggregationId;
    private int numberOfMessages;

    public Aggregation() {
        aggregationId = id++;
    }

    public int getAggregationId() {
        return aggregationId;
    }

    public void setAggregationId(int aggregationId) {
        this.aggregationId = aggregationId;
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    public void setNumberOfMessages(int numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }
}
