package com.android.watch.calendar.util;

import java.util.Calendar;
import java.util.Date;

import com.watch.calendar.MyCalendar;

public class CompeterCalendar {
	/** һ��ĺ�����	 */
	public static final long DAY_MILLIS = 24 * 60 * 60 * 1000;
	private static final int YEAR_MAX = 2100;
	private static final int YEAR_MIN = 1900;
	private Calendar gregorianDate;
	/**
	 * ֧�ַ�Χ������
	 * @return
	 */
	public static int getMaxYear(){
		return YEAR_MAX;
	}
	
	/**
	 * ֧�ַ�Χ��С���
	 * @return
	 */
	public static int getMinYear(){
		return YEAR_MIN + 1;
	}
	/**
	 * ���ع�����Ϣ<br/>
	 * ����:{@link java.util.Calendar#get(int)}
	 * @param field
	 * @return
	 */
	public int getGregorianDate(int field){
		return gregorianDate.get(field);
	}
	/**
	 * @see Calendar#getTimeInMillis()
	 * @return
	 */
	public long getTimeInMillis(){
		return gregorianDate.getTimeInMillis();
	}
	public CompeterCalendar(){
		this(new Date());
	}
	
	public CompeterCalendar(Calendar date){
		this(date.getTimeInMillis());
	}
	
	
	public CompeterCalendar(Date date){
		this(date.getTime());
	}
	public CompeterCalendar(long milliSeconds){
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(milliSeconds);
		gregorianDate = date;
	}
	/**
	 * �Ƿ�Ϊ����
	 * @return
	 */
	public boolean isToday(){
		Calendar today = Calendar.getInstance();
		if ((gregorianDate.get(Calendar.YEAR) == today.get(Calendar.YEAR))
				&& (gregorianDate.get(Calendar.MONTH) == today.get(Calendar.MONTH))
				&& (gregorianDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)))
		{
			return true;
		}
		
		return false;
	}
}
