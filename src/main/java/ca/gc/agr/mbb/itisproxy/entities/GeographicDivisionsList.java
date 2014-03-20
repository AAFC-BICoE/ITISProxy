package ca.gc.agr.mbb.itisproxy.entities;



import com.fasterxml.jackson.annotation.JsonProperty; 
import java.util.ArrayList;

public class GeographicDivisionsList extends BaseTsn{
    @JsonProperty("geoDivisions")
    public ArrayList<GeoDivision> geoDivisions;


    public String toString(){
	StringBuilder sb = new StringBuilder();
	if(geoDivisions != null){
	    for(GeoDivision gd: geoDivisions){
		sb.append(gd + "; ");
	    }
	}
	return sb.toString();
    }
}
