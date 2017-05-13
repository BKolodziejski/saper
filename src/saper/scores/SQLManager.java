package saper.scores;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import saper.DifficultyLevel;

public class SQLManager {
	private static Connection con;
	private static Statement statement;
	
	public static void init()
	{
		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:scores.db");
			
			  statement = con.createStatement();
		      statement.setQueryTimeout(30);  // set timeout to 30 sec.

		      statement.executeUpdate("CREATE TABLE IF NOT EXISTS Difficulty ("
		      		+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
		      		+ "width INTEGER NOT NULL,"
		      		+ "height INTEGER NOT NULL,"
		      		+ "bomb_no INTEGER NOT NULL);"
		      		);
		      statement.executeUpdate("CREATE TABLE IF NOT EXISTS Scores ("
		      		+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
		      		+ "name VARCHAR(10),"
		      		+ "time INTEGER NOT NULL,"
		      		+ "date DATE NOT NULL,"
		      		+ "Difficulty_id INTEGER NOT NULL,"
		      		+ "FOREIGN KEY (Difficulty_id) REFERENCES Difficulty(id));");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	}
	
	public static void loadScores(DifficultyLevel diff)
	{
		int diffId = getDiffId(diff);
		
		ResultSet rs;
		try {
			rs = statement.executeQuery(
					String.format(
							"SELECT name, time, date FROM Scores WHERE Difficulty_id=%d",
							diffId
							)
					);
			
			while(rs.next())
			{
				diff.getScoreList().addScore(new Score(rs.getString(1), rs.getInt(2),
						new SimpleDateFormat("dd/MM/yyyy").parse(rs.getString(3))));
			}
		} catch (SQLException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveScores(DifficultyLevel diff)
	{
		if (diff.getScoreList().size() == 0)
			return;
		
		int diffId = getDiffId(diff);
		
		try {
			statement.executeUpdate(String.format(
					"DELETE FROM Scores WHERE Difficulty_id=%d;", diffId
					));
			
			String insertQuery = "INSERT INTO Scores ('name', 'time', 'date', 'Difficulty_id')"
					+ "VALUES ";
			ScoreList scores = diff.getScoreList();
			Score sc;
			for (int i = 0; i < scores.size(); ++i)
			{
				sc = scores.get(i);
				insertQuery += String.format("('%s', %d, '%s', %d),", sc.name, sc.time,
						new SimpleDateFormat("dd/MM/yyyy").format(sc.date), diffId);
			}
			insertQuery = insertQuery.substring(0 ,insertQuery.length() - 1) + ";";
			
			statement.executeUpdate(insertQuery);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static int getDiffId(DifficultyLevel diff)
	{
		String diffIdQuery = String.format(
				"SELECT id FROM Difficulty WHERE width=%d AND height=%d;",
				diff.X_TILES,
				diff.Y_TILES);
		
		ResultSet rs;
		try {
			rs = statement.executeQuery(diffIdQuery);
			if ( !rs.next() )
			{
				statement.executeUpdate(
						String.format(
						"INSERT INTO Difficulty"
						+ "('width', 'height', 'bomb_no') values"
						+ "(%d, %d, %d);"
						, diff.X_TILES, diff.Y_TILES, diff.BOMB_NO
						));
				
				rs = statement.executeQuery(diffIdQuery);
				rs.next();
			}
			
			return rs.getInt("id");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
	}
}
