package client.gui;

import client.TravelClientAppGateway;
import client.TravelRefundReplyListener;
import client.model.Address;
import client.model.ClientTravelMode;
import client.model.TravelRefundReply;
import client.model.TravelRefundRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ApprovalClientController implements Initializable {
    @FXML
    private ComboBox<String> cbTravelMode;
    @FXML
    private TextField tfOriginStreet;
    @FXML
    private TextField tfOriginNumber;
    @FXML
    private TextField tfOriginCity;
    @FXML
    private TextField tfTeacher;
    @FXML
    private TextField tfDestinationStreet;
    @FXML
    private TextField tfDestinationNumber;
    @FXML
    private TextField tfDestinationCity;
    @FXML
    private TextField tfStudent;
    @FXML
    private TextField tfCosts;
    @FXML
    private Label lbCosts;
    @FXML
    private ListView<ClientListLine> lvRequestReply;

    private TravelClientAppGateway travelClientAppGateway;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       cbTravelMode.getItems().addAll(
                "car",
                "public transport"
        );
        cbTravelMode.getSelectionModel().select(0);
        jcbModeItemStateChanged();

        travelClientAppGateway = new TravelClientAppGateway();
        travelClientAppGateway.setReplyListener(new TravelRefundReplyListener() {
            @Override
            public void onReplyReceived(TravelRefundReply travelRefundReply, TravelRefundRequest travelRefundRequest) {
                ClientListLine rr = getRequestReply(travelRefundRequest);

                if (rr != null) {
                    rr.setReply(travelRefundReply);
                    lvRequestReply.refresh();
                }
            }
        });
    }

    private ClientListLine getRequestReply(TravelRefundRequest request) {

        for (int i = 0; i < lvRequestReply.getItems().size(); i++) {
            ClientListLine rr = lvRequestReply.getItems().get(i);
            if (rr.getRequest() == request) {
                return rr;
            }
        }

        return null;
    }

    @FXML
    private void jcbModeItemStateChanged() {
        int mode = cbTravelMode.getSelectionModel().getSelectedIndex();
        String label;
        if (mode == ClientTravelMode.PUBLIC_TRANSPORT.ordinal()){
            label = "ticket costs";
        } else {
            label = "kilometers";
        }
        lbCosts.setText(label);
    }

    @FXML
    public void btnSendClick(ActionEvent actionEvent) {
        // get data from the frame
        String street = tfOriginStreet.getText();
        int number = Integer.parseInt(tfOriginNumber.getText());
        String city = tfOriginCity.getText();
        Address originAddress = new Address(street, number, city);

        street = tfDestinationStreet.getText();
        number = Integer.parseInt(tfDestinationNumber.getText());
        city = tfDestinationCity.getText();
        Address destinationAddress = new Address(street, number, city);

        String teacher = tfTeacher.getText();
        String student = tfStudent.getText();

        TravelRefundRequest request;
        int mode = cbTravelMode.getSelectionModel().getSelectedIndex();
        if (mode == ClientTravelMode.PUBLIC_TRANSPORT.ordinal()){
            double costs = Double.parseDouble(tfCosts.getText());
            request = new TravelRefundRequest( originAddress, destinationAddress,teacher, student, costs);
        } else {
            int kilometers = Integer.parseInt(tfCosts.getText());
            request = new TravelRefundRequest( originAddress, destinationAddress,teacher, student, kilometers);
        }
        // add the TravelRefundRequest to lvRequestReply
        lvRequestReply.getItems().add(new ClientListLine(request, null));

        //@TODO: send the TravelRefundRequest
        travelClientAppGateway.applyForRefund(request);
    }
}
