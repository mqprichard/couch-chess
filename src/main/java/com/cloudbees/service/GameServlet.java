package com.cloudbees.service;

import java.io.StringWriter;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.ektorp.DocumentNotFoundException;

import com.cloudbees.model.Game;
import com.google.gson.stream.JsonWriter;

@Path("/game")
public class GameServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private CouchDAO dao = new CouchDAO();
	
	@GET
    @Path("{id}")	
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGame(@PathParam("id") String id ) {
		
		StatusType statusCode = null;
		Game game = null;
		String msg = null;
		
		StringWriter sw = new StringWriter();
		JsonWriter writer = new JsonWriter(sw);
		
		try {
			game = dao.getGame( id );
			
			if (game != null ) {
				writer.beginObject();
				writer.name("white").value( game.getWhite());
				writer.name("black").value(game.getBlack());
				writer.name("description").value(game.getDescription());
				writer.name("result").value(game.getResult());
				writer.name("next").value(game.getNext());
				writer.name("move").value(game.getMove());
				writer.endObject();
				writer.close();

				statusCode = Response.Status.OK;
				msg = sw.toString();
			}
			else
				// IllegalArgumentException/NullPointerException
				statusCode = Response.Status.BAD_REQUEST;
		}
		catch (DocumentNotFoundException e) {
			statusCode = Response.Status.BAD_REQUEST;
		}
		catch (IllegalArgumentException e){
			statusCode = Response.Status.BAD_REQUEST;
		}
		catch (Exception e) {
			e.printStackTrace();

			// Others: Return 500 Internal Server Error
    		statusCode = Response.Status.INTERNAL_SERVER_ERROR;			
		}
		finally {
		}

		if (statusCode != Response.Status.OK)
			return Response.status(statusCode).build();
		else
			return Response.status(statusCode).entity(msg).build();		
	}
	
	@POST
    @Path("new")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response newGame(Game game) {
		
		StatusType statusCode = null;
		String msg = null;
		StringWriter sw = new StringWriter();
		JsonWriter writer = new JsonWriter(sw);

		try {			
		    // Create a new game (key = game id)
			String id = dao.newGame( game );
			if (id == null) {
    			// Return 400 Bad Request
	    		statusCode = Response.Status.BAD_REQUEST;
			} else {	
				writer.beginObject();
				writer.name("id").value( id );
				writer.endObject();
				writer.close();

				statusCode = Response.Status.OK;
				msg = sw.toString();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();

			// Return 500 Internal Server Error
    		statusCode = Response.Status.INTERNAL_SERVER_ERROR;
		}
		finally {
		}

		if (statusCode != Response.Status.OK)
			return Response.status(statusCode).build();
		else
			return Response.status(statusCode).entity(msg).build();	
	}
	
	public CouchDAO getDao() {
		return dao;
	}
	public void setDao(CouchDAO dao) {
		this.dao = dao;
	}
}
