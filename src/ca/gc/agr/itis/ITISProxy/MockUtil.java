
package ca.gc.agr.itis.ITISProxy;

import ca.gc.agr.itis.ITISModel.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;  

public class MockUtil{

	private MockUtil(){

	}

	// Languages using https://en.wikipedia.org/wiki/ISO_639-2 
	static private String ENG = "eng";
	static private String POR = "por";
	static private String FRA = "fra";    

	// Rank Names
	static final private String RANK_KINGDOM = "Kingdom";
	static final private String RANK_DIVISION = "Division";
	static final private String RANK_SUBDIVISION = "Subdivision";
	static final private String RANK_ORDER = "Order";
	static final private String RANK_FAMILY = "Family";
	static final private String RANK_GENUS = "Genus";
	static final private String RANK_SPECIES = "species";


	// Taxonomic Standing names
	static final private String TAXONOMIC_STANDING_VALID = "standing";
	static final private String TAXONOMIC_STANDING_ACCEPTED = "accepted";

	// Credibility: Completeness names
	static final private String COMPLETENESS_UNVERIFIED = "unverified";
	static final private String COMPLETENESS_UNKNOWN = "unknown";
	static final private String COMPLETENESS_PARTIAL = "partial";
	// Credibility: Global Species names
	static final private String GLOBAL_SPECIES_VERIFIED= "verified - standards met";
	static final private String GLOBAL_SPECIES_UNVERIFIED= "unverified";
	// Credibility: Currancy names
	static final private String CURRENCY_2002= "2002";
	static final private String CURRENCY_2004= "2004";
	static final private String CURRENCY_UNKNOWN= "unknown";



	////////////////////
	static final public String fungiTsn = "555705";
	static final public String ascomycotaTsn = "610624";
	static final public String pezizomycotinaTsn = "610625";
	static final public String lecanoromycetesTsn = "610630";
	static final public String lecanoralesTsn = "14000";
	static final public String teloschistineaeTsn = "191608";
	static final public String teloschistaceaeTsn = "14034";
	static final public String caloplacaTsn = "14035";
	static final public String caloplacaFerrugineaTsn = "191680";
	static private String[] hierarchyToCaloplacaFerruginea = {fungiTsn, pezizomycotinaTsn, lecanoromycetesTsn, lecanoralesTsn, teloschistineaeTsn, teloschistaceaeTsn, caloplacaTsn, caloplacaFerrugineaTsn};

	public static Map<String,String> tsnToNameMap;
	static private Map<String,String> tsnToRankMap;
	static private Map<String,String> tsnToTaxonomicStandingMap;
	static private Map<String,String> tsnToGlobalSpeciesMap;
	static private Map<String,String> tsnToCompletenessMap;
	static private Map<String,String> tsnToCurrencyMap;
	static private Map<String, Map<String, List<String>>> tsnToVernacularMap;




	// Kingdoms
	static private String[] kingdomNames = {"Monera", "Protozoa", "Plantae", "*Fungi", "Animalia", "Chromista"};
	static private String[] kingdomTsns = {"202420", "630577", "202422", fungiTsn, "202423", "630578"};
	static private String[] kingdomRankNames = {RANK_KINGDOM, RANK_KINGDOM,
	                                            RANK_KINGDOM, RANK_KINGDOM,
	                                            RANK_KINGDOM, RANK_KINGDOM};
	static private String[] kingdomTaxonomicStanding = {TAXONOMIC_STANDING_VALID, TAXONOMIC_STANDING_VALID, 
	                                                    TAXONOMIC_STANDING_ACCEPTED, TAXONOMIC_STANDING_VALID, 
	                                                    TAXONOMIC_STANDING_ACCEPTED, TAXONOMIC_STANDING_VALID};
	static private String[] kingdomCompleteness = {COMPLETENESS_UNKNOWN, COMPLETENESS_UNKNOWN, 
	                                               COMPLETENESS_PARTIAL, COMPLETENESS_UNKNOWN, 
	                                               COMPLETENESS_UNKNOWN, COMPLETENESS_UNKNOWN, };


	//#######################################################################################
	//Kingdom Fungi
	//   Divisions
	static private String[] fungalDownOneLevelNames = {"*Ascomycota","Basidiomycota","Deuteromycotina","Myxomycota"};
	static private String[] fungalDownOneLevelTsns = {ascomycotaTsn,"623881","14115","13762"};
	static private String[] fungalDownOneLevelRankNames = {RANK_DIVISION, RANK_DIVISION, RANK_DIVISION, RANK_DIVISION};


	//   Vernacular
	static private String[] fungiVernacularNamesEng = {"fungi"};
	static private String[] fungiVernacularNamesPor = {"Fungo"};
	static private String[] fungiVernacularNamesFra = {"champignons"};



	//#######################################################################################
	//Division: Ascomycota
	static private String[] ascomycotaDownOneLevelNames = {"Coryneliales","Lahmiales","Medeolariales","Mycocaliciales","Ostropales","*Pezizomycotina"};
	static private String[] ascomycotaDownOneLevelTsns = {"612827", "612828","612829", "610647", "14044", pezizomycotinaTsn};
	static private String[] ascomycotaDownOneLevelRankNames = { RANK_ORDER, RANK_ORDER, RANK_ORDER, RANK_ORDER, RANK_ORDER, RANK_SUBDIVISION};


	//#######################################################################################
	//Subdivision: 
	static private String[] pezizomycotinaDownOneLevelNames = {"C"};
	static private String[] pezizomycotinaDownOneLevelTsns = {"6"};
	static private String[] pezizomycotinaDownOneLevelRankNames = {RANK_ORDER};

