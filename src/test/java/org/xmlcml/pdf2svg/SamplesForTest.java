package org.xmlcml.pdf2svg;

import org.junit.Ignore;
import org.junit.Test;

/** typical files that may or may not give errors
 * will not be run in normal testing
 * 
 * @author pm286
 *
 */
public class SamplesForTest {
	
	@Test
	@Ignore
	public void main() {
		// columbia
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/columbia", "../pdfs/columbia");
		// NICE
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/nice1", "src/test/resources/nice1");
		// national occ standards
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/nos", "src/test/resources/nos");
		// Comment in/out what you want
		// astrophysics
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/astrophys", "src/test/resources/astrophys");
		// Law paper
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/gjoil", "src/test/resources/gjoil");
		// Word thesis 1 document
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/word", "src/test/resources/word/harterchap7small.pdf");
//		// encryption 1 article // this also has a stretched glyph //OK
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/ajc", "../pdfs/ajc/CH01182.pdf");
		//living reviews in relativity
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/livingReviews", "src/test/resources/livingReviews");
		
//		// AJC corpus 52 sec
//        new PDF2SVGConverter().run( "-logger", "-infofiles", "-logglyphs", "-outdir", "target/ajc/sample", "../pdfs/ajc/sample");
//		// ?? 42 secs
//		new PDF2SVGConverter().run("-logger", "-outdir", "target/0all/0", "../pdfs/0all/0");
//		// 100 CSIRO all work
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/csiro/pick100", "../pdfs/csiro/pick100");
//		// IUCR has symbol fonts MT_MI/MT_SY // OK
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/iucr", "../pdfs/iucr");
		// MDPI 
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mdpi", "src/test/resources/mdpi");
		// Packed PDFs
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/packed", "../pdfs/packed");
		// Springer
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/springer", "src/test/resources/springer");
//		// CSIRO 
//		new PDF2SVGConverter().run("-logger", "-outdir", "target/csiro/test", "../pdfs/csiro/test");
//		// CSIRO AusSystBot26 // needs a few symbols doing // 180 secs
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/csiro/AusSystBot26", "../pdfs/csiro/AusSystBot26");
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/BotJLinn55", "../pdfs/BotJLinn55");
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/IchthyRes79", "../pdfs/IchthyRes79");
		
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/MolBiolEvol51", "../pdfs/MolBiolEvol51");
		// runs OK // 460 secs
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/Palaeontology44", "../pdfs/Palaeontology44");
		// runs OK
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/ZooJLinn46", "../pdfs/ZooJLinn46");
//		// this has a LOT of problem fonts - Type3, no x,y coords, etc.
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/zootaxa37", "../pdfs/zootaxa37");
//		armbruster_08_genus_626780.pdf // has font3, etc.
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/zootaxa37/armbruster_08/", "../pdfs/zootaxa37/armbruster_08_genus_626780.pdf");
//		// contains Calibri
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/bmc/1471-2148-11-332/", "../pdfs/bmc/1471-2148-11-332.pdf");
//	    // BMC evol biol
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/bmc/evolbio/", "../ami2/pdfs/bmcevolbiol/2/");
//		// other BMC
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/bmc/", "../pdfs/bmc");
		// Elsevier trees (47)
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/e/49MPE/", "../pdfs/e/49MPE/");
//		// 
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/acs/", "../pdfs/acs/");
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/ajc/", "../pdfs/ajc/");
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/ajc/sample", "../pdfs/ajc/sample");
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/apa/", "../pdfs/apa/");
		// 6
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/arxiv/", "../pdfs/arxiv/");
		// 27
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/bmc/", "../pdfs/bmc/");
		// 8
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/csiro/", "../pdfs/csiro/");
		// 3
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/e/", "../pdfs/e/"
//				,"-debugFontName", "GGLKDA+AdvOTb92eb7df.I"
//				);
		// fontType0 problems...
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/jb/", "../pdfs/jb/");
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/misc/", "../pdfs/misc/");
//      new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/npg/", "../pdfs/npg/");
		// OK
		
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/test", "../pdfs/test");
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/test", "../pdfs/peerj/30.pdf",
//				"-debugFontName" , "MCHWMU+CMTT10");
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/test", "../pdfs/peerj/36.pdf", 
//				"-debugFontName", "RNMPIC+Dingbats");

		
//		mainAB();
//		mainCJ();
//		mainMZ();

//		minorABC();
//		minorDEF();
//		minorGHIJK();
//		minorLMNOP();
//		minorRST();
//		minorUVWXYZ();

//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/plosone/", "../pdfs/plosone/");
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/taylorfrancis/", "../pdfs/taylorfrancis/");
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/psyc/", "../pdfs/psyc/");
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/royalsoc/", "../pdfs/royalsoc/");
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/rsc/many/", "../pdfs/rsc/many/");
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/springer/", "../pdfs/springer/");
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/taylorfrancis/", "../pdfs/taylorfrancis/");
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/thesis/", "../pdfs/thesis/");
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/wiley/", "../pdfs/wiley/");
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/word/", "../pdfs/word/");
	}

