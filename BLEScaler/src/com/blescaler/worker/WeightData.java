package com.blescaler.worker;

public class WeightData {

		WeightData(int g, int t, int n)
		{
			gross = g;
			tare = t;
			net = t;
		}
		public WeightData()
		{
			gross = WorkService.getGrossWeight();
			tare  = WorkService.getTareWeight();
			net   = gross - tare;
		}
		private int gross;
		private int tare;
		private int net;
		public int getGross() {
			return gross;
		}
		public void setGross(int gross) {
			this.gross = gross;
		}
		public int getTare() {
			return tare;
		}
		public void setTare(int tare) {
			this.tare = tare;
		}
		public int getNet() {
			return net;
		}
		public void setNet(int net) {
			this.net = net;
		}
	};


