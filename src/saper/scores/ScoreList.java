package saper.scores;

import java.util.LinkedList;

public class ScoreList {
	
	private LinkedList<Score> innerScoreList = new LinkedList<Score>();
	
	public LinkedList<Score> getScores() 
	{
		return innerScoreList;
	}
	
	public int size()
	{
		return innerScoreList.size();
	}
	
	public Score get(int index)
	{
		return innerScoreList.get(index);
	}
	
	public int addScore(Score newScore) 
	{
		int index = 0;
		
		for (Score s : innerScoreList)
		{
			if (newScore.time < s.time)
			{
				innerScoreList.add(index, newScore);
				if (innerScoreList.size() > 10)
					innerScoreList.removeLast();
				return index;
			}
			++index;
		}
		
		if (innerScoreList.size() < 10)
		{
			innerScoreList.add(newScore);
			return innerScoreList.size()-1;
		}
		return 11;
	}
}
