package web.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import web.messages.SymbolSearchMessage;
import web.messages.TimeSeriesMessage;

public class EnitityUtils {

	private static Logger logger = LoggerFactory.getLogger(EnitityUtils.class);
	
	public static TimeSeriesMessage consumeTimeSeries(HttpEntity entity) {
		try {
			InputStream is = entity.getContent();
			Gson gson = JsonUtils.getGson(1.0f);
			TimeSeriesMessage msg = gson.fromJson(new BufferedReader(new InputStreamReader(is)), TimeSeriesMessage.class);
			logger.info("EntityUtils processed time series message");
			return msg;
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static SymbolSearchMessage consumeSymbolSearch(HttpEntity entity) {
		try {
			InputStream is = entity.getContent();
			Gson gson = JsonUtils.getGson(1.0f);
			SymbolSearchMessage msg = gson.fromJson(new BufferedReader(new InputStreamReader(is)), SymbolSearchMessage.class);
//			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//			String line = reader.readLine();
//			while(line != null) {
//				System.out.println(line);
//				line = reader.readLine();
//			}
			
			logger.info("Entity Utils consuming symbol search");
			return msg;
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
