package ca.gc.agr.mbb.itisproxy.entities;


import com.fasterxml.jackson.annotation.JsonProperty; 
import java.util.ArrayList;

public class OtherSource{
    @JsonProperty("acquisitionDate")
    public String acquisitionDate;

    @JsonProperty("class")
    public String _class;

    @JsonProperty("referenceFor")
    public ArrayList<ReferenceFor> referenceFor;

    @JsonProperty("source")
    public String source;

    @JsonProperty("sourceComment")
    public String sourceComment;

    @JsonProperty("sourceType")
    public String sourceType;

    @JsonProperty("updateDate")
    public String updateDate;

    @JsonProperty("version")
    public String version;

    public String toString(){
	return 
	    source + ":"
	    + sourceComment + ":"
	    + sourceType + ":"
	    + version + ":"
	    + acquisitionDate;
    }
}
