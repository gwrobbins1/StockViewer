package gui.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import event.AbstractEventConsumer;
import event.Event;

public class MessageConsumer
extends AbstractEventConsumer{

	private Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
	public MessageConsumer() {
		super();
	}

	@Override
	public void consume(Event event) {
		logger.info("Received Message:" + event.getSource().getClass().getCanonicalName());
//		System.out.println(event.getSource());
	}

}
