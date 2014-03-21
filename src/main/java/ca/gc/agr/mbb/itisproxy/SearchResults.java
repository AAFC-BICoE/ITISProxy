package ca.gc.agr.mbb.itisproxy;

import ca.gc.agr.itis.itismodel.ItisRecord;
import java.util.List;

public class SearchResults{
    public List<ItisRecord> records = null;
    public int totalResults = 0;
    public int start = 0;
    public int end = 0;
    public boolean searchResultsTooLarge = false;

    public SearchResults(){

    }

    public SearchResults(final SearchResults sr){
	this.records = sr.records;
	this.totalResults = sr.totalResults;
	this.start = sr.start;
	this.end = sr.end;
    }

}
