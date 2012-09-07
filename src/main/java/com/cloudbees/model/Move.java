package com.cloudbees.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.*;
import org.ektorp.support.CouchDbDocument;

@JsonIgnoreProperties({"id", "revision"})
@XmlRootElement
public class Move extends CouchDbDocument {
	private static final long serialVersionUID = 1L;

	protected String game;
	protected String white;
	protected String black;
	protected long move;
	
    private String id;
    private String revision;

	public Move() {
		this.move = 0;
		this.game = "";
		this.white = "";
		this.black = "";
	}
	
	public Move(String white, String black, long move, String game){
		this.move = move;
		this.game = game;
		this.white = white;
		this.black = black;
	}

	public long getMove() {
		return move;
	}

	public void setMove(long move) {
		this.move = move;
	}

	public String getGame() {
		return game;
	}

	public void setGame(String game) {
		this.game = game;
	}

	public String getWhite() {
		return white;
	}

	public void setWhite(String white) {
		this.white = white;
	}

	public String getBlack() {
		return black;
	}

	public void setBlack(String black) {
		this.black = black;
	}
	
	@JsonProperty("_id")
	public String getId() {
		return id;
	}
	
	@JsonProperty("_id")
	public void setId(String id) {
		this.id = id;
	}
	
	@JsonProperty("_rev")
	public String getRevision() {
		return revision;
	}

	@JsonProperty("_rev")
	public void setRevision(String revision) {
		this.revision = revision;
	}

	public boolean isLegal(String move) {
		// if move [1] == a-h
		// and move [2] == 1-8
		// and move [3] == - or x
		// and move [4] == a-h
		// and move [5] == 1-8
		return true;
	}
}
