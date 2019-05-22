package gui.event;

import event.Event;
import event.EventSource;
import event.EventType;

public class RequestEvent
implements Event{

	public RequestEvent() {}

	@Override
	public EventType getType() {
		return EventType.GUI_REQUEST_EVENT;
	}

	@Override
	public EventSource getSource() {
		return new EventSource() {
			
		};
	}

}
