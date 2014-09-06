package mmqa.video;

import java.io.File;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import mmqa.controller.BasicCrawler;
import mmqa.crawler.Page;
import mmqa.crawler.WebCrawler;
import mmqa.imagecrawler.Cryptography;
import mmqa.parser.BinaryParseData;
import mmqa.url.WebURL;
import mmqa.util.IO;

/*
 * This class shows how you can crawl images on the web and store them in a
 * folder. This is just for demonstration purposes and doesn't scale for large
 * number of images. For crawling millions of images you would need to store
 * downloaded images in a hierarchy of folders
 */
public class VideoCrawler extends WebCrawler {

	private static final Pattern filters = Pattern.compile(".*(\\.(css|js|mid|mp2|bmp|gif|jpe?g|png|tiff|m4v|pdf"
			+ "|rm|smil|zip|rar|gz))$");

	private static final Pattern videoPatterns = Pattern.compile(".*(\\.(mp3|mp4|wav|avi|mov|mpeg|ram|wmv|swf|wma?))$");

	private static File storageFolder;
	private static String[] crawlDomains;

	public static void configure(String[] domain, String storageFolderName) {
		VideoCrawler.crawlDomains = domain;

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

		if (videoPatterns.matcher(href).matches()) {
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
		Directory directory = new RAMDirectory();
		try {
			String url = page.getWebURL().getURL();
			iwriter = BasicCrawler.getIndexWriter();
			iwriter.setMaxFieldLength(25000);
			// We are only interested in processing images
			if (!(page.getParseData() instanceof BinaryParseData)) {
				return;
			}
	
			if (!videoPatterns.matcher(url).matches()) {
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
			//IO.writeBytesToFile(page.getContentData(), storageFolder.getAbsolutePath() + "/" + hashedName);
			doc.add(new Field("questionvdo", url, Field.Store.YES, Field.Index.ANALYZED));
			doc.add(new Field("url", url, Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field("video", url, Field.Store.YES, Field.Index.NOT_ANALYZED));
			iwriter.addDocument(doc);

			iwriter.optimize();
			iwriter.close();
			System.out.println("Stored: " + url);
		}catch (Exception e){
			
		}
	}
}
