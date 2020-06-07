package gateway;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

public class MessageSenderGateway extends GatewayBase {
    protected MessageProducer producer;

    public MessageSenderGateway(String queueName) {
        super(queueName);
    }

    @Override
    protected void doClassSpecificSetup() throws JMSException {
        destinationQueue = session.createQueue(queueName);
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

    public void send(Message msg) {
        try {
            producer.send(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
