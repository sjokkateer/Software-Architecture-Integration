package gateway;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Class responsible for all generic set up for the project,
 * as well as providing some template methods for concrete classes to implement.
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

    /**
     * Should be implemented by a concrete class, since the type of queue and way of creating a queue can differ,
     * not only for producer and consumer but also when trying to apply a more dynamic way of creating queues.
     *
     * @return
     * @throws JMSException
     */
    protected abstract Destination createQueue() throws JMSException;

    /**
     * Should be implemented by a concrete class, instructing the class on creating a queue to be set
     * as destination queue, as well as creating a consumer or producer and whether a connection should be started.
     *
     * @throws JMSException
     */
    protected abstract void doClassSpecificSetup() throws JMSException;

    /**
     * Generic method that all classes can use to safely close the connection to the messaging service.
     */
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
