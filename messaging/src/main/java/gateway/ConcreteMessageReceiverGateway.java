package gateway;

import javax.jms.Destination;
import javax.jms.JMSException;

public class ConcreteMessageReceiverGateway extends MessageReceiverGateway {

    public ConcreteMessageReceiverGateway(String queueName) {
        super(queueName);
    }

    @Override
    protected Destination createQueue() throws JMSException {
        return session.createQueue(queueName);
    }
}
