package gui;

import configuration.ConfigurationUtils;
import db.DBManager;
import event.EventRouter;
import event.EventType;
import gui.consumers.MessageConsumer;
import gui.consumers.TimeSeriesConsumer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import web.Web;

public class App
extends Application{

	private static EventRouter router;
	private static GuiManager guiManager;
	private static MessageConsumer messageLoggingService;
	private static TimeSeriesConsumer timeSeriesConsumer;
	private static Web webInterop;
	private static ConfigurationUtils config;
	private static DBManager db;
	
	// Instantiate component instances to start worker threads.
	static {
		config = ConfigurationUtils.getInstance();
		db = DBManager.getInstance();
		router = EventRouter.getInstance();
		guiManager = GuiManager.getInstance();
		messageLoggingService = new MessageConsumer();
		timeSeriesConsumer = new TimeSeriesConsumer();
		webInterop = Web.getInstance();
	}

	public App() {}

	public static void main(String[] args) throws InterruptedException {
		router.register(EventType.MESSAGE_EVENT, messageLoggingService);
		router.register(EventType.MESSAGE_EVENT, timeSeriesConsumer);
		router.register(EventType.MESSAGE_EVENT, guiManager);
		router.register(EventType.REQUEST_TIMEOUT, guiManager);

		config.initialize();
		webInterop.loadDailyCounter();

		launch();
	}

	@Override
	public void start(Stage stage) throws Exception {
		guiManager.createAndShow(stage);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
            	router.stopBroadcasting();
            	webInterop.stopWorker();
            	messageLoggingService.stopWorker();
            	db.stopWorker();
            	
                Platform.exit();
                System.exit(0);
            }
        });
	}
}
