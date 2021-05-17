package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	private SellerService service;

	@FXML
	private Button btNew;

	@FXML
	private TableView<Seller> tvSeller;

	@FXML
	private TableColumn<Seller, Integer> tcId;

	@FXML
	private TableColumn<Seller, String> tcName;
	
	@FXML
	private TableColumn<Seller, Seller> tcEmail;
	
	@FXML
	private TableColumn<Seller, Date> tcBirthDate;
	
	@FXML
	private TableColumn<Seller, Double> tcBaseSalary;
	
	@FXML
	private TableColumn<Seller, String> tcDepartment;

	@FXML
	private TableColumn<Seller, Seller> tcEDIT;

	@FXML
	private TableColumn<Seller, Seller> tcREMOVE;

	private ObservableList<Seller> obsList;

	public void setSellerService(SellerService service) {
		this.service = service;
	}

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Seller obj = new Seller();
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}

	@FXML
	public void onTableViewSellerAction() {
		System.out.println("onTableViewSellerAction");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {

		tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tcEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tcBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		tcBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		tcDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
		Utils.formatTableColumnDate(tcBirthDate, "dd/MM/yyyy");
		Utils.formatTableColumnDouble(tcBaseSalary, 2);
		
		// Método para fazer o TableView realizar o auto ajuste na tela.
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tvSeller.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		List<Seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tvSeller.setItems(obsList);
		initEditButtons();
		initRemoveButtons();

	}

	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			SellerFormController controller = loader.getController();
			controller.setSeller(obj);
			controller.setServices(new SellerService(), new DepartmentService());
			controller.loadAssociatedObjects();
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Seller data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			// Modality impede que janelas anteriores sejam
			// acessadas antes que a atual seja fechada.
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		}

		catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {

		tcEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tcEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("Edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tcREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tcREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("Remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Seller obj) {

		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");

		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
			
				service.remove(obj);
				updateTableView();
			}
			
			catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
