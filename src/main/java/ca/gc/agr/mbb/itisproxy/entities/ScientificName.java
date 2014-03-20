package ca.gc.agr.mbb.itisproxy.entities;


import com.fasterxml.jackson.annotation.JsonProperty; 

public class ScientificName extends BaseTsn{

    @JsonProperty("author")
    public String author;
    @JsonProperty("combinedName")
    public String combinedName;

    @JsonProperty("unitInd1")
    public String unitInd1;

    @JsonProperty("unitInd2")
    public String unitInd2;

    @JsonProperty("unitInd3")
    public String unitInd3;

    @JsonProperty("unitInd4")
    public String unitInd4;

    @JsonProperty("unitName1")
    public String unitName1;

    @JsonProperty("unitName2")
    public String unitName2;

    @JsonProperty("unitName3")
    public String unitName3;

    @JsonProperty("unitName4")
    public String unitName4;

    public String toString(){
	return 
	    "author=" + author + "; "
	    + "combinedName=" + combinedName + "; "
	    + "unitInd1=" + unitInd1 + "; "
	    + "unitInd2=" + unitInd2 + "; "
	    + "unitInd3=" + unitInd3 + "; "
	    + "unitInd4=" + unitInd4 + "; "
	    + "unitName1=" + unitName1 + "; "
	    + "unitName2=" + unitName2 + "; "
	    + "unitName3=" + unitName3 + "; "
	    + "unitName4=" + unitName4 + "; ";

	    }
}
