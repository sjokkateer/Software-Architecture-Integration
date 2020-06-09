package gateway;

import javax.jms.Destination;
import javax.jms.JMSException;

/**
 * Class is used when a queue with a fixed queue name is created.
 */
public class ConcreteMessageReceiverGateway extends MessageReceiverGateway {

    public ConcreteMessageReceiverGateway(String queueName) {
        super(queueName);
    }

    /**
     * Returns a queue based on queue name.
     *
     * @return
     * @throws JMSException
     */
    @Override
    protected Destination createQueue() throws JMSException {
        return session.createQueue(queueName);
    }
}
