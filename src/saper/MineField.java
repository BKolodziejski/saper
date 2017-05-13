package saper;
import java.util.ArrayList;
import java.util.Collections;

import saper.enums.GameState;
import saper.enums.TileState;

public class MineField
{
	private GameState gameState = GameState.START;
	private Tile[][] tileField;
	private DifficultyLevel difficultyLevel;
	private int remainingTiles;
	//private int bombLeftCount;
	
	MineField(DifficultyLevel diff)
	{
		difficultyLevel = diff;
		init();
	}
	
	public DifficultyLevel getDifficulty()
	{
		return difficultyLevel;
	}
	
	public void setDifficulty(DifficultyLevel diff)
	{
		difficultyLevel = diff;
	}
	
	/*public int getBombsLeftCount()
	{
		return bombLeftCount;
	}*/
	
	private void setBomb(int x, int y)
	{
		tileField[x][y].isBomb = true;
		
		for (Coords c : getNeighbours(x, y))
		{
			tileField[c.x][c.y].adjacentBombNumber += 1;
		}
	}
	
	// changedTiles is used for recurrence, should be null for normal usage
	public ArrayList <Coords> revealTile(int x, int y, ArrayList <Coords> changedTiles)
	{
		Tile tile = tileField[x][y];
		
		if (tile.state == TileState.REVEALED)
		{
			return changedTiles;
		}
		
		if (changedTiles == null)
		{
			changedTiles = new ArrayList <Coords>();
		}
		
		tile.state = TileState.REVEALED;
		remainingTiles -= 1;
		changedTiles.add(new Coords(x, y, tile));
		
		if (tile.isBomb)
		{
			gameState = GameState.OVER;
			return changedTiles;
		}
		
		if (remainingTiles == difficultyLevel.BOMB_NO)
		{
			gameState = GameState.VICTORY;
			return changedTiles;
		}
		
		if (tile.adjacentBombNumber == 0)
		{
			for (Coords c : getNeighbours(x, y))
			{
				revealTile(c.x, c.y, changedTiles);
			}
		}
		
		return changedTiles;
	}
	
	public ArrayList <Coords> getBombs()
	{
		ArrayList <Coords> bombList = new ArrayList<>();
		for (int i = 0; i < difficultyLevel.Y_TILES; ++i)
		{
			for (int j = 0; j < difficultyLevel.X_TILES; ++j)
			{
				Tile tile = tileField[j][i];
				if (tile.isBomb)
				{
					bombList.add(new Coords(j, i, tile));
				}
			}
		}
		return bombList;
	}

	public GameState getState()
	{
		return gameState;
	}
	
	public void setState(GameState st)
	{
		gameState = st;
	}
	
	public Tile getTile(int x, int y)
	{
		return tileField[x][y];
	}
	
	public ArrayList<Coords> getNeighbours(int x, int y)
	{
		ArrayList<Coords> list = new ArrayList<>();
		
		for (int i = x - 1; i < x + 2; ++i)
		{
			if (i >= 0 && i < difficultyLevel.X_TILES)
			{
				for (int j = y - 1; j < y + 2; ++j)
				{
					if (j >= 0 && j < difficultyLevel.Y_TILES)
					{
						if (tileField[i][j].state != TileState.REVEALED)
							list.add(new Coords(i, j, tileField[i][j]));
					}
				}
			}
		}
		
		return list;
	}
	
	public void init()
	{
		gameState = GameState.START;
		ArrayList <Coords> bombShuffleArr = new ArrayList<Coords>();

		this.remainingTiles = difficultyLevel.Y_TILES * difficultyLevel.X_TILES;
		//this.bombLeftCount = difficultyLevel.BOMB_NO;
		
		tileField = new Tile[difficultyLevel.X_TILES][difficultyLevel.Y_TILES];
		
		for (int i = 0; i < difficultyLevel.X_TILES; ++i)
		{
			for (int j = 0; j < difficultyLevel.Y_TILES; ++j)
			{
				tileField[i][j] = new Tile();
				bombShuffleArr.add(new Coords(i, j));
			}
		}
		Collections.shuffle(bombShuffleArr);
		for (int i = 0; i < difficultyLevel.BOMB_NO; ++i) {
			Coords temp = bombShuffleArr.get(i);
			setBomb(temp.x, temp.y);
		}
	}
	
	public void reset()
	{
		init();
	}
	
}
