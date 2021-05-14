package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;

public class DeparmentListController implements Initializable {

	@FXML
	private Button btNew;
	
	@FXML
	private TableView<Department> tvDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tcId;
	
	@FXML
	private TableColumn<Department, String> tcName;
	
	@FXML
	public void onButtonNewAction() {
		System.out.println("onButtonNewAction");
	}
	
	@FXML
	public void onTableViewDepartmentAction() {
		System.out.println("onTableViewDepartmentAction");
	}
	
	@FXML
	public void onTableColumnIdAction() {
		//System.out.println("onTableColumnIdAction");
	}
	
	@FXML
	public void onTableColumnNameAction() {
		//System.out.println("onTableColumnNameAction");
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}
	
	private void initializeNodes() {
		
		tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		// Método para fazer o TableView realizar o auto ajuste na tela.
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tvDepartment.prefHeightProperty().bind(stage.heightProperty());
	}

}
