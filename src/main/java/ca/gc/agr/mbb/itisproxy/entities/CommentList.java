package ca.gc.agr.mbb.itisproxy.entities;

import com.fasterxml.jackson.annotation.JsonProperty; 

import java.util.ArrayList;

public class CommentList{
    
    @JsonProperty("class")
    public String _class;

    @JsonProperty("comments")
    public ArrayList<Comment> comments;

    @JsonProperty("tsn")
    public String tsn;


    public String toString(){
	StringBuilder sb = new StringBuilder();
	if(comments != null){
	    for(Comment com: comments){
		sb.append(com + "; ");
	    }
	}
	return sb.toString();
    }

}
