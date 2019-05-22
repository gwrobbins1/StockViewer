package web.utils;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.MetaData;
import model.TimeSeriesData;
import web.messages.TimeSeriesMessage;

public class JsonUtils {
	private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	private static Map<Float,Gson> gsons;
	
	static {
		gsons = new HashMap<>();
	}
	public JsonUtils() {}

	private static Gson getTimeSeriesGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(TimeSeriesMessage.class, new TimeSeriesDeserializer());
		return builder.create();
	}
	
	public static Gson getGson(float version) {
		if(gsons.containsKey(version))
			return gsons.get(version);
		
		logger.info("Creating gson for version:" + version);
		Gson gson = getTimeSeriesGson();
		gsons.put(version, gson);
		return gson;
	}
	
	private static class TimeSeriesDeserializer 
	implements JsonDeserializer<TimeSeriesMessage>{

		@Override
		public TimeSeriesMessage deserialize(JsonElement json, 
									Type typeOfT, 
									JsonDeserializationContext context)
									throws JsonParseException {
			Gson gson = new Gson();
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
			JsonObject obj = json.getAsJsonObject();
			MetaData metadata = null;
			Map<Date,TimeSeriesData> dataMap = new HashMap<>();
			
			for(Entry<String, JsonElement> entry : obj.entrySet()) {
			   if(entry.getKey().equals("Meta Data"))
				   metadata = gson.fromJson(entry.getValue(), MetaData.class);
			   if(entry.getKey().contains("Time Series")) {
				   JsonObject tsObj = entry.getValue().getAsJsonObject();
				   for(Entry<String,JsonElement> timeSeriesEntry : tsObj.entrySet()) {
					   Date date = null;
					   try {
						   date = dateFormatter.parse(timeSeriesEntry.getKey());
					   } catch (ParseException e) {
						   e.printStackTrace();
					   }
					   
					   TimeSeriesData data = gson.fromJson(timeSeriesEntry.getValue(), TimeSeriesData.class);
					   if(date != null && data != null)
						   dataMap.put(date, data);
					   
				   }
			   }
			}
			return new TimeSeriesMessage(metadata,dataMap);
		}
		
	}
}
