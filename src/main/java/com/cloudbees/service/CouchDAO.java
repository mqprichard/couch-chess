package com.cloudbees.service;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.UpdateConflictException;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.support.CouchDbRepositorySupport;

import com.cloudbees.model.Game;
import com.cloudbees.model.Move;
import com.google.gson.stream.JsonWriter;

public class CouchDAO {
	protected String envCouchURI = "";
	//protected String strURI = "http://localhost:5984";
	protected String strURI = "http://mqprichard.cloudbees.cloudant.com/chess/";
	protected String envCouchUsername = "";
	protected String envCouchPassword = "";
	protected String envCouchDatabase = "chess";

	protected HttpClient httpClient;
	protected CouchDbInstance dbInstance;
	protected CouchDbConnector db; 
	protected GameRepository games = null;
	protected MoveRepository moves = null;
	
	protected class GameRepository extends CouchDbRepositorySupport<Game> {
	    public GameRepository(CouchDbConnector db) {
	        super(Game.class, db);
	    }
	}

	protected class MoveRepository extends CouchDbRepositorySupport<Move> {
	    public MoveRepository(CouchDbConnector db) {
	        super(Move.class, db);
	    }
	          
		// "by_game": { "map": "function(doc) { if (doc.game) emit(doc.game, [doc.white,doc.black,doc.move]); }" }
	    public List<Move> findByGame(String gameId) {
	        return queryView("by_game", gameId);
	    }
	}	
	
	public CouchDAO() {
		try {
			// Get CouchDB connection params from system environment
			envCouchURI = System.getenv( "couchchess_couchURI" );
			envCouchUsername = System.getenv( "couchchess_Username" );
			envCouchPassword = System.getenv( "couchchess_Password" );
			envCouchDatabase = System.getenv( "couchchess_Database" );
			
			// Use System properties if system environment not set
			if ( envCouchURI == null ) {
				envCouchURI = System.getProperty( "couchchess_couchURI" );
				envCouchUsername = System.getProperty( "couchchess_Username" );
				envCouchPassword = System.getProperty( "couchchess_Password" );
				envCouchDatabase = System.getProperty( "couchchess_Database" );
			}
			
			if ( ! (envCouchURI == null) ) {
				strURI = envCouchURI;
			}
			else
				System.out.println( "CouchURI system environment not set - " 
									+ "Using default: " + strURI);

			httpClient = new StdHttpClient.Builder()
										  .url(strURI)
										  .username(envCouchUsername)
										  .password(envCouchPassword)
										  .maxConnections(100)
										  .connectionTimeout(10000)
										  .socketTimeout(20000)
										  .build();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public CouchDbConnector getCouchDB() {
		dbInstance = new StdCouchDbInstance(httpClient);
		db = new StdCouchDbConnector(envCouchDatabase, dbInstance);
		return db;
	}
	
	public GameRepository getGameRepository() {
		games = new GameRepository(getCouchDB());
		return games;
	}

	public MoveRepository getMoveRepository() {
		moves = new MoveRepository(getCouchDB());
		return moves;
	}
	
	public Game getGame( String idString ) {
		Game game = new Game();

		try {
			game = getGameRepository().get( idString );
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw e;
		}
		catch (DocumentNotFoundException e) {
			e.printStackTrace();
			throw e;
		}

		return game;		
	}
	
	public String newGame( Game game ) {
		
		try {
			CouchDbConnector couch = getCouchDB();
			couch.create(game);
		}
		catch (UpdateConflictException e) {
			e.printStackTrace();
			throw e;
		}

		return game.getId();
	}
	
	public Game updateBlackToMove( String idString ) {
		Game game = null;

		try {
			game = getGameRepository().get( idString );
			game.setNext("B");
			getGameRepository().update(game);
		}
		catch (DocumentNotFoundException e) {
			e.printStackTrace();
			throw e;
		}

		return game;
	}
	
	public Game updateWhiteToMove( String idString ) {
		Game game = null;
		long move = 0L;
		
		try {
			game = getGameRepository().get( idString );
			move = game.getMove();
			game.setNext("W");
			game.setMove(move + 1);
			getGameRepository().update(game);
		}
		catch (DocumentNotFoundException e) {
			e.printStackTrace();
			throw e;
		}
		
		return game;
	}
	
	public String getNext( String idString ) {
		Game game = null;

		try {
			game = getGameRepository().get( idString );
		}
		catch (DocumentNotFoundException e) {
			e.printStackTrace();
			throw e;
		}

		return game.getNext();
	}
	
	public long getMoveNo( String idString ) {
		Game game = null;

		try {
			game = getGameRepository().get( idString );
		}
		catch (DocumentNotFoundException e) {
			e.printStackTrace();
			throw e;
		}
		
		return game.getMove();
	}
	
	public String getMoves( String idString ) {
		String strMoves = "";
		StringWriter sw = new StringWriter();
		JsonWriter writer = new JsonWriter(sw);
		
		try {
			List<Move> moveList = getMoveRepository().findByGame( idString );
			Iterator<Move> moves = moveList.iterator();
			
			writer.beginArray();
			while (moves.hasNext()) {
				Move move = moves.next();
				writer.beginObject();
				writer.name("move").value(move.getMove());
				if (!move.getWhite().isEmpty())
					writer.name("white").value(move.getWhite());
				else
					writer.name("black").value(move.getBlack());
				writer.endObject();
			}
			writer.endArray();
			writer.close();
			strMoves = sw.toString();
		}
		catch (IOException e) {
			e.printStackTrace();
			return (strMoves = "");
		}
		catch( DocumentNotFoundException e) {
			e.printStackTrace();
			throw e;
		}

		return strMoves;
	}

	public String newMove( Move move ) {
		
		try {
			CouchDbConnector couch = getCouchDB();
			couch.create(move);
		}
		catch (UpdateConflictException e) {
			e.printStackTrace();
			throw e;
		}

		return move.getId();
	}
}
