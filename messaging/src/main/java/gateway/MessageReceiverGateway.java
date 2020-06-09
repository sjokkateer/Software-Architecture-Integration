package gateway;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

/**
 * Base class serving as consumer for an application to receive messages.
 */
abstract public class MessageReceiverGateway extends GatewayBase {
    private MessageConsumer consumer;

    public MessageReceiverGateway(String queueName) {
        super(queueName);
    }

    /**
     * Provides a template method for concrete classes.
     *
     * @throws JMSException
     */
    @Override
    protected void doClassSpecificSetup() throws JMSException {
        destinationQueue = createQueue();
        consumer = session.createConsumer(destinationQueue);

        connection.start();
    }

    /**
     * Provides a generic method to assign a listener on when a message is received.
     *
     * @param messageListener
     */
    public void setListener(MessageListener messageListener) {
        try {
            consumer.setMessageListener(messageListener);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
