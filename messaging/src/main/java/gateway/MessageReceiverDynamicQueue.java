package gateway;

import javax.jms.Destination;
import javax.jms.JMSException;

public class MessageReceiverDynamicQueue extends MessageReceiverGateway {

    public MessageReceiverDynamicQueue() {
        super("");
    }

    @Override
    protected Destination createQueue() throws JMSException {
        return session.createTemporaryQueue();
    }

    public Destination getDestinationQueue() {
        return destinationQueue;
    }
}
