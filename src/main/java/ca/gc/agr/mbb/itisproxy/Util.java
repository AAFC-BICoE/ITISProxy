package ca.gc.agr.mbb.itisproxy;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import ca.gc.agr.itis.itismodel.ItisRecord;
import ca.gc.agr.itis.itismodel.TaxonomicRank;
import ca.gc.agr.itis.itismodel.TaxonComment;
import ca.gc.agr.itis.itismodel.TaxonExpert;
import ca.gc.agr.itis.itismodel.TaxonOtherSource;
import ca.gc.agr.itis.itismodel.TaxonPublication;
import ca.gc.agr.itis.itismodel.TaxonJurisdictionalOrigin;

public class Util{
    private Util(){

    }


	
    public static final void checkString(final String s, final String mes) throws IllegalArgumentException{
	if(s==null){
	    throw new IllegalArgumentException("String cannot be null " + mes);
	}

	if(s.length() <1){
	    throw new IllegalArgumentException("String cannot be empty " + mes);
	}
    }


    public static final void checkStringIsPositiveInteger(final String s) throws IllegalArgumentException{
	if(s == null){
	    throw new IllegalArgumentException("String needs to be not null");
	}
	try{
	    int tmp = Integer.parseInt(s);
	    if(tmp <= 0){
		throw new IllegalArgumentException("String needs to be a positive int");
	    }
	}catch(NumberFormatException nfe){
	    throw new IllegalArgumentException("String needs to be an int: [" + s + "]");
	}
    }

    public static final void checkRange(final int start, final int end, String mes) throws IllegalArgumentException{
	if(mes == null){
	    mes = "";
	}
	if(start < 0){
	    throw new IllegalArgumentException("Start cannot be < 0. " + mes);
	}

	if(end <= start){
	    throw new IllegalArgumentException("End cannot be <= Start. " + mes);
	}
    }

    static final char[] ch = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
    public static final String[] makeWholeAlphabetArray(){
	List<String> l = new ArrayList<String>(ch.length*ch.length);

	for(int i=0; i<ch.length; i++){
	    for(int j=0; j<ch.length; j++){
		StringBuilder sb = new StringBuilder();
		sb.append(ch[i]);
		sb.append(ch[j]);
		l.add(sb.toString());
	    }
	}
	return l.toArray(new String[0]);
    }

    public static final String makeWholeAlphabet(){
	StringBuilder sb = new StringBuilder(ch.length*ch.length*3);
	for(int i=0; i<ch.length; i++){
	    for(int j=0; j<ch.length; j++){
		if(i != 0 || j != 0){
		    sb.append(CachingProxyImpl.WARM_CACHE_QUERIES_SEPARATOR);
		}
		sb.append(ch[i]);
		sb.append(ch[j]);
	    }
	}
	return sb.toString();
    }

    public static String toString(ItisRecord rec){
	StringBuilder sb = new StringBuilder();
	app(sb, 0, "TSN", rec.getTsn());
	app(sb, 1, "CombinedName", rec.getCombinedName());
	app(sb, 1, "NameAuthor", rec.getNameAuthor());
	app(sb, 1, "RankSynonyms", rec.getRankSynomyms());
	    
	Map<String, List<String>> verns = rec.getVernacularNames();
	for (Map.Entry<String, List<String>> entry : verns.entrySet()) {
	    String key = entry.getKey();
	    List<String> value = entry.getValue();
	    for(String vern: value){
		app(sb, 1, "Vernacular", key + ":" + vern);
	    }
	}

	app(sb, 1, "GlobalSpecies", rec.getGlobalSpecies());
	app(sb, 1, "Completeness", rec.getCompleteness());
	app(sb, 1, "CurrentTaxonomicStanding", rec.getCurrentTaxonomicStanding());
	app(sb, 1, "RecordCredibilityRating", rec.getRecordCredibilityRating());

	List<TaxonomicRank> ranksAbove = rec.getTaxonomicHierarchy();
	int indent = 0;
	for(TaxonomicRank r: ranksAbove){
	    app(sb, indent++, "<", r.getRankName() + ":" + r.getRankValue() 
		+ ": [vernaculars: " + mapStringListStringToString(r.getCommonNames()) + "] ");
	}
	
	++indent;
	List<TaxonomicRank> ranksBelow = rec.getBelowSpeciesRanks();
	for(TaxonomicRank r: ranksBelow){
	    app(sb, indent, ">", r.getRankName() + ":" + r.getRankValue()
		+ ": [vernaculars: " + mapStringListStringToString(r.getCommonNames()) + "] ");
	}

	app(sb, 1, "Experts", rec.getExperts());
	app(sb, 1, "OtherSources", rec.getOtherSources());
	app(sb, 1, "References", rec.getReferences());

	app(sb, 1, "GeographicInfo", rec.getGeographicInfo());

	app(sb, 1, "Comments", rec.getComments());

	app(sb, 1, "TaxonJurisdictionalOrigins", rec.getTaxonJurisdictionalOrigins());

	return sb.toString();
    }

    public static final void app(StringBuilder sb, int d, String name, List values){
	if(values == null){
	    //app(sb, d, name, "null");
	    return;
	}
	for(Object o: values){
	    if(o instanceof TaxonComment){
		app(sb, d, name, ((TaxonComment)o).getCommentDetail() + "| " + ((TaxonComment)o).getCommentator());
	    }else{
		if(o instanceof TaxonExpert){
		    app(sb, d, name, ((TaxonExpert)o).getExpert() + "| " + ((TaxonExpert)o).getComment());
		}else{
		    if(o instanceof TaxonOtherSource){
			app(sb, d, name, ((TaxonOtherSource)o).getSource() + "| " + ((TaxonOtherSource)o).getSourceType() + "| " + ((TaxonOtherSource)o).getSourceComment());
		    }else{
			if(o instanceof TaxonPublication){
			    app(sb, d, name, ((TaxonPublication)o).getReferenceAuthor() + "| " + ((TaxonPublication)o).getPubYear() + "| " + ((TaxonPublication)o).getPubName() + "| " 
				+ ((TaxonPublication)o).getPublisher() + "| " + ((TaxonPublication)o).getPubPlace() + "| " 
				+ ((TaxonPublication)o).getPages() + "| " + ((TaxonPublication)o).getIsbn() + "| " 
				+ ((TaxonPublication)o).getPubComment());
			}else{
			    if(o instanceof TaxonJurisdictionalOrigin){
				app(sb, d, name, ((TaxonJurisdictionalOrigin)o).getJurisdiction() + "| " + ((TaxonJurisdictionalOrigin)o).getOrigin());
			    }else{			    
				app(sb, d, name, o.toString());
			    }
			}
		    }
		}
	    }
	}
    }

    public static final void app(StringBuilder sb, int d, String name, String value){
	sb.append("\n");
	for(int i=0; i<d; i++){
	    sb.append(" ");
	}
	sb.append(name + ":: " + value);
    }

    public static String mapStringListStringToString(Map<String, List<String>> map){
	StringBuilder sb = new StringBuilder();
	for(String lang: map.keySet()){
	    sb.append("|" + lang + ":");
	    List<String>verns = map.get(lang);
	    for(String vern: verns){
		sb.append(vern + ";");
	    }
	}
	return sb.toString();
    }
}
