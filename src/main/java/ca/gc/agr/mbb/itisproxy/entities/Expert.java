package ca.gc.agr.mbb.itisproxy.entities;



import com.fasterxml.jackson.annotation.JsonProperty; 
import java.util.ArrayList;

public class Expert{

    @JsonProperty("class")
    public String _class;

    @JsonProperty("comment")
    public String comment;

    @JsonProperty("expert")
    public String expert;

    @JsonProperty("referenceFor")
    public ArrayList<ReferenceFor>referenceFor;

    @JsonProperty("updateDate")
    public String updateDate;


    public String toString(){
	return 
	    expert + ":"
	    + comment;
    }



}
