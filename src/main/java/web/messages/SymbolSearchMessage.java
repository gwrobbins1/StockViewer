package web.messages;

import java.util.Collections;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import event.EventSource;
import model.SymbolData;

public class SymbolSearchMessage 
implements EventSource{

	@SerializedName("bestMatches")
	List<SymbolData> bestMatches;
	
	public SymbolSearchMessage() {}

	public List<SymbolData> getBestMatches(){
		return (bestMatches != null) ? bestMatches : Collections.emptyList();
	}
}
