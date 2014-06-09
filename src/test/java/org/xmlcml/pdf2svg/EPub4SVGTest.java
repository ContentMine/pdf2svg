package org.xmlcml.pdf2svg;

import java.io.File;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import nl.siegmann.epublib.domain.Author;
//import nl.siegmann.epublib.domain.Book;
//import nl.siegmann.epublib.domain.Resource;
//import nl.siegmann.epublib.domain.Resources;
//import nl.siegmann.epublib.epub.EpubReader;
//import nl.siegmann.epublib.epub.EpubWriter;
//
//import org.apache.log4j.Logger;
//import org.junit.Assert;
//import org.junit.Ignore;
//import org.junit.Test;


public class EPub4SVGTest {

//	private final static Logger LOG = Logger.getLogger(EPub4SVGTest.class);
//	@Test
//	public void testInitial() throws IOException {
//		PDF2SVGConverter converter = new PDF2SVGConverter();
//		File input = new File(Fixtures.MISC_DIR, "1129-2377-14-93.pdf");
//		Assert.assertTrue("exists", input.exists());
//		String outdirname = "target/1129-2377-14-93/";
//		converter.run("-outdir", outdirname, input.toString());
//
//		String title = "BMC article";
//		Author author = new Author("Peter", "M-R");
//		String outfilename = "target/article.epub";
//		Book book = writeEpub(outdirname, title, author, outfilename);
//		debugEpub("target/article.epub");
//
//	}
//	
//	@Test
//	@Ignore // OOM
//	public void testTrees() throws IOException {
//		PDF2SVGConverter converter = new PDF2SVGConverter();
//		File input = new File(Fixtures.MISC_DIR, "1471-2148-11-312.pdf");
//		Assert.assertTrue("exists", input.exists());
//		String outdirname = "target/1471-2148-11-312/";
//		converter.run("-outdir", outdirname, input.toString());
//
//		String title = "BMC article";
//		Author author = new Author("Peter", "M-R");
//		String outfilename = "target/1471-2148-11-312.epub";
//		Book book = writeEpub(outdirname, title, author, outfilename);
//		debugEpub("target/1471-2148-11-312.epub");
//
//	}
//	
//	private Book writeEpub(String outdirname, String title, Author author,
//			String outfilename) throws FileNotFoundException, IOException {
//		Book book = new Book();
//		book.getMetadata().addTitle(title);
//		book.getMetadata().addAuthor(author);
//		File outdir = new File(outdirname);
//		File[] files = outdir.listFiles();
//		for (File file : files) {
//			FileInputStream fis = new FileInputStream(file);
//			Resource resource = new Resource(fis, file.getName());
//			book.getResources().add(resource);
//		}
//		EpubWriter epubWriter = new EpubWriter();
//		epubWriter.write(book, new FileOutputStream(outfilename));
//		return book;
//	}
//	
//	private void debugEpub(String filename) throws IOException,
//			FileNotFoundException {
//		EpubReader epubReader = new EpubReader();
//		Book book1 = epubReader.readEpub(new FileInputStream(filename));
//		List<Resource> resources = book1.getContents();
//		Resources resources1 = book1.getResources();
//		List<Resource> zz = new ArrayList<Resource>(resources1.getAll());
//		for (Resource resource : zz) {
//			System.out.println(">>>"+resource);
//		}
//	}
}
