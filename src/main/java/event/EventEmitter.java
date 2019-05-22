package event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EventEmitter {

	private EventRouter router = EventRouter.getInstance();
	Logger logger = LoggerFactory.getLogger(EventEmitter.class);
	public EventEmitter() {	}

	public void emit(Event evt) {
		logger.info("Emitting event:\n"+evt);
		router.addBroadcast(evt);
	}
}
