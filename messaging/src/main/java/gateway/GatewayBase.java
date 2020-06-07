package gateway;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/*
    The controllers would extend the gateway base to handle sending and receiving
 */
abstract public class GatewayBase {
    private static final String BROKER_URL = "tcp://localhost:61616";

    protected Connection connection;
    protected Session session;
    protected Destination destinationQueue;
    protected String queueName;

    public GatewayBase(String queueName) {
        this.queueName = queueName;
        ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);

        try {
            connection = factory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            doClassSpecificSetup();
        } catch (JMSException e) {
            e.printStackTrace();
            closeConnections();
        }
    }

    protected abstract void doClassSpecificSetup() throws JMSException;

    public void closeConnections() {
        try {
            if (session != null) {
                session.close();
            }

            if (connection != null) {
                connection.close();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
