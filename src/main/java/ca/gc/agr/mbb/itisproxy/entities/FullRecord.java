package ca.gc.agr.mbb.itisproxy.entities;

import com.fasterxml.jackson.annotation.JsonProperty; 

import java.util.ArrayList;

public class FullRecord extends BaseTsn{
    @JsonProperty("acceptedNameList")
    public AcceptedNamesList acceptedNamesList;

    @JsonProperty("commentList")
    public CommentList commentList;

    @JsonProperty("commonNameList")
    public CommonNameList commonNameList;

    @JsonProperty("completenessRating")
    public CompletenessRating completenessRating;

    @JsonProperty("coreMetadata")
    public CoreMetadata coreMetadata;

    @JsonProperty("credibilityRating")
    public CredibilityRating credibilityRating;

    @JsonProperty("currencyRating")
    public CurrencyRating currencyRating;

    @JsonProperty("dateData")
    public DateData dateData;

    @JsonProperty("expertList")
    public ExpertList expertList;

    @JsonProperty("geographicDivisionList")
    public GeographicDivisionsList geographicDivisionList;

    @JsonProperty("hierarchyUp")
    public HierarchyUp hierarchyUp;

    @JsonProperty("jurisdictionalOriginList")
    public JurisdictionalOriginsList jurisdictionalOriginList;

    @JsonProperty("kingdom")
    public Kingdom kingdom;

    @JsonProperty("otherSourceList")
    public OtherSourceList otherSourceList;

    @JsonProperty("parentTSN")
    public ParentTsn parentTsn;

    @JsonProperty("publicationList")
    public PublicationList publicationList;

    @JsonProperty("scientificName")
    public ScientificName scientificName;

    @JsonProperty("synonymList")
    public SynonymList synonymList;

    @JsonProperty("taxRank")
    public TaxRank taxRank;

    @JsonProperty("taxonAuthor")
    public TaxonAuthor taxonAuthor;

    @JsonProperty("unacceptReason")
    public UnacceptReason unacceptReason;

    @JsonProperty("usage")
    public Usage usage;

    public String toString(){
	return " "
	    + "tsn=" + tsn
	    + "\n\t\t"
	    + "acceptedNamesList=" + acceptedNamesList
	    + "\n\t\t"
	    + "commentList=" + commentList
	    + "\n\t\t"
	    + "commonNameList=" + commonNameList
	    + "\n\t\t"
	    + "completenessRating=" + completenessRating
	    + "\n\t\t"
	    + "coreMetadata=" + coreMetadata
	    + "\n\t\t"
	    + "credibilityRating=" + credibilityRating
	    + "\n\t\t"

	    + "currencyRating=" + currencyRating
	    + "\n\t\t"

	    + "dateData=" + dateData 
	    + "\n\t\t"

	    + "ExpertList=" + expertList
	    + "\n\t\t"

	    + "geographicDivisionList=" + geographicDivisionList
	    + "\n\t\t"

	    + " geographicDivisionList=" +  geographicDivisionList
	    + "\n\t\t"

	    + "hierarchyUp=" + hierarchyUp
	    + "\n\t\t"
	    + "jurisdictionalOriginList=" + jurisdictionalOriginList
	    + "\n\t\t"
	    + "kingdom=" + kingdom
	    + "\n\t\t"
	    + "otherSourceList=" + otherSourceList
	    + "\n\t\t"
	    + "parentTsn=" + parentTsn
	    + "\n\t\t"
	    + "publicationList=" + publicationList
	    + "\n\t\t"
	    + "scientificName=" + scientificName
	    + "\n\t\t"
	    + "synonymList=" + synonymList
	    + "\n\t\t"
	    + "taxRank=" + taxRank
	    + "\n\t\t"
	    + "taxonAuthor=" + taxonAuthor
	    + "\n\t\t"
	    + "unacceptReason=" + unacceptReason
	    + "\n\t\t"
	    + "usage=" + usage
	    + "\n\t\t"
	    ;
	    }

    public FullRecord(){
	acceptedNamesList = new AcceptedNamesList(); 
	commentList = new CommentList(); 
	commonNameList = new CommonNameList(); 
	completenessRating = new CompletenessRating(); 
	coreMetadata = new CoreMetadata(); 
	currencyRating = new CurrencyRating(); 
	credibilityRating = new CredibilityRating(); 
	dateData = new DateData(); 
	expertList = new ExpertList(); 
	geographicDivisionList = new GeographicDivisionsList(); 
	hierarchyUp = new HierarchyUp(); 
	jurisdictionalOriginList = new JurisdictionalOriginsList(); 
	kingdom = new Kingdom(); 
	otherSourceList = new OtherSourceList(); 
	parentTsn = new ParentTsn(); 
	publicationList = new PublicationList(); 
	scientificName = new ScientificName(); 
	synonymList = new SynonymList(); 
	taxRank = new TaxRank(); 
	taxonAuthor = new TaxonAuthor(); 
	unacceptReason = new UnacceptReason(); 
	usage = new Usage(); 

    }
}
