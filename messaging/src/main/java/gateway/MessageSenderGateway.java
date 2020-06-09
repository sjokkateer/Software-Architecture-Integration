package gateway;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

/**
 * Class is responsible for setting up a queue, creating messages and sending messages to a given queue.
 */
public class MessageSenderGateway extends GatewayBase {
    protected MessageProducer producer;

    public MessageSenderGateway(String queueName) {
        super(queueName);
    }

    /**
     * Is called in the base class to create a queue by queue name.
     *
     * Returns a destination queue.
     *
     * @return
     * @throws JMSException
     */
    @Override
    protected Destination createQueue() throws JMSException {
        return session.createQueue(queueName);
    }

    /**
     * Is called in the base class.
     *
     * Will create a producer based on the created queue.
     *
     * @throws JMSException
     */
    @Override
    protected void doClassSpecificSetup() throws JMSException {
        destinationQueue = createQueue();
        producer = session.createProducer(destinationQueue);
    }

    /**
     * Creates a text message from given content.
     *
     * @param content
     * @return
     */
    public Message createTextMessage(String content) {
        Message msg = null;

        try {
            msg = session.createTextMessage(content);
        } catch (JMSException e) {
            e.printStackTrace();
        }

        return msg;
    }

    /**
     * Simply sends off the given msg.
     *
     * @param msg
     */
    public void send(Message msg) {
        try {
            producer.send(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