	@Test
	@Ignore
	public void minorUVWXYZ() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/minorJournals/uvwxyz", "../pdfs/minorJournals/uvwxyz");
	}
	@Test
	@Ignore
	public void minorRST() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/minorJournals/rst", "../pdfs/minorJournals/rst");
	}
	@Test
	@Ignore
	public void minorLMNOP() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/minorJournals/lmnop", "../pdfs/minorJournals/lmnop");
	}
	@Test
	@Ignore
	public void minorGHIJK() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/minorJournals/ghijk", "../pdfs/minorJournals/ghijk");
	}
	@Test
	@Ignore
	public void minorDEF() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/minorJournals/def", "../pdfs/minorJournals/def");
	}
	@Test
	@Ignore
	public void minorABC() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/minorJournals/abc", "../pdfs/minorJournals/abc");
	}
	
	@Test
	@Ignore
	public void mainMZ() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/MolecularBiologyEvolution", "../pdfs/mainJournals/MolecularBiologyEvolution");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/Nature", "../pdfs/mainJournals/Nature");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/Naturwissenschaften", "../pdfs/mainJournals/Naturwissenschaften");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/Palaeontology", "../pdfs/mainJournals/Palaeontology");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/PaläontologischeZeitschrift", "../pdfs/mainJournals/PaläontologischeZeitschrift");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/PhilosophicalTransB", "../pdfs/mainJournals/PhilosophicalTransB");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/PlantSystematicsEvolution", "../pdfs/mainJournals/PlantSystematicsEvolution");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/PLoSBiology", "../pdfs/mainJournals/PLoSBiology");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/PNAS", "../pdfs/mainJournals/PNAS");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/PNAS1", "../pdfs/mainJournals/PNAS1");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/ProcRoySocB", "../pdfs/mainJournals/ProcRoySocB");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/Science", "../pdfs/mainJournals/Science");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/SystBiol", "../pdfs/mainJournals/SystBiol");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/SystematicEntomology", "../pdfs/mainJournals/SystematicEntomology");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/SystematicZoology", "../pdfs/mainJournals/SystematicZoology");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/ZoologicaScripta", "../pdfs/mainJournals/ZoologicaScripta");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/ZoologicalJournalLinneanSociety", "../pdfs/mainJournals/ZoologicalJournalLinneanSociety");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/Zootaxa", "../pdfs/mainJournals/Zootaxa");
	}

	@Test
	@Ignore
	public void mainAB() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/ActaPalaeontologicaPolonica", "../pdfs/mainJournals/ActaPalaeontologicaPolonica");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/ActaZoologica", "../pdfs/mainJournals/ActaZoologica");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/AmericanJournalBotany", "../pdfs/mainJournals/AmericanJournalBotany");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/AmericanMuseumNovitates", "../pdfs/mainJournals/AmericanMuseumNovitates");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/AmericanZoologist", "../pdfs/mainJournals/AmericanZoologist");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/AnnualReviewEcologySystematics", "../pdfs/mainJournals/AnnualReviewEcologySystematics");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/AppliedMathematicsLetters", "../pdfs/mainJournals/AppliedMathematicsLetters");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/BiochemicalSystematicsEcology", "../pdfs/mainJournals/BiochemicalSystematicsEcology");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/Bioinformatics", "../pdfs/mainJournals/Bioinformatics");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/BiologicalJournalLinneanSociety", "../pdfs/mainJournals/BiologicalJournalLinneanSociety");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/BiologicalReviews", "../pdfs/mainJournals/BiologicalReviews");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/BiologyLetters", "../pdfs/mainJournals/BiologyLetters");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/BiologicalReviews", "../pdfs/mainJournals/BiologicalReviews");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/BiologyPhilosophy", "../pdfs/mainJournals/BiologyPhilosophy");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/BMCBioinformatics", "../pdfs/mainJournals/BMCBioinformatics");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/BulletinAmericanMuseumNaturalHistory", "../pdfs/mainJournals/BulletinAmericanMuseumNaturalHistory");
	}
	
	@Test
	@Ignore
	public void mainCJ() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/CanadianJournalEarthSciences", "../pdfs/mainJournals/CanadianJournalEarthSciences");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/Cladistics", "../pdfs/mainJournals/Cladistics");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/Copeia", "../pdfs/mainJournals/Copeia");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/Cretaceous Research", "../pdfs/mainJournals/CretaceousResearch");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/EarthEnvironmentalScienceTransactionsRoyalSocietyEdinburgh", "../pdfs/mainJournals/EarthEnvironmentalScienceTransactionsRoyalSocietyEdinburgh");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/Evolution", "../pdfs/mainJournals/Evolution");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/EvolutionaryBiology", "../pdfs/mainJournals/EvolutionaryBiology");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/Exs", "../pdfs/mainJournals/Exs");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/FungalBiology", "../pdfs/mainJournals/FungalBiology");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/Geobios", "../pdfs/mainJournals/Geobios");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/Geodiversitas", "../pdfs/mainJournals/Geodiversitas");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/HerpetologicalMonographs", "../pdfs/mainJournals/HerpetologicalMonographs");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/IchthyologicalResearch", "../pdfs/mainJournals/IchthyologicalResearch");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/InvertebrateBiology", "../pdfs/mainJournals/InvertebrateBiology");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/JournalBiogeography", "../pdfs/mainJournals/JournalBiogeography");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/JournalEvolutionaryBiology", "../pdfs/mainJournals/JournalEvolutionaryBiology");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/JournalHumanEvolution", "../pdfs/mainJournals/JournalHumanEvolution");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/JournalMammalianEvolution", "../pdfs/mainJournals/JournalMammalianEvolution");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/JournalMolluscanStudies", "../pdfs/mainJournals/JournalMolluscanStudies");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/JournalPaleontology", "../pdfs/mainJournals/JournalPaleontology");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/JournalSystematicPalaeontology", "../pdfs/mainJournals/JournalSystematicPalaeontology");
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mainJournals/JournalVertebratePaleontology", "../pdfs/mainJournals/JournalVertebratePaleontology");
	}

}
