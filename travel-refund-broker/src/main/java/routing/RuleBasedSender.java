package routing;

import gateway.MessageSenderGateway;
import org.mariuszgromada.math.mxparser.*;
import javax.jms.Message;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class is responsible for evaluating rules related to the queue
 * and sending a message accordingly.
 */
public class RuleBasedSender {
    private static final String RESOURCE_FOLDER = "/travel-refund-broker/src/main/resources";
    private static final String RULE_FILE = "rules.txt";

    private static final int RULE = 0;
    private static final int QUEUE_NAME = 1;

    private MessageSenderGateway gateway;
    private String rule;

    public RuleBasedSender(MessageSenderGateway gateway, String rule) {
        this.gateway = gateway;
        this.rule = rule;
    }

    /**
     * Will evaluate the objects corresponding rule,
     * if the rule evaluates to true the message is send and true is returned.
     * False otherwise.
     *
     * @param costs
     * @param message
     * @return
     */
    public boolean send(double costs, Message message) {
        String argument = " costs = " + costs;

        Expression expression = new Expression(rule, new Argument(argument));
        double result = expression.calculate();

        if (result == 1.0) {
            gateway.send(message);
            return true;
        }

        return false;
    }

    public Message createTextMessage(String content) {
        return gateway.createTextMessage(content);
    }

    /**
     * Method responsible for loading the text file in the resource folder.
     * This text file contains rules and related queue names.
     *
     * Method will create a corresponding object for each line and add them to a collection.
     *
     * Ultimately returning a collection of rule based senders.
     *
     * @return
     */
    public static List<RuleBasedSender> loadFromFile() {
        String pathToFiles = System.getProperty("user.dir");
        List<RuleBasedSender> ruleBasedSenders = new ArrayList<>();

        if((new File(pathToFiles + RESOURCE_FOLDER)).exists()) {
            pathToFiles += RESOURCE_FOLDER;

            MessageSenderGateway messageSenderGateway;
            String rule;

            try {
                File inputFile = new File(pathToFiles + "/" + RULE_FILE);
                Scanner scanner = new Scanner(inputFile);

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] items = line.split(",");

                    rule = items[RULE];
                    messageSenderGateway = new MessageSenderGateway(items[QUEUE_NAME]);

                    ruleBasedSenders.add(new RuleBasedSender(messageSenderGateway, rule));
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return ruleBasedSenders;
    }
}
