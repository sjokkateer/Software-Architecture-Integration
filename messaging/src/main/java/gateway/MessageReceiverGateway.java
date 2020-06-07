package gateway;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

abstract public class MessageReceiverGateway extends GatewayBase {
    private MessageConsumer consumer;

    public MessageReceiverGateway(String queueName) {
        super(queueName);
    }

    @Override
    protected void doClassSpecificSetup() throws JMSException {
        destinationQueue = createQueue();
        consumer = session.createConsumer(destinationQueue);

        connection.start();
    }

    public void setListener(MessageListener messageListener) {
        try {
            consumer.setMessageListener(messageListener);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
