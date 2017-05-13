package saper;

import javafx.scene.image.Image;
import javafx.scene.text.Font;

public class ResourceManager {
	public final static Image BOMB_IMG = new Image(
			ResourceManager.class.getResource("/saper/resources/mine.png").toExternalForm(),
			View.TILE_SIZE - 2, View.TILE_SIZE - 2, false, true
			);
	
	/*public final static Image NORMAL_FACE = new Image(
			ResourceManager.class.getResource("/saper/resources/normal-face.png").toExternalForm(),
			View.TILE_SIZE - 2, View.TILE_SIZE - 2, false, true
			);
	
	public final static Image DEAD_FACE = new Image(
			ResourceManager.class.getResource("/saper/resources/dead-face.png").toExternalForm(),
			View.TILE_SIZE - 2, View.TILE_SIZE - 2, false, true
			);
	
	public final static Image WIN_FACE = new Image(
			ResourceManager.class.getResource("/saper/resources/win-face.png").toExternalForm(),
			View.TILE_SIZE - 2, View.TILE_SIZE - 2, false, true
			);
	*/
	public final static Image FLAG = new Image(
			ResourceManager.class.getResource("/saper/resources/flag.png").toExternalForm(),
			View.TILE_SIZE - 2, View.TILE_SIZE - 2, false, true
			);
	
	public static void init()
	{
		Font.loadFont(ResourceManager.class.getResource("/saper/resources/digital-7.ttf").toExternalForm(), 24);
	}
}
