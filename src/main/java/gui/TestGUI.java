package gui;

import java.util.Scanner;

import event.EventRouter;
import event.EventType;
import gui.consumers.MessageConsumer;
import web.Web;

public class TestGUI {

	private static EventRouter router;
	private static MessageConsumer webMessageConsumer;
	private final static EventType INTERESTED_TYPE = EventType.MESSAGE_EVENT;
	
	static {
		router = EventRouter.getInstance();
		webMessageConsumer = new MessageConsumer();
	}
	public TestGUI() {}

	public static void main(String[] args) throws InterruptedException {
		router.register(INTERESTED_TYPE, webMessageConsumer);
		System.out.println("Starting stock ticker! Press enter to process test request");
		
		while(true) {//wait til i press <enter> to allow debugger to connect
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			if(scanner.nextLine() != null) break;
			Thread.sleep(5);
		}
		
		Web interop = Web.getInstance();
		interop.testDefaultRequest();
	}
}
