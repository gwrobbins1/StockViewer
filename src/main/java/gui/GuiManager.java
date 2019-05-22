package gui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import event.Event;
import event.EventConsumer;
import event.EventType;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import model.SymbolData;

import web.APIRequest;
import web.FunctionType;
import web.IntervalType;
import web.Web;
import web.messages.SymbolSearchMessage;

public final class GuiManager 
implements EventConsumer{
	private Logger logger = LoggerFactory.getLogger(GuiManager.class);
	
	private static Web webInterop;
	private boolean initialized = false;
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 600;
	private static final int GRID_PADDING = 10;
	
	// Graphical components that need to be updated so they must be class members
	private Scene scene = null;
	private Stage stage = null;
	private BorderPane pane = null;
	private HBox graphSection = null;
	private ComboBox<String> symbolBox = null;
	private Button requestButton = null;
	private Node graph = null;
	private Label stockInfoLabel = null;
	private ProgressIndicator spinner = null;
	
	private Map<String,String> nameToSymbolMap = new ConcurrentHashMap<>();
	private Map<String,String> nameToSymbolInfoMap = new ConcurrentHashMap<>();
	
	static {
		webInterop = Web.getInstance();
	}
	private GuiManager() {}
	
	private static class LazyHolder {
		public static final GuiManager INSTANCE = new GuiManager();
	}
	
	public static GuiManager getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	@SuppressWarnings("unchecked")
	public void createAndShow(Stage stage) {
		this.stage = stage;
		
		pane = new BorderPane();
		pane.setPadding(new Insets(GRID_PADDING));

        HBox top = new HBox(GRID_PADDING);
        top.setPrefWidth(WIDTH);
        
        HBox bottom = new HBox();
        bottom.setPrefWidth(WIDTH);
        
        graphSection = new HBox();
        graphSection.setPrefWidth(WIDTH);
        
        scene = new Scene(pane, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Stock Ticker");
        
        String letterOrNumberRegex = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(letterOrNumberRegex);
        
        symbolBox = new ComboBox<>();
        symbolBox.setEditable(true);
        symbolBox.setOnKeyReleased((evt)->{
        	String text = symbolBox.getEditor().getText();
        	IntervalType type = IntervalType.FIFTEEN_MIN;//not used
        	if(pattern.matcher(text).matches()) {
        		webInterop.addNewRequest(new APIRequest(FunctionType.SYMBOL_SEARCH,text,type));
        	}
        	
        	if(text.equals(""))
        		symbolBox.getItems().clear();
        });
        symbolBox.setFocusTraversable(false);

        symbolBox.focusedProperty().addListener((observable,oldValue,newValue) -> {
			if((Boolean)newValue)
				symbolBox.show();
			else
				symbolBox.hide();
        });

        symbolBox.valueProperty().addListener((observable,oldValue,newValue) -> {
			if(newValue != null && nameToSymbolInfoMap.containsKey(newValue))
				updateStockInfo(nameToSymbolInfoMap.get(newValue));
        });
        
        ComboBox<IntervalType> intervalBox = new ComboBox<>();
        for(IntervalType interval : IntervalType.values())
        	intervalBox.getItems().add(interval);
        intervalBox.setValue(IntervalType.FIVE_MIN);
        
        ComboBox<FunctionType> functionTypeBox = new ComboBox<>();
        for(FunctionType type : FunctionType.values()) 
        	functionTypeBox.getItems().add(type);
        
        functionTypeBox.setValue(FunctionType.TIME_SERIES_DAILY);
        functionTypeBox.setOnAction((evt)->{
        	FunctionType function = functionTypeBox.getSelectionModel()
        				   							.getSelectedItem();

        	if(function == FunctionType.TIME_SERIES_INTRADAY) 
        		top.getChildren().add(intervalBox);
        	else
        		if(top.getChildren().contains(intervalBox))
        			top.getChildren().remove(intervalBox);        	
        });
        
        requestButton = new Button();
        requestButton.setText("Send request");
        requestButton.setOnAction((evt)->{
        	String sym = "";
        	String symName = symbolBox.getSelectionModel().getSelectedItem();
        	
        	if(symName != null && nameToSymbolMap.containsKey(symName))
        		sym = nameToSymbolMap.get(symName);
        	
        	IntervalType interval = intervalBox.getSelectionModel().getSelectedItem();
        	FunctionType function = functionTypeBox.getSelectionModel().getSelectedItem();
        	
        	if(!sym.equals("") && interval != null && function != null) {
            	
        		requestButton.setDisable(true);
        		
        		if(spinner == null)
        			spinner = new ProgressIndicator();
        		
        		graphSection.getChildren().clear();
        		graphSection.getChildren().add(spinner);
        		
        		webInterop.addNewRequest(new APIRequest(function,sym,interval));
        	}
        });
        
        stockInfoLabel = new Label();

        top.getChildren().add(requestButton);
        top.getChildren().add(symbolBox);
        top.getChildren().add(functionTypeBox);
        bottom.getChildren().add(stockInfoLabel);
        
        pane.setTop(top);
        pane.setCenter(graphSection);
        pane.setBottom(bottom);
        
        stage.show();
        initialized = true;
	}
	
	public double getWindowWidth() {
		return stage.getWidth();
	}
	
	public double getWindowHeight() {
		return stage.getHeight();
	}
	
	public void resetGui(String errTxt) {
		Platform.runLater(()->{
			stockInfoLabel.setText(errTxt);
			stockInfoLabel.setTextFill(Paint.valueOf("red"));
			requestButton.setDisable(false);
			graphSection.getChildren().clear();			
		});
	}
	
	private void requestTimedOut() {
		resetGui("Request timed out!");
	}
	// Update methods that will update graphical components when gui manager receives an event
	private void updateStockInfo(String info) {
		Platform.runLater(() -> {
			stockInfoLabel.setText(info);
			stockInfoLabel.setTextFill(Paint.valueOf("black"));
		});
	}
	
	private void updateSymbolBox(SymbolSearchMessage message) {
		Platform.runLater(() -> {
			symbolBox.getItems().clear();
			for(SymbolData data : message.getBestMatches()) {
				String name = data.getName();
				symbolBox.getItems().add(name);
				if(!nameToSymbolMap.containsKey(name))
					nameToSymbolMap.put(name, data.getSymbol());
				if(!nameToSymbolInfoMap.containsKey(name))
					nameToSymbolInfoMap.put(name, data.toString());
			}
		});
	}
	
	public void addGraph(Node node) {
		if(!initialized) {
			logger.error("Can not add graph. GUI manager is not initialized");
			return;
		}
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				graphSection.getChildren().clear();
				requestButton.setDisable(false);
				
				graph = node;
				graphSection.getChildren().clear();
				graphSection.getChildren().add(node);
			}
		});
	}

	@Override
	public void consume(Event event) {
		EventType type = event.getType();
		switch(type) {
			case MESSAGE_EVENT:
				if(!(event.getSource() instanceof SymbolSearchMessage)) return;
					updateSymbolBox((SymbolSearchMessage)event.getSource());
				break;
			case REQUEST_TIMEOUT:
				requestTimedOut();
				break;
			default:
				logger.error("GuiManager received unknown event");
		}
	}
}
