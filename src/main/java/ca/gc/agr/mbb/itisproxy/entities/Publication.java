package ca.gc.agr.mbb.itisproxy.entities;


import com.fasterxml.jackson.annotation.JsonProperty; 
import java.util.ArrayList;

public class Publication extends BaseClass
{
    @JsonProperty("actualPubDate")
    public String actualPubDate;

    @JsonProperty("isbn")
    public String isbn;

    @JsonProperty("issn")
    public String issn;

    @JsonProperty("listedPubDate")
    public String listedPubDate;

    @JsonProperty("pages")
    public String pages;

    @JsonProperty("pubComment")
    public String pubComment;

    @JsonProperty("pubName")
    public String pubName;

    @JsonProperty("pubPlace")
    public String pubPlace;

    @JsonProperty("publisher")
    public String publisher;

    @JsonProperty("referenceAuthor")
    public String referenceAuthor;

    @JsonProperty("referenceFor")
    public ArrayList<ReferenceFor>referenceFor;

    @JsonProperty("title")
    public String title;

    @JsonProperty("updateDate")
    public String updateDate;

    public String toString(){
	return 
	    "actualPubDate=" + actualPubDate + "; "
	    + "isbn=" + isbn + "; "
	    + "issn=" + issn + "; "
	    + "listedPubDate=" + listedPubDate + "; "
	    + "pages=" + pages + "; "
	    + "pubComment=" + pubComment + "; "
	    + "pubName=" + pubName + "; "
	    + "pubPlace=" + pubPlace + "; "
	    + "publisher=" + publisher + "; "
	    + "referenceAuthor=" + referenceAuthor + "; "
	    + "title=" + title + "; ";
    }
}
