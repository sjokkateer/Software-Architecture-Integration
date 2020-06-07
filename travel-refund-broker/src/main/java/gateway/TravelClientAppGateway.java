package gateway;

import client.model.TravelRefundRequest;
import com.google.gson.Gson;

import javax.jms.*;
import java.util.HashMap;

public class TravelClientAppGateway {
    private Gson gson = new Gson();

    private ConcreteMessageReceiverGateway messageReceiverGatewayClient;
    private MessageSenderGateway messageSenderGatewayClient;

    private TravelRequestListener travelRequestListener;

    // So why not store the destination queue here by message id?
    HashMap<String, Destination> messageIdReturnAddress;

    public TravelClientAppGateway() {
        messageIdReturnAddress = new HashMap<>();

        messageReceiverGatewayClient = new ConcreteMessageReceiverGateway("travel-refund-broker-request");
        messageReceiverGatewayClient.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    String messageContent = ((TextMessage) message).getText();
                    TravelRefundRequest travelRefundRequest = gson.fromJson(messageContent, TravelRefundRequest.class);

                    messageIdReturnAddress.put(message.getJMSMessageID(), message.getJMSReplyTo());
                    System.out.println(travelRefundRequest);

                    if (travelRequestListener != null) {
                        travelRequestListener.onRequestReceived(travelRefundRequest, message.getJMSMessageID());
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

        // messageSenderGatewayClient = new MessageSenderGateway("travel-refund-broker-request");
    }

    public void setTravelRequestListener(TravelRequestListener travelRequestListener) {
        this.travelRequestListener = travelRequestListener;
    }

//    public void sendLoanReply(String originalLoanRequestId, BankInterestReply interestReply) {
//        try {
//            LoanReply loanReply = new LoanReply(interestReply.getId(), interestReply.getInterest(), interestReply.getBankId());
//            String lrJSON = gson.toJson(loanReply);
//            Message message = messageSenderGatewayClient.createTextMessage(lrJSON);
//            message.setJMSCorrelationID(originalLoanRequestId);
//            messageSenderGatewayClient.send(message);
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
//    }
}
