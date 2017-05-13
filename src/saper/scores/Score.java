package saper.scores;

import java.util.Date;

public class Score {
	public Score(String nm, int tm, Date dt) {
		name = nm;
		time = tm;
		date = dt;
	}
	
	public String name;
	public int time;
	public Date date;
}