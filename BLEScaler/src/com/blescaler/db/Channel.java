package com.blescaler.db;

public class Channel {
	public float getR0() {
		return R0;
	}
	public void setR0(float r0) {
		R0 = r0;
	}
	public float getT0() {
		return T0;
	}
	public void setT0(float t0) {
		T0 = t0;
	}
	public float getG() {
		return G;
	}
	public void setG(float g) {
		G = g;
	}
	public float getK() {
		return K;
	}
	public void setK(float k) {
		K = k;
	}
	public float getC() {
		return C;
	}
	public void setC(float c) {
		C = c;
	}
	private double Filter(double v)
	{
		if(index >=5 ) index = 0;
		buffer[index] = v;
		double sum = 0;
		for(int i = 0; i < 5; i++)
		{
			sum+=buffer[i];
		}
		return sum / 5;
	}
	public double CalcValue(double P_AD,double T_AD)
	{
	
		
	    double a = 0.0014051;
	    double b = 0.0002369;
	    double c = 0.0000001019;
	    //const double d = 101.97;
	    //计算温度
	    double N2 = Math.log(T_AD);
	    double T1 =  1/(a+ b*N2 + c*N2*N2*N2)-273.2; //温度值
	    //计算
	    double R1 = (P_AD*P_AD) / 1000; //渗透压.

	    double P  = G * ( R1 - R0 ) + K * ( T1 - T0 );


	    double H = P * C + Diff;
	    
	    H = Filter(H);
	    if(H < 0) return -H;
	    return H;
	}
	private double buffer[] = new double[5];
	private int index = 0;
	private float R0;
	private float T0;
	private float G;
	private float K;
	private float C;
	private double A;
	private double B;
	private double CC;
	private float Diff;
	public float getDiff() {
		return Diff;
	}
	public void setDiff(float diff) {
		Diff = diff;
	}
	
}
