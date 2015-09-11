package com.example.bluetooth.le;



import android.app.Activity;
import android.os.Bundle;
import android.widget.Spinner;

public class TestActivity extends Activity {

	private Spinner sp_zerotrack;
	private Spinner sp_zeroinit;
	private Spinner sp_mtd;
	private Spinner sp_dignum;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		sp_zerotrack = (Spinner) findViewById(R.id.sp_zerotrack);
		sp_zeroinit = (Spinner) findViewById(R.id.sp_zeroinit);
		sp_mtd = (Spinner) findViewById(R.id.sp_mtd);
		sp_dignum = (Spinner) findViewById(R.id.sp_dignum);
		
	}

}
