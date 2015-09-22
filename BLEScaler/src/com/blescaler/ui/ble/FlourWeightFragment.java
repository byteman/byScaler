package com.blescaler.ui.ble;



import java.lang.ref.WeakReference;

import com.blescaler.db.WeightDao;
import com.blescaler.ui.R;
import com.blescaler.ui.ble.OneWeightFragment.MHandler;
import com.blescaler.utils.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.WorkService;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FlourWeightFragment extends BaseFragment implements View.OnClickListener {
	View root;
	
	private int cout = 0,timeout=0;
	private boolean pause = false,disconnect=false;
	
	private static Handler mHandler = null;
	protected static final String TAG = "weight_activity";
	private Runnable watchdog = new Runnable()
	{
		
		@Override
		public void run() {
			// TODO Auto-generated method stub

			   if(timeout++ > 2)
			   {
				   WorkService.readNextWgt(false);
				   timeout = 0;
			   }
			   
			  
			   mHandler.postDelayed(this, 200);  
		}
		
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.activity_flourweight, container, false);
		initRes();
		return root;
	}
	@Override
	public void onClick(View v) {

	}
	public static Fragment newFragment() {
		FlourWeightFragment f = new FlourWeightFragment();
		Bundle bundle = new Bundle();


		f.setArguments(bundle);
		return f;
		
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onPause();
		mHandler.removeCallbacks(watchdog);
		
		WorkService.delHandler(mHandler);
		Log.e(TAG, "onStop");
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		WorkService.addHandler(mHandler);
		mHandler.postDelayed(watchdog, 200);
		//WorkService.connectPrinter(null);
		if(!WorkService.hasConnectAll())
		{
			//WorkService.connectAll();
		}
		pause = false;
	}
	private void initRes()
	{
		mHandler = new MHandler(this.getActivity());

		mHandler.postDelayed(watchdog, 200);
	}
	
	static class MHandler extends Handler {

		WeakReference<FlourWeightFragment> mActivity;

		MHandler(FlourWeightFragment activity) {
			mActivity = new WeakReference<FlourWeightFragment>(activity);
			
		}
		
		@Override
		public void handleMessage(Message msg) {
			FlourWeightFragment theActivity = mActivity.get();
			switch (msg.what) {

				case Global.MSG_BLE_WGTRESULT:
				{
					//BluetoothDevice device = (BluetoothDevice) msg.obj;
					
					Scaler d = (Scaler) msg.obj;
					
					//theActivity.tv_weight.setText(String.valueOf(totalweight)+"  kg ");
					theActivity.timeout = 0;
					
					WorkService.readNextWgt(false);
					break;
				}
				case Global.MSG_BLE_DISCONNECTRESULT:
				{
					String addr =(String)msg.obj;
					Utils.Msgbox(theActivity.getActivity(), addr + " has disconnect!!");
					break;
				}
		
				case Global.MSG_BLE_FAILERESULT:
				{

					Toast.makeText(theActivity.getActivity(), WorkService.getFailReason(msg.arg1), Toast.LENGTH_SHORT).show();
					break;
				}
				
			
			}
			
		}
	}
	

}
