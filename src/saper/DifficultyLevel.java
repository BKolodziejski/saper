package saper;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import saper.scores.ScoreList;

@XmlRootElement(namespace = "saper")
public class DifficultyLevel {
	public static final DifficultyLevel EASY = new DifficultyLevel(16, 16, 10);
	public static final DifficultyLevel MEDIUM = new DifficultyLevel(18, 20, 50);
	public static final DifficultyLevel HARD = new DifficultyLevel(20, 20, 100);
	
	public final int X_TILES;
	public final int Y_TILES;
	public final int BOMB_NO;
	
	@XmlElement
	private ScoreList scoreList;
	
	public DifficultyLevel(int x, int y, int b)
	{
		X_TILES = x;
		Y_TILES = y;
		BOMB_NO = b;
		scoreList = new ScoreList();
	}
	
	public DifficultyLevel()
	{
		X_TILES = 16;
		Y_TILES = 16;
		BOMB_NO = 10;
		scoreList = new ScoreList();
	}

	public ScoreList getScoreList() {
		return scoreList;
	}
}
