package web;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import event.CallbackInterface;
import event.Event;
import event.EventEmitter;
import event.EventSource;
import event.EventType;
import web.messages.SymbolSearchMessage;
import web.messages.TimeSeriesMessage;
import web.utils.EnitityUtils;

public class APIRequest
extends EventEmitter
implements Runnable{

	private static Logger logger = LoggerFactory.getLogger(APIRequest.class);
	private FunctionType type;
	private String symbol;
	private IntervalType interval;
	private static final String API_KEY = "";
	private static final String DOMAIN = "https://www.alphavantage.co/query?";
	private boolean isRunning = false;
	
	private CallbackInterface callback = null;
	
	@SuppressWarnings("unused")
	private APIRequest() {}


	public APIRequest(FunctionType type, String symbol, IntervalType interval) {
		super();
		this.type = type;
		this.symbol = symbol;
		this.interval = interval;
	}


	/**
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * @return the interval
	 */
	public IntervalType getInterval() {
		return interval;
	}

	/**
	 * @return the apikey
	 */
	public static String getApikey() {
		return API_KEY;
	}
	
	public void cancel() {
		isRunning = false;
	}
	
	public boolean isCanceled() {
		return isRunning;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		switch(type) {
			case TIME_SERIES_INTRADAY:
				return DOMAIN+"function="+type+"&symbol="+symbol+"&interval="+interval.getInterval()
						+"&apikey="+API_KEY;
			case SYMBOL_SEARCH:
				return DOMAIN+"function="+type+"&keywords="+symbol+"&apikey="+API_KEY;
			default:
				return DOMAIN+"function="+type+"&symbol="+symbol+"&apikey="+API_KEY;
		}
	}


	@Override
	public void run() {
		isRunning = true;
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String requestStr = this.toString();
		logger.info("Making a request:"+requestStr);
		HttpGet httpGet = new HttpGet(this.toString());
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Gives the opportunity to cancel a request while waiting for the response
		if(isRunning) {
			StatusLine status = response.getStatusLine();
			if(status.getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				switch(type) {
					case SYMBOL_SEARCH:
						SymbolSearchMessage search = EnitityUtils.consumeSymbolSearch(entity);
						emit(new Event() {
							@Override
							public EventType getType() {
								return EventType.MESSAGE_EVENT;
							}

							@Override
							public EventSource getSource() {
								return search;
							}
						});
						break;
					default:
						TimeSeriesMessage msg = EnitityUtils.consumeTimeSeries(entity);
						emit(new Event() {
			
							@Override
							public EventType getType() {
								return EventType.MESSAGE_EVENT;
							}
			
							@Override
							public EventSource getSource() {
								return msg;
							}
						});
				}
				if(callback != null)
					callback.complete();
			}else {//status code != 200
				if(callback != null)
					callback.failed();
			}
		}
	}
	
	public void registerCallback(CallbackInterface callback) {
		this.callback = callback;
	}
}
