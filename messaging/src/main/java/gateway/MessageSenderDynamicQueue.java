package gateway;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

/**
 * Class is responsible for setting up a temporary queue, creating messages and sending messages to a given queue.
 */
public class MessageSenderDynamicQueue extends GatewayBase {
    protected MessageProducer producer;

    public MessageSenderDynamicQueue(String queueName) {
        super(queueName);
    }

    /**
     * Returns null that will be used as argument for the destination queue,
     * allowing it to use a temporary queue.
     *
     * @return
     * @throws JMSException
     */
    @Override
    protected Destination createQueue() throws JMSException {
        return null;
    }

    /**
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
    public void send(Destination returnAddress, Message msg) {
        try {
            producer.send(returnAddress, msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
