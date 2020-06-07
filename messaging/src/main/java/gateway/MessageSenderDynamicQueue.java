package gateway;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

public class MessageSenderDynamicQueue extends GatewayBase {
    protected MessageProducer producer;

    public MessageSenderDynamicQueue(String queueName) {
        super(queueName);
    }

    @Override
    protected Destination createQueue() throws JMSException {
        return null;
    }

    @Override
    protected void doClassSpecificSetup() throws JMSException {
        destinationQueue = createQueue();
        producer = session.createProducer(destinationQueue);
    }

    public Message createTextMessage(String content) {
        Message msg = null;

        try {
            msg = session.createTextMessage(content);
        } catch (JMSException e) {
            e.printStackTrace();
        }

        return msg;
    }

    public void send(Destination returnAddress, Message msg) {
        try {
            producer.send(returnAddress, msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
