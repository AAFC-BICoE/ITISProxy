package ca.gc.agr.mbb.itisproxy.entities;



import com.fasterxml.jackson.annotation.JsonProperty; 

public class Comment{

    @JsonProperty("class")
    public String _class;

    @JsonProperty("commentDetail")
    public String commentDetail;

    @JsonProperty("commentId")
    public String commentId;

    @JsonProperty("commentTimeStamp")
    public String commentTimeStamp;

    @JsonProperty("commentator")
    public String commentator;

    @JsonProperty("updateDate")
    public String updateDate;


    public String toString(){
	return commentId + ":" + commentator + ":" + commentDetail;
    }
}
