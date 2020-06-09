package gui;

import approval.model.ApprovalReply;
import client.model.TravelRefundRequest;
import gateway.ApprovalReplyListener;
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

        // Adds a listener to controller with instructions on what code to execute
        // when receiving a request.
        controller.setTravelRequestListener(new TravelRequestListener() {
            @Override
            public void onRequestReceived(TravelRefundRequest travelRefundRequest, String originalMessageId) {
                updateRefundRequestReply(travelRefundRequest, originalMessageId);
            }
        });

        // Adds a listener to controller with instructions on what code to execute
        // when receiving a reply.
        controller.setApprovalReplyListener(new ApprovalReplyListener() {
            @Override
            public void onReplyReceived(ApprovalReply approvalReply, String correlationId) {
                updateRefundRequestReply(approvalReply, correlationId);
            }
        });
    }

    /**
     * Updates a refund request reply object with the obtained travel refund request object.
     *
     * @param travelRefundRequest
     * @param originalMessageId
     */
    public void updateRefundRequestReply(TravelRefundRequest travelRefundRequest, String originalMessageId) {
        RefundRequestReply refundRequestReply = new RefundRequestReply(travelRefundRequest);
        cache.put(originalMessageId, refundRequestReply);
        refundRequestReplyListModel.addElement(refundRequestReply);
    }

    /**
     * Updates a refund request reply object with the obtained travel refund request object.
     *
     * @param approvalReply
     * @param correlationId
     */
    public void updateRefundRequestReply(ApprovalReply approvalReply, String correlationId) {
        RefundRequestReply refundRequestReply = cache.get(correlationId);
        refundRequestReply.setApprovalReply(approvalReply);
        // Updates the GUI after receiving a reply.
        applicationPanel.repaint();
        applicationPanel.revalidate();
    }

    /**
     * Sets up the application GUI with a specific title, size and location.
     *
     * @param args
     */
    public static void main(String[] args) {
        TravelRefundBrokerApp travelRefundBrokerApp = new TravelRefundBrokerApp("Travel Refund Broker Application");
        travelRefundBrokerApp.setSize(travelRefundBrokerApp.WIDTH, travelRefundBrokerApp.HEIGHT);
        travelRefundBrokerApp.setLocationRelativeTo(null);
        travelRefundBrokerApp.setVisible(true);
    }
}
