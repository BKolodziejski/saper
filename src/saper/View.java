package saper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import saper.enums.TileState;
import saper.scores.ScoreList;

public class View {
	private int width;
	private int height;
	public final static int TILE_SIZE = 24;
	private Controller controller;
	private GraphicsContext gc;
	private Stage primaryStage;
	private VBox root;
	private HBox resetBox;
	private Label minesLeftCounter;
	private Label timer;
	private Button resetFace;
	private MenuBar menuBar;
	
	public View(Stage primaryStage, int xbombs, int ybombs)
	{
		this.primaryStage = primaryStage;
		
		ResourceManager.init();
		
		primaryStage.getIcons().add(ResourceManager.FLAG);
		
		primaryStage.setTitle("Saper");
        
		drawMenu();
        drawMainBar();
        
        reset(xbombs, ybombs);
        
        primaryStage.setResizable(false);
        primaryStage.show();
	}
	
	public void setController(Controller c)
	{
		this.controller = c;
	}
	
	public void drawTile(int x, int y, Tile tile)
	{
		x *= TILE_SIZE;
		y *= TILE_SIZE;
		
		if (tile.state != TileState.REVEALED)
		{
			gc.setFill(Color.ANTIQUEWHITE);
			gc.fillRect(x, y, TILE_SIZE-2, TILE_SIZE-2);
			gc.setFill(Color.web("0x333333"));
			gc.fillRect(x+2, y+2, TILE_SIZE-2, TILE_SIZE-2);
			gc.setFill(Color.web("0xbfbfbf"));
			gc.fillRect(x+2, y+2, TILE_SIZE-4, TILE_SIZE-4);
			
			if (tile.state == TileState.QUESTION_MARKED)
			{
				gc.setFill(Color.DARKSLATEGRAY);
				gc.fillText("?", x+TILE_SIZE/3, y+TILE_SIZE/1.5);
			}
			
			if (tile.state == TileState.FLAGGED)
			{
			    gc.drawImage( ResourceManager.FLAG, x, y );
			}
		
		}
		else
		{
			gc.setFill(Color.web("0x555555"));
			gc.fillRect(x+1, y+1, TILE_SIZE-1, TILE_SIZE-1);
			gc.setFill(Color.web("0xbfbfbf"));
			gc.fillRect(x+2, y+2, TILE_SIZE-3, TILE_SIZE-3);
			
			if (tile.adjacentBombNumber > 0)
			{
				gc.setFill(Color.RED);
				gc.fillText(Integer.toString(tile.adjacentBombNumber), x+TILE_SIZE/3, y+TILE_SIZE/1.5);
			}
		}
		
		return;
	}
	
	public void drawBomb(int x, int y)
	{
		x *= TILE_SIZE;
		y *= TILE_SIZE;
		
		//Image bomb = new Image("assets/mine.png", TILE_SIZE - 2, TILE_SIZE - 2, false, true);
	    gc.drawImage( ResourceManager.BOMB_IMG, x, y );
	}
	
	public void drawExplodingBomb(int x, int y)
	{
		int x_c = x * TILE_SIZE;
		int y_c = y * TILE_SIZE;
		
		gc.setFill(Color.RED);
		gc.fillRect(x_c+1, y_c+1, TILE_SIZE-1, TILE_SIZE-1);
		drawBomb(x, y);
		return;
	}
	
	public void drawTiles(ArrayList <Coords> arr)
	{
		for (Coords c : arr)
		{
			drawTile(c.x, c.y, c.tile);
		}
	}
	
