package saper;

import java.util.ArrayList;
import java.util.Date;

import saper.enums.GameState;
import saper.enums.TileState;
import saper.scores.SQLManager;
import saper.scores.Score;
import saper.scores.ScoreList;

public class Controller
{
	private MineField mineField;
	private View view;
	private TimerTask timerTask;
	private int bombsLeftCount;
	
	public Controller(MineField mf, View v)
	{
		SQLManager.init();
		SQLManager.loadScores(mf.getDifficulty());
		
		bombsLeftCount = mf.getDifficulty().BOMB_NO;
		mineField = mf;
		view = v;
		view.setController(this);
		view.drawMinesLeftCounter(bombsLeftCount);
		
	}
	
	public void handleReveal(int x, int y)
	{
		GameState gState = mineField.getState();
		if (gState == GameState.START)
		{
			mineField.setState(GameState.RUNNING);
			gState = GameState.RUNNING;
			timerTask = new TimerTask(view);
			new Thread(timerTask).start();
		}
		
		// Do nothing if game is over or tile is flagged
		if ((gState != GameState.RUNNING) || mineField.getTile(x, y).state == TileState.FLAGGED)
		{
			return;
		}
		
		ArrayList <Coords> arr = mineField.revealTile(x, y, null);
		if (arr == null)
			return;
		view.drawTiles(arr);
		
		gState = mineField.getState();
		if (gState == GameState.OVER)
		{
			view.drawExplodingBomb(x, y);
			for (Coords c : mineField.getBombs())
			{
				if(c.tile.state != TileState.FLAGGED)
					view.drawBomb(c.x, c.y);
			}
			timerTask.cancel();
			view.drawGameOver();
			return;
		}
		if (gState == GameState.VICTORY)
		{
			timerTask.cancel();
			view.drawVictory();
			ScoreList sList = mineField.getDifficulty().getScoreList();
			int place = sList.addScore(new Score("Unknown", timerTask.getTime(), new Date())) + 1;
			if(place < 11)
			{
				view.drawScoreBoard(sList, place);
			}
			
            
			return;
		}
	}
	
	public void revealSurroundings(int x, int y) {
		ArrayList <Coords> neighbours = mineField.getNeighbours(x, y);
		Tile t = mineField.getTile(x, y);
		
		if (t.state != TileState.REVEALED)
			return;
		
		int surroundingFlags = 0;
		
		for (Coords c : neighbours)
		{
			if (c.tile.state == TileState.FLAGGED)
			{
				++surroundingFlags;
			}
		}
		if (surroundingFlags < t.adjacentBombNumber)
			return;
		
		for (Coords c : mineField.getNeighbours(x, y))
		{
			handleReveal(c.x, c.y);
		}
	}
	
	public void handleFlag(int x, int y)
	{
		GameState gState = mineField.getState();
		if (gState != GameState.RUNNING && gState != GameState.START)
			return;
		
		TileState currentState = mineField.getTile(x, y).state;
		switch (currentState)
		{
			case DEFAULT:
				currentState = TileState.QUESTION_MARKED;
				break;
			case QUESTION_MARKED:
				--bombsLeftCount;
				currentState = TileState.FLAGGED;
				break;
			case FLAGGED:
				++bombsLeftCount;
				currentState = TileState.DEFAULT;
				break;
			default:
				return;
		}
		Tile newTile = mineField.getTile(x, y);
		newTile.state = currentState;
		view.drawMinesLeftCounter(bombsLeftCount);
		view.drawTile(x, y, newTile);
		
	}
	
	public void changeDifficulty(int width, int height, int bombs)
	{
		if (width * height <= bombs)
			throw new IllegalArgumentException();
		
		setDifficulty(new DifficultyLevel(width, height, bombs));
	}
	
	public void setDifficulty(DifficultyLevel diff)
	{	
		if (diff != mineField.getDifficulty())
		{
			SQLManager.saveScores(mineField.getDifficulty());
			mineField.setDifficulty(diff);
			SQLManager.loadScores(diff);
			reset();
		}
	}
	
	public void reset()
	{
		if (timerTask != null)
			timerTask.cancel();
		mineField.reset();
		DifficultyLevel diff = mineField.getDifficulty();
		view.reset(diff.X_TILES, diff.Y_TILES);
		bombsLeftCount = diff.BOMB_NO;
		view.drawMinesLeftCounter(bombsLeftCount);
	}
}
