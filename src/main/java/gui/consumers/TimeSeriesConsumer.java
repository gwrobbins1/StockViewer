package gui.consumers;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import event.AbstractEventConsumer;
import event.Event;
import event.EventSource;
import event.EventType;
import gui.GuiManager;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import model.MetaData;
import model.TimeSeriesData;
import web.messages.TimeSeriesMessage;

public class TimeSeriesConsumer
extends AbstractEventConsumer{
	private Logger logger = LoggerFactory.getLogger(TimeSeriesConsumer.class);
	private static GuiManager guiManager;
    
    private static final float BOUND_PADDING = 0.02f;

	static {
		guiManager = GuiManager.getInstance();
	}
	
	public TimeSeriesConsumer() {
		super();
	}

	@Override
	public void consume(Event event) {
		EventSource src = event.getSource();
		
		if(event.getType() != EventType.MESSAGE_EVENT) return;
		if( !(src instanceof TimeSeriesMessage)) return;
		
		// We have received time series message event
		TimeSeriesMessage msg = (TimeSeriesMessage)src;
		MetaData meta = msg.getMetadata();
		Map<Date,TimeSeriesData> data = msg.getTimeseriesData();

		if(meta == null || data == null || data.isEmpty()) {
			logger.error(msg.toString());
			guiManager.resetGui("Received no data");
			return;
		}
    	
    	XYChart.Series<String,Number> series[] = new XYChart.Series[4];
    	series[0] = getOpeningSeries(meta.getSymbol(),data);
    	series[1] = getClosingSeries(meta.getSymbol(),data);
    	series[2] = getHighSeries(meta.getSymbol(),data);
    	series[3] = getLowSeries(meta.getSymbol(),data);
    	
    	Float[] bounds = getLineChartBounds(data,BOUND_PADDING);
		LineChart<String,Number> lineChart = getIntradayLineChart(Arrays.asList(series),bounds,meta.getInterval());
		
		lineChart.setPrefHeight(guiManager.getWindowHeight());
		lineChart.setPrefWidth(guiManager.getWindowWidth());
		
    	guiManager.addGraph(lineChart);
    }
	
	/**
	 * Get the upper and lower bounds of the data passed in. The return is an 
	 * array of the bounds. Lower bound is index 0, upper bound is index 1. A padding
	 * is added to the bounds.
	 * 
	 * @param data to find the lower and upper bounds.
	 * @param padding to apply to the lower and upper bounds
	 * @return array of bounds
	 */
	private Float[] getLineChartBounds(Map<Date,TimeSeriesData> data, Float padding) {
		Float[] bounds = new Float[2];
		// lower bound
        bounds[0] = Float.POSITIVE_INFINITY;
        // upper bound
        bounds [1] = Float.NEGATIVE_INFINITY;
        
        for(Map.Entry<Date, TimeSeriesData> entry : data.entrySet()) {
        	TimeSeriesData tsData = entry.getValue();
        	
        	Float openning = tsData.getOpen();
        	Float closing = tsData.getClose();
        	Float high = tsData.getHigh();
        	Float low = tsData.getLow();
        	
        	if(openning < bounds[0]) bounds[0] = openning;
        	if(closing < bounds[0]) bounds[0] = closing;
        	if(high < bounds[0]) bounds[0] = high;
        	if(low< bounds[0]) bounds[0] = low;
        	
        	if(openning > bounds[1]) bounds[1] = openning;
        	if(closing > bounds[1]) bounds[1] = closing;
        	if(high > bounds[1]) bounds[1] = high;
        	if(low > bounds[1]) bounds[1] = low;
        }
        
        bounds[0] -= padding;
        bounds[1] += padding;
        
        return bounds;
	}
	
	private XYChart.Series<String,Number> getOpeningSeries(String name,Map<Date,TimeSeriesData> data) {
    	XYChart.Series<String,Number> series = new XYChart.Series<>();
        series.setName(name+"_open");
    	
		for(Map.Entry<Date, TimeSeriesData> entry : data.entrySet()) {
	        //populating the series with data
			String date = entry.getKey().toString();
			Float open = entry.getValue().getOpen();
			series.getData().add(new XYChart.Data<String,Number>(date,open));
		}
		
    	return series;
	}

	private XYChart.Series<String,Number> getClosingSeries(String name,Map<Date,TimeSeriesData> data) {
    	XYChart.Series<String,Number> series = new XYChart.Series<>();
        series.setName(name+"_closing");
    	
		for(Map.Entry<Date, TimeSeriesData> entry : data.entrySet()) {
	        //populating the series with data
			String date = entry.getKey().toString();
			Float close = entry.getValue().getClose();
			series.getData().add(new XYChart.Data<String,Number>(date,close));
		}
		
    	return series;
	}

	private XYChart.Series<String,Number> getHighSeries(String name,Map<Date,TimeSeriesData> data) {
    	XYChart.Series<String,Number> series = new XYChart.Series<>();
        series.setName(name+"_high");
    	
		for(Map.Entry<Date, TimeSeriesData> entry : data.entrySet()) {
	        //populating the series with data
			String date = entry.getKey().toString();
			Float high = entry.getValue().getHigh();
			series.getData().add(new XYChart.Data<String,Number>(date,high));
		}
		
    	return series;
	}
	
	private XYChart.Series<String,Number> getLowSeries(String name,Map<Date,TimeSeriesData> data) {
    	XYChart.Series<String,Number> series = new XYChart.Series<>();
        series.setName(name+"_low");
    	
		for(Map.Entry<Date, TimeSeriesData> entry : data.entrySet()) {
	        //populating the series with data
			String date = entry.getKey().toString();
			Float low = entry.getValue().getLow();
			series.getData().add(new XYChart.Data<String,Number>(date,low));
		}
		
    	return series;
	}
	
	private LineChart<String,Number> getIntradayLineChart(List<XYChart.Series<String,Number>> series, 
										   Float[] bounds, 
										   String title) {
		
        final CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Time");
		
        final NumberAxis yAxis = new NumberAxis(bounds[0],bounds[1],1.0f);
        yAxis.setLabel("Price($)");
                    
        LineChart<String,Number> lineChart = new LineChart<>(xAxis,yAxis);
    	lineChart.setTitle(title);
    	
        try {
        	lineChart.getData().addAll(series);
        }catch(IllegalArgumentException e) {
        	logger.error(e.getMessage());
        }
    	
    	return lineChart;
	}
}
