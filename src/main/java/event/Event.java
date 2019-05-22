package event;

public interface Event {
	EventType getType();
	EventSource getSource();
}
