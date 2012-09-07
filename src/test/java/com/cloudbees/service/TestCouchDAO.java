package com.cloudbees.service;

import static org.junit.Assert.*;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import com.cloudbees.model.Game;
import com.cloudbees.model.Move;

public class TestCouchDAO {
	private CouchDAO dao = new CouchDAO();
	
	private String testWhite = "Fischer";
	private String testBlack = "Spassky";
	private String testDescription = "Reykjavik Game 1";
	private String testResult = "";
	private String testNext = "W";
	private long testMove = 1L;
	private String testId = "";
	private String moveId = "";
	private String testWhiteMove = "e2-e4";
	private String testBlackMove = "e7-e5";

	@Test
	public void testGame() {
		try {
			// Create new game
			Game game = new Game( testWhite, testBlack, testDescription);
			
			testId  = dao.newGame(game);
			assertFalse( testId.isEmpty() );
		
			// Get the game object back from MongoDB
			Game result = dao.getGame( testId );
	
			assertEquals(result.getWhite(), testWhite);
			assertEquals(result.getBlack(), testBlack);
			assertEquals(result.getDescription(), testDescription);
			assertEquals(result.getResult(), testResult);
			assertEquals(result.getNext(), testNext);
			assertEquals(result.getMove(), testMove);		
			
			// Set game state: Black to move
			dao.updateBlackToMove( testId );
			
			// Get the game object back from MongoDB
			result = dao.getGame( testId );
	
			assertEquals(result.getNext(), "B");
			assertEquals(result.getMove(), 1L);
			assertEquals( dao.getNext( testId ), "B" );
			assertEquals( dao.getMoveNo( testId ), 1L );			
			
			// Set game state: White to move
			dao.updateWhiteToMove( testId );
			
			// Get the game object back from MongoDB
			result = dao.getGame( testId );
	
			assertEquals( result.getNext(), "W" );
			assertEquals( result.getMove(), 2L );
			assertEquals( dao.getNext( testId ), "W" );
			assertEquals( dao.getMoveNo( testId ), 2L );
		}	
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		finally {
		}
	}

	@Test
	public void testMoves() {
		try {
			// Create new game
			Game game = new Game( testWhite, testBlack, testDescription);
			testId = dao.newGame(game);
			assertFalse( testId.isEmpty() );
		
			// White move
			Move move = new Move( testWhiteMove, "", 1L, testId );
			moveId  = dao.newMove( move );
			assertFalse( moveId.isEmpty() );
			
			// Black move
			move = new Move( "", testBlackMove, 1L, testId);
			moveId  = dao.newMove( move );
			assertFalse( testId.isEmpty() );
			
			// Get array of moves for game
			String result = dao.getMoves( testId );
			String response = "{result:" + result.toString() + "}";
			JSONObject jObject = new JSONObject(response);
			JSONArray jsonMoves = jObject.getJSONArray("result");
			assertEquals( jsonMoves.length(), 2);
			
			// Validate array elements for both moves
			JSONObject first = jsonMoves.getJSONObject( 0 );
			assertEquals( first.getString( "white"), testWhiteMove );		
			JSONObject second = jsonMoves.getJSONObject( 1 );
			assertEquals( second.getString( "black"), testBlackMove );	
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		finally {
		}		
	}

}
