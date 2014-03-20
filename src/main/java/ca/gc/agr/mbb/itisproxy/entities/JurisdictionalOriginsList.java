package ca.gc.agr.mbb.itisproxy.entities;

import com.fasterxml.jackson.annotation.JsonProperty; 

import java.util.ArrayList;

public class JurisdictionalOriginsList extends BaseTsn{

    @JsonProperty("jurisdictionalOrigins")
    public ArrayList<JurisdictionalOrigin> jurisdictionalOrigins;

    public String toString(){
	StringBuilder sb = new StringBuilder();
	if(jurisdictionalOrigins != null){
	    for(JurisdictionalOrigin jo: jurisdictionalOrigins){
		sb.append(jo + "; ");
	    }
	}
	return sb.toString();
    }
}
