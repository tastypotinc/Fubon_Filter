package org.Fubon.Server;
import java.util.Comparator;
import java.util.Date;
/*
public class DayLine implements Comparable<DayLine> {
	public Date		tDate;
	public float	Open;
	public float	High;
	public float	Low;
	public float	Close;
	public long		Volume;
	DayLine() 
	{
	}
	@Override

	public int compareTo(DayLine D2) throws ClassCastException
	{
		//DayLine D2=(DayLine)o2;
		return this.tDate.compareTo(D2.tDate);	
	}

}

class DayLineComparator implements Comparator<DayLine> {
	@Override
	public int compare(DayLine D1,DayLine D2) {
		//Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
		return D1.tDate.compareTo(D2.tDate);	
	}

}

*/



public class DayLine implements Comparable<Object> {
	public Date		tDate;
	public float	Open;
	public float	High;
	public float	Low;
	public float	Close;
	public long		Volume;
	DayLine() 
	{
	}
	@Override
	public int compareTo(Object o2) throws ClassCastException
	{
		DayLine D2=(DayLine)o2;
		return this.tDate.compareTo(D2.tDate);	
	}

}

class DayLineComparator implements Comparator<Object> {
	@Override
	public int compare(Object o1,Object o2) {
		//Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
		DayLine D1=(DayLine)o1;
		DayLine D2=(DayLine)o2;
		return D1.tDate.compareTo(D2.tDate);
		
	}


}


