package saper;

public class Coords
{
	public int x;
	public int y;
	public Tile tile;
	
	public Coords(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Coords(int x, int y, Tile t)
	{
		this.x = x;
		this.y = y;
		this.tile = t;
	}
}