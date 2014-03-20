package ca.gc.agr.mbb.itisproxy.entities;

import com.fasterxml.jackson.annotation.JsonProperty; 
import java.util.ArrayList;

public class GetFullHierarchyFromTSN extends BaseTsn{

    @JsonProperty("hierarchyList")
    public ArrayList<HierarchyRecord> hierarchyList;    

}
