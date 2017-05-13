package saper;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class TimerTask extends Task <Void> {

	private View view;
	private int currTime;
	public TimerTask(View v)
	{
		view = v;
		currTime = 0;
	}
	
	public int getTime()
	{
		return currTime;
	}
	
	@Override
	public Void call() throws Exception 
	{
		while (!isCancelled())
		{
			Thread.sleep(1000);
			++currTime;
			Platform.runLater( () -> view.drawTimer(currTime) );
		}
		
		return null;
	}

}