	public void reset(int xbombs, int ybombs)
	{
		width = xbombs * TILE_SIZE;
		height = ybombs * TILE_SIZE;
		
		/* Create canvas with tiles, set mouse handler */
        Canvas canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        
        canvas.setOnMouseClicked(
        		(MouseEvent e) ->
        		{
        			int x = (int) (e.getX() / TILE_SIZE);
    				int y = (int) (e.getY() / TILE_SIZE);
    				
    				MouseButton clickType = e.getButton();
    				
    				switch(clickType)
    				{
    				case PRIMARY:
    					controller.handleReveal(x, y);
    					break;
    				case SECONDARY:
    					controller.handleFlag(x, y);
    					break;
    				case MIDDLE:
    					controller.revealSurroundings(x, y);
    					break;
    				default:
    					
    				}
        		}
        	);
        
        this.root = new VBox();
        VBox innerBox = new VBox();
        innerBox.getChildren().addAll(resetBox, canvas);
        innerBox.setPadding(new Insets(10, 10, 0, 10));
        innerBox.setSpacing(10);
        this.root.getChildren().addAll(menuBar, innerBox);
        this.root.setStyle("-fx-background-color: #bfbfbf;");
		
		for (int i = 0; i < ybombs; ++i)
        {
        	for (int j = 0; j < xbombs; ++j)
        	{
        		drawTile(j, i, new Tile());
        	}
        }
		
		resetFace.setStyle("-fx-background-image: url(/saper/resources/normal-face.png);"
        		+ "-fx-background-size: cover;");
		drawTimer(0);
		
		Scene scene = new Scene(this.root, width + 2 * 10, height + 50 + 2 * 10 + 2 * 10);
        primaryStage.setScene(scene);
	}
	
	public void drawGameOver()
	{
		resetFace.setStyle("-fx-background-image: url(/saper/resources/dead-face.png); -fx-background-size: cover;");
	}
	
	public void drawVictory()
	{
		resetFace.setStyle("-fx-background-image: url(/saper/resources/win-face.png); -fx-background-size: cover;");
	}
	
	public void drawMinesLeftCounter(int minesLeft)
	{
		minesLeftCounter.setText(String.format("%03d", minesLeft));
	}
	
	public void drawTimer(int currTime)
	{
		timer.setText(String.format("%03d", currTime));
	}
	
	private void drawMenu()
	{
		Menu gameMenu = new Menu("Game");
        
        ToggleGroup diffToggle = new ToggleGroup();
        
        RadioMenuItem easy = new RadioMenuItem("Easy");
        RadioMenuItem medium = new RadioMenuItem("Medium");
        RadioMenuItem hard = new RadioMenuItem("Hard");
        
        easy.setOnAction(e -> controller.setDifficulty(DifficultyLevel.EASY));
        medium.setOnAction(e -> controller.setDifficulty(DifficultyLevel.MEDIUM));
        hard.setOnAction(e -> controller.setDifficulty(DifficultyLevel.HARD));
        
        easy.setSelected(true);
        
        easy.setToggleGroup(diffToggle);
        medium.setToggleGroup(diffToggle);
        hard.setToggleGroup(diffToggle);
        
        
        MenuItem customSettings = new MenuItem("Custom Settings");
        customSettings.setOnAction( e -> drawCustomSettingsModal() );
        gameMenu.getItems().addAll(easy, medium, hard, new SeparatorMenuItem(), customSettings);
        
        menuBar = new MenuBar();
        menuBar.getMenus().addAll(gameMenu);
        menuBar.setStyle("-fx-padding: 0px;");
	}
	
	private void drawMainBar()
	{
		/* Create bar with smiley face and counters */
		String counterStyle = "-fx-background-color: #000000; -fx-text-fill: red;"
        		+ "-fx-font-family: \"digital-7 mono\"; -fx-font-size: 32;";
		
        minesLeftCounter = new Label("000");
        minesLeftCounter.setMinHeight(30);
        minesLeftCounter.setMinWidth(45);
        minesLeftCounter.setStyle(counterStyle);
        
        
        Region firstReg = new Region();
        firstReg.setPrefWidth(113);
        HBox.setHgrow(firstReg, Priority.ALWAYS);
        Region secondReg = new Region();
        HBox.setHgrow(secondReg, Priority.ALWAYS);
        secondReg.setPrefWidth(113);
        
        timer = new Label("000");
        timer.setMinHeight(30);
        timer.setMinWidth(45);
        timer.setStyle(counterStyle);
        
        resetFace = new Button();
        resetFace.setMinHeight(30);
        resetFace.setMinWidth(30);
        
        resetBox = new HBox();

        resetBox.getChildren().addAll(minesLeftCounter, firstReg, 
        							  resetFace, secondReg, timer);
        resetBox.setStyle("-fx-padding: 0px; -fx-margin: 0px;");
        
        resetFace.setOnMouseClicked(
        		new EventHandler<MouseEvent>()
        		{
        			public void handle(MouseEvent e)
        			{
        				controller.reset();
        			}
        		}
        		);
	}
	
