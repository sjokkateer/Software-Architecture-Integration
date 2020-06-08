package approval.gui;

import approval.ApprovalRequestListener;
import approval.TravelApprovalAppGateway;
import approval.model.ApprovalReply;
import approval.model.ApprovalRequest;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ResourceBundle;

public class ApprovalController implements Initializable {

    @FXML
    private ListView<ApprovalListLine> lvRequestReply;
    @FXML
    private RadioButton rbApprove;
    @FXML
    private RadioButton rbReject;
    @FXML
    private Button btnSendReply;

    private final String approvalName;

    private TravelApprovalAppGateway travelApprovalAppGateway;

    public ApprovalController(String approvalAppName, String approvalRequestQueue) {
        this.approvalName = approvalAppName;
        travelApprovalAppGateway = new TravelApprovalAppGateway(approvalRequestQueue);

        // Inject piece of code instructing what to do when we receive an approval request.
        travelApprovalAppGateway.setApprovalRequestListener(new ApprovalRequestListener() {
            @Override
            public void onRequestReceived(ApprovalRequest approvalRequest) {
                ApprovalListLine rr = new ApprovalListLine(approvalRequest);
                lvRequestReply.getItems().add(rr);
                lvRequestReply.refresh();
            }
        });
    }

    private void sendApprovalReply() {
        ApprovalListLine rr = lvRequestReply.getSelectionModel().getSelectedItem();
        boolean approved = rbApprove.isSelected();
        String name = "";
        if (!approved){
            name = approvalName;
        }

        ApprovalReply reply = new ApprovalReply(approved, name);
        if (rr!= null){
            rr.setReply(reply);
            lvRequestReply.refresh();
            ApprovalRequest request = rr.getRequest();

            // send reply for the selected request
            travelApprovalAppGateway.sendApprovalReply(reply, request.getId());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup radioButtonsGroup = new ToggleGroup();
        rbApprove.setToggleGroup(radioButtonsGroup);
        rbReject.setToggleGroup(radioButtonsGroup);
        btnSendReply.setOnAction(event -> sendApprovalReply());
    }
}
