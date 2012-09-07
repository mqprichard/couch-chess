package com.cloudbees.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.*;
import org.ektorp.support.CouchDbDocument;
import org.ektorp.support.TypeDiscriminator;

@JsonIgnoreProperties({"id", "revision"})
@XmlRootElement
public class Game extends CouchDbDocument {
	private static final long serialVersionUID = 1L;
	
	protected String white;
	protected String black;
	protected String description;
	protected String next;
	protected long move;
	protected String result;

    private String id;
    private String revision;

	public Game() {
		this.white = "";
		this.black = "";
		this.description = "";
		this.next = "W";
		this.move = 1;
		this.result = "";
	}
	
	public Game(String white, 
				String black, 
				String description) {
		this.white = white;
		this.black = black;
		this.description = description;
		this.next = "W";
		this.move = 1;
		this.result = "";
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	public long getMove() {
		return move;
	}

	public void setMove(long move) {
		this.move = move;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
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
}
