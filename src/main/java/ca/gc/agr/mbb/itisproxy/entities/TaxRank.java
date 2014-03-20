package ca.gc.agr.mbb.itisproxy.entities;


import com.fasterxml.jackson.annotation.JsonProperty; 
import java.util.Map;
import java.util.List;

public class TaxRank extends BaseTsn{
    @JsonProperty("kingdomId")
    public String kingdomId;
    
    @JsonProperty("kingdomName")
    public String kingdomName;
    
    @JsonProperty("rankId")
    public String rankId;
    
    @JsonProperty("rankName")
    public String rankName;

    public String rankValue;

    public Map<String, List<String>> commonNames;

    public String toString(){
	return 
	    "kingdomId=" + kingdomId + "; "
	    + "kingdomName=" + kingdomName + "; "
	    + "rankId=" + rankId + "; "
	    + "rankName=" + rankName + "; ";
    }
}