	//#######################################################################################
	//Unknown: 
	static private String[] lecanoromycetesDownOneLevelNames = {"C"};
	static private String[] lecanoromycetesDownOneLevelTsns = {"6"};
	static private String[] lecanoromycetesDownOneLevelRankNames = {RANK_ORDER};

	//#######################################################################################
	//Order: 
	static private String[] lecanoralesDownOneLevelNames = {"C"};
	static private String[] lecanoralesDownOneLevelTsns = {"6"};
	static private String[] lecanoralesDownOneLevelRankNames = {RANK_ORDER};


	//#######################################################################################
	//Class: 
	static private String[] teloschistineaeDownOneLevelNames = {"C"};
	static private String[] teloschistineaeDownOneLevelTsns = {"6"};
	static private String[] teloschistineaeDownOneLevelRankNames = {RANK_ORDER};

	//#######################################################################################
	//Family: 	
	static private String[] teloschistaceaeDownOneLevelNames = {"C"};
	static private String[] teloschistaceaeDownOneLevelTsns = {"6"};
	static private String[] teloschistaceaeDownOneLevelRankNames = {RANK_ORDER};

	//#######################################################################################
	//Genus: 
	static private String[] caloplacaDownOneLevelNames = {"C"};
	static private String[] caloplacaDownOneLevelTsns = {"6"};
	static private String[] caloplacaDownOneLevelRankNames = {RANK_ORDER};

	//#######################################################################################
	//Species: 
	static private String[] caloplacaFerrugineaDownOneLevelNames = {"C"};
	static private String[] caloplacaFerrugineaDownOneLevelTsns = {"6"};
	static private String[] caloplacaFerrugineaDownOneLevelRankNames = {RANK_ORDER};

	//#######################################################################################
	static{
		init();
	}


	static final void init(){
		// Init  tsnToNameMap tsn/names
		tsnToNameMap = new HashMap<String,String>();
		tsnToRankMap = new HashMap<String,String>();
		tsnToTaxonomicStandingMap = new HashMap<String,String>();
		tsnToCompletenessMap = new HashMap<String,String>();
		for(int i=0; i<kingdomTsns.length; i++){
			tsnToNameMap.put(kingdomTsns[i], kingdomNames[i]);
			tsnToRankMap.put(kingdomTsns[i], kingdomRankNames[i]);
			tsnToTaxonomicStandingMap.put(kingdomTsns[i], kingdomTaxonomicStanding[i]);
			tsnToCompletenessMap.put(kingdomTsns[i], kingdomCompleteness[i]);
		}

		for(int i=0; i<fungalDownOneLevelTsns.length; i++){
			tsnToNameMap.put(fungalDownOneLevelTsns[i], fungalDownOneLevelNames[i]);
			tsnToRankMap.put(fungalDownOneLevelTsns[i], fungalDownOneLevelRankNames[i]);
		}

		tsnToVernacularMap = new HashMap<String, Map<String, List<String>>>();
		// Init Fungal kindom
		//   Vernacular
		Map<String, List<String>> fungiLanguageToVernacularNamesMap = new HashMap<String, List<String>>();
		fungiLanguageToVernacularNamesMap = new HashMap<String, List<String>>();
		fungiLanguageToVernacularNamesMap.put(ENG, Arrays.asList(fungiVernacularNamesEng));
		fungiLanguageToVernacularNamesMap.put(FRA, Arrays.asList(fungiVernacularNamesFra));
		fungiLanguageToVernacularNamesMap.put(POR, Arrays.asList(fungiVernacularNamesPor));
		tsnToVernacularMap.put(fungiTsn, fungiLanguageToVernacularNamesMap);
		//   Vernacular
	}


	static List<ItisRecord> getKingdoms(){
		List<ItisRecord> kingdoms = new ArrayList<ItisRecord>();
		for(int i=0; i<kingdomTsns.length; i++){
			kingdoms.add(makeRecordFromTsn(kingdomTsns[i]));
		}
		return kingdoms;
	}

	static ItisRecord getByTSN(String tsn) throws Exception{
		ItisRecord ir = makeRecordFromTsn(tsn);
		if(ir != null){
			return ir;
		}
		return null;
	}

	static ItisRecord makeRecordFromTsn(String tsn){
		if(tsnToNameMap.containsKey(tsn)){
			ItisRecord kir = new ItisRecord();
			kir.setCombinedName(tsnToNameMap.get(tsn));
			kir.setTsn(tsn);	
			kir.setCompleteness(tsnToCompletenessMap.get(tsn));
			kir.setVernacularNames(tsnToVernacularMap.get(tsn));
			return kir;
		}
		return null;
	}

	public static List<ItisRecord> getRanksOneRankDownFromTsn(String tsn) throws Exception{
		switch(tsn){
		case fungiTsn:
	    
			break;
		case ascomycotaTsn:

			break;
		case pezizomycotinaTsn:

			break;
		case lecanoromycetesTsn:

			break;
		case lecanoralesTsn:

			break;
		case teloschistineaeTsn:

			break;
		case teloschistaceaeTsn:

			break;
		case caloplacaTsn:

			break;
		case caloplacaFerrugineaTsn:

			break;
		}
		return null;
	}


	/// Utils

    
	static final TaxonomicRank makeTaxonomicRankFromTsn(String tsn){
		TaxonomicRank tr = new TaxonomicRank();
		return tr;
	}
}
