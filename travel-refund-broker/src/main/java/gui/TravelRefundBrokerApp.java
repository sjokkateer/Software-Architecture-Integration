package gui;

import client.model.TravelRefundRequest;
import gateway.TravelRequestListener;

import javax.swing.*;
import java.util.HashMap;

public class TravelRefundBrokerApp extends JFrame {
    private JPanel applicationPanel;
    private JList travelRefundReplyList;

    private static int WIDTH = 800;
    private static int HEIGHT = 300;

    private DefaultListModel<RefundRequestReply> refundRequestReplyListModel;

    private TravelRefundBrokerController controller;
    private HashMap<String, RefundRequestReply> cache;

    public TravelRefundBrokerApp(String title) {
        super(title);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(applicationPanel);
        pack();

        cache = new HashMap<>();

        refundRequestReplyListModel = new DefaultListModel<>();
        travelRefundReplyList.setModel(refundRequestReplyListModel);

        controller = new TravelRefundBrokerController();

        controller.setTravelRequestListener(new TravelRequestListener() {
            @Override
            public void onRequestReceived(TravelRefundRequest travelRefundRequest, String originalMessageId) {
                updateRefundRequest(travelRefundRequest, originalMessageId);
            }
        });

//        controller.setBankInterestReplyListener(new InterestReplyListener() {
//            @Override
//            public void onInterestReplyReceived(BankInterestReply interestReply, String correlationId) {
//                updateRequestOffer(interestReply, correlationId);
//            }
//        });
    }

    public void updateRefundRequest(TravelRefundRequest travelRefundRequest, String originalMessageId) {
        RefundRequestReply refundRequestReply = new RefundRequestReply(travelRefundRequest);
        cache.put(originalMessageId, refundRequestReply);
        refundRequestReplyListModel.addElement(refundRequestReply);
    }

//    public void updateRequestOffer(BankInterestReply interestReply, String correlationId) {
//        RequestOffer requestOffer = cache.get(correlationId);
//        requestOffer.setBankInterestReply(interestReply);
//        // GUI should be refreshed?
//    }

    public static void main(String[] args) {
        TravelRefundBrokerApp travelRefundBrokerApp = new TravelRefundBrokerApp("Travel Refund Broker Application");
        travelRefundBrokerApp.setSize(travelRefundBrokerApp.WIDTH, travelRefundBrokerApp.HEIGHT);
        travelRefundBrokerApp.setLocationRelativeTo(null);
        travelRefundBrokerApp.setVisible(true);
    }
}
