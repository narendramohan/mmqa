package mmqa.imagecrawler;

import java.io.File;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import mmqa.controller.BasicCrawler;
import mmqa.crawler.Page;
import mmqa.crawler.WebCrawler;
import mmqa.parser.BinaryParseData;
import mmqa.url.WebURL;
import mmqa.util.IO;

/*
 * This class shows how you can crawl images on the web and store them in a
 * folder. This is just for demonstration purposes and doesn't scale for large
 * number of images. For crawling millions of images you would need to store
 * downloaded images in a hierarchy of folders
 */
public class ImageCrawler extends WebCrawler {

	private static final Pattern filters = Pattern.compile(".*(\\.(css|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf"
			+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	private static final Pattern imgPatterns = Pattern.compile(".*(\\.(bmp|gif|jpe?g|png|tiff?))$");

	private static File storageFolder;
	private static String[] crawlDomains;

	public static void configure(String[] domain, String storageFolderName) {
		ImageCrawler.crawlDomains = domain;

		storageFolder = new File(storageFolderName);
		if (!storageFolder.exists()) {
			storageFolder.mkdirs();
		}
	}

	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		if (filters.matcher(href).matches()) {
			return false;
		}

		if (imgPatterns.matcher(href).matches()) {
			return true;
		}

		for (String domain : crawlDomains) {
			if (href.startsWith(domain)) {
				return true;
			}
		}
		return false;
	}
	IndexWriter iwriter=null;
	@Override
	public void visit(Page page) {
		Document doc = new Document();
		try {
			//if(iwriter==null)
			iwriter = BasicCrawler.getIndexWriter();
			iwriter.setMaxFieldLength(25000);
			String url = page.getWebURL().getURL();
	
			// We are only interested in processing images
			if (!(page.getParseData() instanceof BinaryParseData)) {
				return;
			}
	
			if (!imgPatterns.matcher(url).matches()) {
				return;
			}
	
			// Not interested in very small images
			if (page.getContentData().length < 10 * 1024) {
				return;
			}
	
			// get a unique name for storing this image
			String extension = url.substring(url.lastIndexOf("."));
			String hashedName = Cryptography.MD5(url) + extension;
	
			// store image
			IO.writeBytesToFile(page.getContentData(), storageFolder.getAbsolutePath() + "/" + hashedName);
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_34);
	
			// Store the index in memory:
			
	
			//String text1 = "This is the text to be indexed.";
			doc.add(new Field("image", url, Field.Store.YES, Field.Index.ANALYZED));
			//System.out.println(html);
			//doc.add(new Field("html", html, Field.Store.YES, Field.Index.ANALYZED));
			//doc.add(new Field("links", links.toString(), Field.Store.YES, Field.Index.ANALYZED));
			iwriter.addDocument(doc);

			iwriter.optimize();
			iwriter.close();
			System.out.println("Stored: " + url);
		} catch (Exception e){
			
		}
	}
}
