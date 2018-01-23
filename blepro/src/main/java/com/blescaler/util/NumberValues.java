package com.blescaler.util;

public class NumberValues {
	static public FloatValue GetFloatValue(String edit)
	{
		FloatValue v = new FloatValue();
		try{
			v.value = Float.parseFloat(edit);
			v.ok = true;
		}
		catch(NumberFormatException e)
		{
			
		}
		
		return v;
		
	}
	static public IntValue GetIntValue(String edit)
	{
		IntValue v = new IntValue();
		try{
			v.value = Integer.parseInt(edit);
			v.ok = true;
		}
		catch(NumberFormatException e)
		{
			
		}
		
		return v;
		
	}
}


