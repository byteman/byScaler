 
package com.blescaler.db;

import com.blescaler.utils.Utils;
import com.blescaler.worker.WeightData;


public class WeightRecord {

 	private String wet_id;
    private String tare;
    private String net;
    private String gross;
    private long time;
    public WeightRecord()
    {
    	WeightData w = new WeightData();
    	setGross(w.getGross()+"");
    	setTare(w.getTare()+"");
    	setNet(w.getNet()+"");
    }
   
    public String getID()
    {
		return this.wet_id;
    	
    }
	public String getTare() {
		return tare;
	}

	public void setTare(String _tare) {
		this.tare = _tare;
	}

	{
		return Utils.getNormalTime(this.time);
	}
	public String getFormatDate()
	{
		return Utils.getNormalDate(this.time);
	}
	public String getNet() {
		return net;
	}

	public void setNet(String _net) {
		this.net = _net;
	}
	public String getGross() {
		return gross;
	}

	public void setGross(String _gross) {
		this.gross = _gross;
	}
	

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	public void setID(String wetid) {
		// TODO Auto-generated method stub
		this.wet_id = wetid;
	}
	
}