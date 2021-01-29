package cm.uy1.inf301.app;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import cm.uy1.inf301.app.services.datastructures.Vertex;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Controller implements Initializable {
	
	AppServices appServices;
	
	File fileInput;

	@FXML
	private Stage myStage;
	
	@FXML
	private Scene myScene;
	
	@FXML
	private Tab algorithms;
	
	@FXML
	private Tab graphs;
	
	@FXML
	private Tab settings;
	
	@FXML
	private VBox vBox1;
	
	@FXML
	private AnchorPane ap1;
	
	@FXML
	private MenuButton algoMenu;
	
	protected int algoNum = -1;
	
	@FXML
	private MenuItem algo1;
	
	@FXML
	private MenuItem algo2;
	
	@FXML
	private MenuItem algo3;
	
	@FXML
	private MenuItem algo4;
	
	@FXML
	private Label chooseDatasetFile;
	
	@FXML
	private Label mustBeValidFormat;
	
	@FXML
	private Label loading;
	
	@FXML
	private Label waitPlease;
	
	@FXML
	private Label lastLoadingInfos;
	
	@FXML
	private Label lastLoadingDate;
	
	@FXML
	private Label craftAGraph;
	
	@FXML
	private Label verticesText; 
	
	@FXML
	private Button popDatabaseChooser;
	
	@FXML
	private Button popOutputFileChooser;
	
	@FXML
	private Button showGraphCrafter;
	
	@FXML
	private Button runAlgorithm;
	
	@FXML
	private Button parent;
	
	@FXML
	private Button children;
	
	@FXML
	private Button runLOUDS;
	
	@FXML
	private Button runGLOUDS;
	
	@FXML
	private Button resetGraph;
	
	@FXML
	private TextField datasetPath;
	
	@FXML
	private TextField supportInput;
	
	@FXML
	private TextField outputPath;
	
	@FXML
	private TextField numberOfVerticesInput;
	
	@FXML
	private ChoiceBox<Vertex> vertexEncodingRoot;
	
	@FXML
	private ChoiceBox<Vertex> vertexToComputeParent;
	
	@FXML
	private ChoiceBox<Vertex> vertexToComputeChildren;
	
	private int numberOfVertices = -1;
	
	private boolean mustPrintResults;
	
	private File outputFile;
	
	protected double support_min;
	
	@FXML
	private TextArea consoleLog1;
	
	@FXML
	private TextArea consoleLog2;
	
	private int logArea = 0;
	
	@FXML
	private Button fetchData;
	
	@FXML
	private Button clearDataset;
	
	@FXML
	private ProgressBar progressBar;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		System.out.println("\n\nInitialization...");
		
		boolean doFilesExist = AppServices.checkFiles();
		
		if(doFilesExist) {
			this.appServices = new AppServices();
			System.out.println("\n\nLoading items and transactions...");
			this.loadDatasetInfos();
		}
		else	
			hide();
	}
	
	protected void loadDatasetInfos() {
		
		Task<Void> task = new Task<Void>() {
			
			@Override
			protected Void call() throws Exception {
				Platform.runLater(() -> {
					String content, content_;
					content = "Last found " + AppServices.readItemsSize() + " items and " + AppServices.readTransactionsSize() + " transactions on the";
					System.out.println("\n\nDone !");
					content_ = (new Date(AppServices.lastDatasetLoadingDate())).toString();
					lastLoadingInfos.setText(content);
					lastLoadingDate.setText(content_);
				});
				
				return null;
			}
		};
		
		Thread t = new Thread(task);
		t.setDaemon(true);
		t.start();
	}
	
	protected void hide() {
		this.algorithms.setDisable(true);
		this.graphs.setDisable(true);
		
		this.lastLoadingInfos.setVisible(false);
		this.lastLoadingDate.setVisible(false);
		this.clearDataset.setVisible(false);
		
		this.chooseDatasetFile.setVisible(true);
		this.mustBeValidFormat.setVisible(true);
		this.popDatabaseChooser.setVisible(true);
		this.datasetPath.setVisible(true);
		this.fetchData.setVisible(true);
	}
	
	protected void splashMainScreen() {
		this.progressBar.setVisible(false);
		this.waitPlease.setVisible(false);
		this.loading.setVisible(false);
		
		this.lastLoadingInfos.setVisible(true);
		this.lastLoadingDate.setVisible(true);
		this.clearDataset.setVisible(true);
		this.loadDatasetInfos();
		
		this.algorithms.setDisable(false);
		this.graphs.setDisable(false);
	}
	
	protected void runLoader() {
		
		this.chooseDatasetFile.setVisible(false);
		this.mustBeValidFormat.setVisible(false);
		this.popDatabaseChooser.setVisible(false);
		this.datasetPath.setVisible(false);
		this.fetchData.setVisible(false);
		
		this.progressBar.setVisible(true);
		this.waitPlease.setVisible(true);
		this.loading.setVisible(true);
	}
	
	public void errorPopup(String message) {
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error Dialog");
		alert.setHeaderText("We encoutered an issue.");
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	@FXML
	public void datasetChooser(final ActionEvent e) {
		
		FileChooser c = new FileChooser();
		c.setTitle("Select dataset file");
		c.getExtensionFilters().add(new ExtensionFilter("Spreadsheet new format (*.xlsx)","*.xlsx"));
		File f = c.showOpenDialog(myStage);
		
		if(f != null) {
			if(AppServices.checkExtension(f, ".xlsx")) {
				this.datasetPath.setText(f.getAbsolutePath());
				this.fileInput = new File(f.getAbsolutePath());
			}
			else
				errorPopup("Choose a valid xlsx file !");
		}
	}
	
	public void outputDirChooser(final ActionEvent e) {
		
		DirectoryChooser c = new DirectoryChooser();
		File f = c.showDialog(this.myStage);
		if(f != null) {
			this.outputPath.setText(f.getAbsolutePath()+ File.separatorChar + "output.txt");
			this.mustPrintResults = true;
			this.outputFile = new File(f, "output.txt");
		}
			
	}
	
	@FXML
	public void fetchData(final ActionEvent e) {
		
		if(this.fileInput != null) {
			
			this.runLoader();
			
			Task<Boolean> task = new Task<Boolean>() {
				boolean status = false;
				@Override
				protected Boolean call() {
					status = AppServices.saveItemsAndTransactions(fileInput);
					if(status)
						Platform.runLater(() -> {
							splashMainScreen();
							appServices = new AppServices();
						});
					return status;
				}
			};
			
			Thread t = new Thread(task);
			t.setDaemon(true);
			t.start();
			
		}
		else
			errorPopup("Select a dataset file !");
	}
	
	@FXML
	public void clearData(final ActionEvent e) {
		
		AppServices.clearItemsAndTransactions();
		this.myStage.close();
	}
	
	@FXML
	public void changeAlgoMenuLabel(final ActionEvent e) {
		MenuItem selectedMenu = ((MenuItem)e.getSource());
		this.algoMenu.setText(selectedMenu.getText());
		this.algoNum = this.algoMenu.getItems().indexOf(selectedMenu);
	}
	
	protected void getSupport() {
		
		String input = this.supportInput.getText();
		try {
			this.support_min = Double.valueOf(input).doubleValue();
		}
		catch(NumberFormatException e) {
			errorPopup("Bad or no support input.");
			this.support_min = -1;
		}
	}
	
	protected boolean checkSupport() {
		
		return this.support_min > 0 && this.support_min <= 100;
	}
	
	protected void log(final String message) {
		
		if(this.logArea == 0)
			this.consoleLog1.appendText(message);
		else
			this.consoleLog2.appendText(message);
	}
	
	protected void log(final String message, final boolean brutal) {
		
		if(brutal)
			if(this.logArea == 0)
				this.consoleLog1.setText(message);
			else
				this.consoleLog2.setText(message);
		else
			this.log(message);
	}
		
	protected void runApriori() {
		
		Task<Void> task = new Task<Void>() {
			
			@Override
			protected Void call() throws Exception {
				Platform.runLater(() -> {
					consoleLog1.clear();
					runAlgorithm.setDisable(true);
					clearDataset.setDisable(true);
					log("\nReading items and transactions...\n\n");
					log("Running APriori (Classic version) with a support of "
						+ support_min + "%...\n");
				});
				
				appServices.runAPriori(support_min);
				
				Platform.runLater(() -> {
					runAlgorithm.setDisable(false);
					clearDataset.setDisable(false);
					try {
						appServices.saveResults();
						log(appServices.results());
						consoleLog1.positionCaret(0);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				});
				
				return null;
			}
		};
		
		Thread t = new Thread(task);
		t.setDaemon(true);
		t.start();
	}
		
	protected void runAprioriTID() {
		
		Task<Void> task = new Task<Void>() {
			
			@Override
			protected Void call() throws Exception {
				Platform.runLater(() -> {
					consoleLog1.clear();
					runAlgorithm.setDisable(true);
					clearDataset.setDisable(true);
					log("\nReading items and transactions...\n\n");
					log("Running APriori TID (Counting using set of possibly large transaction candidates) with a support of "
						+ support_min + "%...\n");
				});
				
				appServices.runAPrioriTID(support_min);
				
				Platform.runLater(() -> {
					runAlgorithm.setDisable(false);
					clearDataset.setDisable(false);
					try {
						appServices.saveResults();
						log(appServices.results());
						consoleLog1.positionCaret(0);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				});
				
				return null;
			}
		};
		
		Thread t = new Thread(task);
		t.setDaemon(true);
		t.start();
	}
	
	protected void runAprioriAIS() {
		
		Task<Void> task = new Task<Void>() {
			
			@Override
			protected Void call() throws Exception {
				Platform.runLater(() -> {
					consoleLog1.clear();
					runAlgorithm.setDisable(true);
					clearDataset.setDisable(true);
					log("\nReading items and transactions...\n\n");
					log("Running APriori AIS (Candidates generated and counted as database is scanned) with a support of "
						+ support_min + "%...\n");
				});
				
				appServices.runAPrioriAIS(support_min);
				
				Platform.runLater(() -> {
					runAlgorithm.setDisable(false);
					clearDataset.setDisable(false);
					try {
						appServices.saveResults();
						log(appServices.results());
						consoleLog1.positionCaret(0);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				});
				
				return null;
			}
		};
		
		Thread t = new Thread(task);
		t.setDaemon(true);
		t.start();
	}
	
	protected void runAprioriSETM() {
		
		Task<Void> task = new Task<Void>() {
			
			@Override
			protected Void call() throws Exception {
				Platform.runLater(() -> {
					consoleLog1.clear();
					runAlgorithm.setDisable(true);
					clearDataset.setDisable(true);
					log("\nReading items and transactions...\n\n");
					log("Running APriori SETM (For query languages like SQL) with a support of "
						+ support_min + "%...\n");
				});
				
				appServices.runAPrioriSETM(support_min);
				
				Platform.runLater(() -> {
					runAlgorithm.setDisable(false);
					clearDataset.setDisable(false);
					try {
						appServices.saveResults();
						log(appServices.results());
						consoleLog1.positionCaret(0);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				});
				
				return null;
			}
		};
		
		Thread t = new Thread(task);
		t.setDaemon(true);
		t.start();
	}
	
	@FXML
	public void runAlgorithm() {
		
		this.logArea = 0;
		
		if(this.algoNum > -1) {
			
			this.getSupport();
			
			if(this.checkSupport()) {
				
				switch(this.algoNum) {
				
				case 0:
					this.runApriori();
					break;	
					
				case 1:
					this.runAprioriTID();
					break;
				
				case 2:
					this.runAprioriAIS();
					break;
				
				case 3:
					this.runAprioriSETM();
					break;
				}
				
				if(this.mustPrintResults) {
					try {
						this.appServices.printResults(this.outputFile);
					}
					catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				}
			}
			else
				this.log("Invalid support entry.", true);
		}
		else
			this.log("Select an algorithm !", true);
	}
	
	protected void getNumberOfVertices() {
		
		String input = this.numberOfVerticesInput.getText();
		try {
			this.numberOfVertices = Integer.valueOf(input).intValue();
			if(this.numberOfVertices <= 0) throw new NumberFormatException();
		}
		catch(NumberFormatException e) {
			errorPopup("Invalid number of vertices.");
			this.numberOfVertices = -1;
		}
	}
	
	protected boolean checkVerticesLabels() {
		
		boolean success = true;
		
		for(int i = 0; i<numberOfVertices; i++)
			if(((TextField)this.vBox1.getChildren().get(i)).getText().isBlank()) {
				success = false;
				break;
			}
		
		return success;
	}
	
	protected void showAdjacencyAccordion() {
		
		Accordion a = new Accordion();
		Object[] keySet = this.appServices.getGraph().getAdjacency().keySet().toArray();
		for(int i = 0; i<this.numberOfVertices; i++) {
			int currentIndex = i;
			TitledPane pane = new TitledPane(((Vertex)keySet[i]).getLabel(), new VBox());
			for(Object vertex: keySet) {
				CheckBox box = new CheckBox(((Vertex)vertex).getLabel());
				box.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						if(box.isSelected())
							appServices.addVertexToAdjacency((Vertex)keySet[currentIndex], (Vertex)vertex);
						else
							appServices.removeVertexToAdjacency((Vertex)keySet[currentIndex], (Vertex)vertex);
					}
				});
				((VBox)pane.getContent()).getChildren().add(box);
			}
			a.getPanes().add(pane);
		}
		this.vBox1.getChildren().add(a);
		Button proceed = new Button("Procced");
		proceed.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				vBox1.getChildren().clear();
				vBox1.setDisable(true);
				ap1.setVisible(true);
				loadVerticesInChoiceBoxes();
			}
		});
		this.vBox1.getChildren().add(proceed);
	}
	
	protected void fetchVerticesLabels() {
		
		if(checkVerticesLabels()) {
			String[] verticesLabels = new String[numberOfVertices];
			for(int i = 0; i<this.numberOfVertices; i++)
				verticesLabels[i] = ((TextField)vBox1.getChildren().get(i)).getText();
			this.appServices.makeGraph(verticesLabels);
			this.vBox1.getChildren().clear();
			this.showAdjacencyAccordion();
		}
		else
			errorPopup("Some labels are still empty");
	}
	
	@FXML
	public void onShowGraphCrafter(final ActionEvent e) {
		
		this.getNumberOfVertices();
		
		if(this.numberOfVertices > 0) {
			for(int i = 1; i<=this.numberOfVertices; ++i) {
				
				TextField vertexLabel = new TextField();
				vertexLabel.setPromptText("Enter label " + String.valueOf(i));
				this.vBox1.getChildren().add(vertexLabel);
			}
			
			this.showGraphCrafter.setVisible(false);
			this.numberOfVerticesInput.setVisible(false);
			this.craftAGraph.setVisible(false);
			this.verticesText.setVisible(false);
			this.resetGraph.setVisible(true);
			this.vBox1.setVisible(true);
			this.vBox1.setDisable(false);
			
			Button validate = new Button("Create vertices");
			validate.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					fetchVerticesLabels();
				}
			});
			this.vBox1.getChildren().add(validate);
		}
	}
	
	protected void clearVerticesInChoiceBoxes() {
		
		this.vertexEncodingRoot.getItems().clear();
		this.vertexToComputeChildren.getItems().clear();
		this.vertexToComputeParent.getItems().clear();
	}
	
	protected void loadVerticesInChoiceBoxes() {
		
		this.clearVerticesInChoiceBoxes();
		this.vertexEncodingRoot.getItems().addAll(this.appServices.getGraph().getAdjacency().keySet());
		this.vertexToComputeChildren.getItems().addAll(this.appServices.getGraph().getAdjacency().keySet());
		this.vertexToComputeParent.getItems().addAll(this.appServices.getGraph().getAdjacency().keySet());
	}
	
	protected boolean isVertexSelected(ChoiceBox<Vertex> box) {
		if(box.getValue() == null) {
			errorPopup("Select a vertex first");
			return false;
		}
		return true;
	}
	
	@FXML
	public void runLOUDSEncoding(final ActionEvent e) {
		
		this.logArea = 1;
		
		if(this.isVertexSelected(vertexEncodingRoot)) {
			String LOUDSCode = this.appServices.runEncoding(this.vertexEncodingRoot.getValue());
			this.log("LOUDS code: " + LOUDSCode, true);
			this.loadVerticesInChoiceBoxes();
			this.runLOUDS.setDisable(true);
			this.runGLOUDS.setDisable(true);
			this.parent.setDisable(false);
			this.children.setDisable(false);
		}
		
	}
	
	@FXML 
	public void runGLOUDSEncoding(final ActionEvent e) {
		
		this.logArea = 1;
		
		if(this.isVertexSelected(vertexEncodingRoot)) {
			String GLOUDSCode = this.appServices.runEncoding(this.vertexEncodingRoot.getValue());
			this.log("GLOUDS code: " + GLOUDSCode, true);
			this.loadVerticesInChoiceBoxes();
			this.runLOUDS.setDisable(true);
			this.runGLOUDS.setDisable(true);
			this.parent.setDisable(false);
			this.children.setDisable(false);
		}
	}
	
	@FXML
	public void resetGraphsAndTrees(final ActionEvent e) {
		
		this.craftAGraph.setVisible(true);
		this.numberOfVerticesInput.setVisible(true);
		this.verticesText.setVisible(true);
		this.showGraphCrafter.setVisible(true);
		
		this.ap1.setVisible(false);
		this.vBox1.setVisible(false);
		this.resetGraph.setVisible(false);
		
		this.parent.setDisable(true);
		this.children.setDisable(true);
		
		this.appServices.resetGraph();
		this.vBox1.getChildren().clear();
		this.numberOfVertices = -1;
		this.consoleLog2.clear();
		this.clearVerticesInChoiceBoxes();
	}
	
	@FXML
	public void parent(final ActionEvent e) {
		
		this.logArea = 1;
		
		if(this.isVertexSelected(vertexToComputeParent)) {
			Vertex parent = this.appServices.parent(this.vertexToComputeParent.getValue());
			String result = parent != null ? parent.toString() : "ORIGIN";
			this.log("\n\nParent" + this.vertexToComputeParent.getValue().toString() + " = "  + result + ".");
		}
	}
	
	@FXML
	public void children(final ActionEvent e) {
		
		this.logArea = 1;
		
		if(this.isVertexSelected(this.vertexToComputeChildren)) {
			ArrayList<Vertex> children = this.appServices.children(this.vertexToComputeChildren.getValue());
			this.log("\n\nChildren" + this.vertexToComputeChildren.getValue().toString() + " = " + children.toString() + ".");
		}
	}
}