	private void drawCustomSettingsModal()
	{
		Stage modal = new Stage();
		
		modal.initModality(Modality.APPLICATION_MODAL);
		modal.setTitle("Custom Settings");
		modal.setWidth(300);
		
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(5);
		grid.setHgap(5);
		
		
		Label messageBox = new Label();
		messageBox.setStyle("-fx-text-fill: red;");

		Label mapHeight = new Label("Height");
		TextField heightInput = new TextField();
		heightInput.setPrefColumnCount(3);
		
		Label mapWidth = new Label("Width");
		TextField widthInput = new TextField();
		widthInput.setPrefColumnCount(3);
		
		Label bombNo = new Label("Bomb number");
		TextField bombNoInput = new TextField();
		bombNoInput.setPrefColumnCount(3);
		
		Button applyButton = new Button("Apply");
		
		applyButton.setOnAction(e -> 
		{
			try
			{
				controller.changeDifficulty(
						Integer.parseInt(widthInput.getText()),
						Integer.parseInt(heightInput.getText()),
						Integer.parseInt(bombNoInput.getText()));
			}
			catch (NumberFormatException ex)
			{
				messageBox.setText("Please enter numeric values");
			}
			catch (IllegalArgumentException ex)
			{
				messageBox.setText("There cannot be more bombs than tiles");
			}
		});
		
		GridPane.setColumnSpan(messageBox, 3);
		GridPane.setConstraints(messageBox, 0, 0);
		
		GridPane.setConstraints(mapWidth, 0, 1);
		GridPane.setConstraints(widthInput, 2, 1);
		
		GridPane.setConstraints(mapHeight, 0, 2);
		GridPane.setConstraints(heightInput, 2, 2);
		
		GridPane.setConstraints(bombNo, 0, 3);
		GridPane.setConstraints(bombNoInput, 2, 3);
		
		GridPane.setConstraints(applyButton, 1, 4);
		
		grid.getChildren().addAll(messageBox, mapHeight, heightInput, mapWidth, widthInput, bombNo, bombNoInput, applyButton);
		Scene scene = new Scene(grid);
		
		modal.setScene(scene);
		modal.show();
	}
	
	public void drawScoreBoard(ScoreList sList, int newPlace)
	{
		Stage scoreBoard = new Stage();
		
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(5);
		grid.setHgap(35);
		
		Label placeLabel = new Label("Place");
		Label nameLabel = new Label("Name");
		Label timeLabel = new Label("Time");
		Label dateLabel = new Label("Date");
		
		GridPane.setConstraints(placeLabel, 0, 0);
		GridPane.setConstraints(nameLabel, 1, 0);
		GridPane.setConstraints(timeLabel, 2, 0);
		GridPane.setConstraints(dateLabel, 3, 0);
		
		grid.getChildren().addAll(placeLabel, nameLabel, timeLabel, dateLabel);
		
		Node name;
		TextField submitName = new TextField();
		submitName.setMaxWidth(120);
		
		for (int i = 0; i < sList.size(); ++i)
		{
			Label place = new Label(String.valueOf(i + 1));
			if (newPlace == i+1)
			{
				name = submitName;
			}
			else
			{
				name = new Label(sList.get(i).name);
			}
			Label time = new Label(String.valueOf(sList.get(i).time));
			Label date = new Label(new SimpleDateFormat("dd/MM/yyyy").format(sList.get(i).date));
			
			GridPane.setConstraints(place, 0, i+1);
			GridPane.setConstraints(name, 1, i+1);
			GridPane.setConstraints(time, 2, i+1);
			GridPane.setConstraints(date, 3, i+1);
			
			grid.getChildren().addAll(place, name, time, date);
		}
		
		Button okButton = new Button("Ok");
		
		okButton.setOnAction(e -> 
		{
			sList.get(newPlace-1).name = submitName.getText();
			scoreBoard.close();
		});
		
		GridPane.setConstraints(okButton, 0, sList.size() + 1);
		GridPane.setColumnSpan(okButton, 4);
		GridPane.setHalignment(okButton, HPos.CENTER);
		grid.getChildren().add(okButton);
		Scene scene = new Scene(grid);
		
		scoreBoard.setScene(scene);
		scoreBoard.show();
	}
}
