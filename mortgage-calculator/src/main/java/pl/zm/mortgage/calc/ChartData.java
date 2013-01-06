package pl.zm.mortgage.calc;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public class ChartData<T extends Enum<T>> {

    private Class<T> clazz;
    
    public ChartData(Class<T> clazz) {
        this.clazz = clazz;
        dataSeries = new EnumMap<T, List<Integer>>(clazz);
    }
    
    private String title;
    private List<Integer> xAxis = new LinkedList<Integer>();
    private EnumMap<T, List<Integer>> dataSeries;

    public T[] getSeriesNames(){
    	return clazz.getEnumConstants();
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Integer> getxAxis() {
        return xAxis;
    }
    
    public void addX(int x) {
        xAxis.add(x);
    }

    public List<Integer> getDataSeries(T name) {
        return dataSeries.get(name);
    }

    public void setData(T series, int data) {
        if(dataSeries.get(series) == null)
            dataSeries.put(series, new LinkedList<Integer>());
        dataSeries.get(series).add(data);
    }

}
