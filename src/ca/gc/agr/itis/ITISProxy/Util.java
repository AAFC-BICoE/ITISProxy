
package ca.gc.agr.itis.ITISProxy;

import ca.gc.agr.itis.ITISModel.*;
import java.util.List;
import java.util.Map;

public class Util{

	private Util(){

	}

	public static void printRecord(ItisRecord ir){
		System.out.println("Name=" + ir.getCombinedName() + "\t\t\t\t [ItisRecord.getCombinedName()]");
		if(ir.getNameAuthor() != null){
			System.out.println(ir.getNameAuthor());
			
		}
		System.out.println("Rank=" + ir.getRank() + "\t\t\t\t [ItisRecord.getRank()]");		
		System.out.println("\ttsn=" + ir.getTsn()  + "\t\t\t [ItisRecord.getTsn()]");
		Map<String, List<String>> vernacularNames = ir.getVernacularNames();

		List<TaxonomicRank> aboveRanks = ir.getTaxonomicHierarchy();
		
		if(aboveRanks != null && aboveRanks.size() > 0){
			System.out.println("\t\t Taxonomic hierarchy:");
			int count = 1;
			for(TaxonomicRank tr: aboveRanks){
				System.out.println("\t\t\t" + spacer(count) + tr.getRankName() + ": " + tr.getRankValue() + "  tsn: " + tr.getTsn());
				count++;
			}
		}


		if(vernacularNames != null && vernacularNames.size() > 0){
			System.out.println("\tVernacularNames:");
			for(String langKey: vernacularNames.keySet()){
				List<String> langNames = vernacularNames.get(langKey);
				for(String name: langNames){
					System.out.println("\t\t" + langKey + ":" + name);
				}
			}
		}
		
		List<TaxonomicRank> belowRanks = ir.getBelowSpeciesRanks();
		if(belowRanks != null && belowRanks.size() > 0){
			System.out.println("\t\t One level below taxonomic ranks:");
			for(TaxonomicRank tr: belowRanks){
				System.out.println("\t\t\t" + tr.getRankName() + ": " + tr.getRankValue() + "  tsn: " + tr.getTsn());
			}
		}

		List<TaxonOtherSource> sources = ir.getOtherSources();
		if(sources != null && sources.size() > 0){
			System.out.println("\t\tOtherSources");
			for(TaxonOtherSource source: sources){
				System.out.println("\t\t\t" + source.getSource() + ":" + source.getSourceType() + ":" + source.getSourceComment());
			}
		}
		List<TaxonPublication> refs = ir.getReferences();
		if(refs != null && refs.size() > 0){
			System.out.println("\t\tReferences:");
			for(TaxonPublication ref: refs){
				System.out.println("\t\t\t" + ref.getPubYear()  + ". " + ref.getReferenceAuthor()
				                   + ". " + ref.getTitle());
			}
		}
		System.out.println();
	}


	static String spacer(int n)
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<n; i++){
			sb.append("  ");
		}
		return sb.toString();
	}
	

}
