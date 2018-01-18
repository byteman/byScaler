 
package com.blescaler.db;




public class CountRecord {

 	private String id; 		//记录编号
    private String count; 		//计数个数
	private String perWeight; //单位重量
	private String totalWeight; //总重量
    private long time; //计数时间
 	 
    public String getID()
    {
		return this.id;
    	
    }
	public void setID(String _id) {
		// TODO Auto-generated method stub
		this.id = _id;
	}
	
	public String getCount() {
		return count;
	}
	public void setCount(String _count) {
		this.count = _count;
	}
	
	public void setPerWeight(String wet) {
		// TODO Auto-generated method stub
		this.perWeight = wet;
	}
	
	public String getPerWeight() {
		return perWeight;
	}
	public void setTotalWeight(String wet) {
		// TODO Auto-generated method stub
		this.totalWeight = wet;
	}
	
	public String getTotalWeight() {
		return totalWeight;
	}
	


	public String getFormatTime()
	{
		return Utils.getNormalTime(this.time);
	}
	public String getFormatDate()
	{
		return Utils.getNormalDate(this.time);
	}
	
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
}
