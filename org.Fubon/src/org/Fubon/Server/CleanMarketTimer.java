package org.Fubon.Server;


import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
public class CleanMarketTimer {
	Timer timer = new Timer();
public CleanMarketTimer() { };

// 改用 Tick 內容判斷清盤 Timer 清盤不再使用

public void start() throws Exception 
{

	Calendar date = Calendar.getInstance();
	date.setTime(new Date());
	date.add(Calendar.DAY_OF_MONTH,1); // 驅動此程式的後一天 
	date.set(Calendar.AM_PM,Calendar.AM); // 上午
	date.set(Calendar.HOUR,11); // 11點 
	date.set(Calendar.MINUTE,0); // 0分
	date.set(Calendar.SECOND,0); // 0秒
	date.set(Calendar.MILLISECOND,0); // 0毫秒
	
	// timer.schedule(param1,param2,param3)
	// param1: 就是要定時驅動執行的程式
	// param2: 設定開始執行的時間,如上面的Calendar設定
	// param3: 執行間隔時間(單位: 毫秒)
	timer.schedule(
	new DailyRun(),
	date.getTime(),
	1000 * 60 * 60 * 24
	); 
} // end of method

public void stop() throws Exception {
		timer.cancel();
} // end of method

public class DailyRun extends TimerTask {
	public DailyRun() {
	}

	public void run() {
	// 這裡是要執行的程式內容 
	} // End of method 

	} // end of class 

} // end of class


