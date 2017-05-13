package saper;

import saper.enums.TileState;

public class Tile
{
	public int adjacentBombNumber = 0;
	public boolean isBomb = false;
	public TileState state = TileState.DEFAULT;
}
