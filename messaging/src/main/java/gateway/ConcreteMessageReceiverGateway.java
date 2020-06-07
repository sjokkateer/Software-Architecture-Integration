package gateway;

import javax.jms.Destination;
import javax.jms.JMSException;

public class ConcreteMessageReceiverGateway extends MessageReceiverGateway {
    protected String queueName;

    public ConcreteMessageReceiverGateway(String queueName) {
        super();
        this.queueName = queueName;
    }

    @Override
    protected Destination createQueue() throws JMSException {
        return session.createQueue(queueName);
    }
}
