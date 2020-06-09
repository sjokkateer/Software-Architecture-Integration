package gateway;

import javax.jms.Destination;
import javax.jms.JMSException;

/**
 * Class is responsible for creating a temporary queue allowing for the return address pattern.
 */
public class MessageReceiverDynamicQueue extends MessageReceiverGateway {

    public MessageReceiverDynamicQueue() {
        super("");
    }

    /**
     * Creates and returns a temporary queue
     *
     * @return
     * @throws JMSException
     */
    @Override
    protected Destination createQueue() throws JMSException {
        return session.createTemporaryQueue();
    }

    /**
     * Returns the destination queue for the message receiver.
     * @return
     */
    public Destination getDestinationQueue() {
        return destinationQueue;
    }
}
