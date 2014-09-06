package mmqa.controller;

import java.io.IOException;
import java.util.regex.Pattern;

import mmqa.crawler.Page;
import mmqa.crawler.WebCrawler;
import mmqa.parsehtmldata.ParseHtmlContent;
import mmqa.parser.HtmlParseData;
import mmqa.url.WebURL;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;


import org.apache.lucene.util.Version;


public class BasicCrawler extends WebCrawler {

	private final static Pattern FILTERS = Pattern
			.compile(".*(\\.(css|js|bmp|gif|jpe?g"
					+ "|png|tiff?|mid|mp2|mp3|mp4"
					+ "|wav|avi|mov|mpeg|ram|m4v|pdf"
					+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	/*private static final Pattern nonImgfilters = Pattern.compile(".*(\\.(css|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf"
			+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	private static final Pattern imgPatterns = Pattern.compile(".*(\\.(bmp|gif|jpe?g|png|tiff?))$");*/

	String crawlerpath = "C:\\MyProject\\eclipse\\workspace\\mmqa\\data";

	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches();
				//&& href.startsWith("http://www.ics.uci.edu/");
	}
	IndexWriter iwriter=null;
	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void visit(Page page) {
		//int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL();
		//String domain = page.getWebURL().getDomain();
		//String path = page.getWebURL().getPath();
		//String subDomain = page.getWebURL().getSubDomain();
		//String parentUrl = page.getWebURL().getParentUrl();
		//String anchor = page.getWebURL().getAnchor();

		try {
			Document doc = new Document();
			//if(iwriter==null)
			iwriter = getIndexWriter();
			iwriter.setMaxFieldLength(25000);
			if (page.getParseData() instanceof HtmlParseData) {
				HtmlParseData htmlParseData = (HtmlParseData) page
						.getParseData();
				
				//ParseHtmlContent.parseAndAnalyzeTextFrom(iwriter, htmlParseData, url);
				//iwriter.addDocument(doc);
				ParseHtmlContent.parseAndAnalyzeQuestion(iwriter, htmlParseData, url);
				//iwriter.addDocument(doc);
				//ParseHtmlContent.parseAndAnalyzeContent(iwriter, htmlParseData);
				//iwriter.addDocument(doc);
				
				
				ParseHtmlContent.parseAndAnalyzeImgFrom(iwriter, htmlParseData, url);
				//ParseHtmlContent.parseAndAnalyzeVdoFrom(iwriter, htmlParseData, url);
				//iwriter.addDocument(doc);
				/*String text = htmlParseData.getText();
				String html = htmlParseData.getHtml();
				System.out.println(html);
				String title = htmlParseData.getTitle();
				List<WebURL> links = htmlParseData.getOutgoingUrls();


				// Store the index in memory:
				
				doc.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED));
				//String text1 = "This is the text to be indexed.";
				doc.add(new Field("text", text, Field.Store.YES, Field.Index.ANALYZED));
				//System.out.println(html);
				doc.add(new Field("html", html, Field.Store.YES, Field.Index.ANALYZED));
				doc.add(new Field("links", links.toString(), Field.Store.YES, Field.Index.ANALYZED));*/

			}

			/*Header[] responseHeaders = page.getFetchResponseHeaders();
			if (responseHeaders != null) {
				// System.out.println("Response headers:");
				for (Header header : responseHeaders) {
					//doc.add(new Field(header.getName(), header.getValue(), Field.Store.YES, Field.Index.ANALYZED));
					// System.out.println("\t" + header.getName() + ": " +
					// header.getValue());
				}
			}*/
			
			iwriter.addDocument(doc);

			iwriter.optimize();
			iwriter.close();
			//System.out.println("BasicCrawler");
			
		} catch (CorruptIndexException e1) {

		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		// System.out.println("=============");
	}
	 /**
	    * Make a Document object with an un-indexed title field and an
	    * indexed content field.
	    */
/*	   private static Document createDocument(String title, String content) {
	      Document doc = new Document();
	 
	      // Add the title as an unindexed field...
	 
	      doc.add(new Field("title", title, Field.Store.YES, Field.Index.NO));
	 
	 
	      // ...and the content as an indexed field. Note that indexed
	      // Text fields are constructed using a Reader. Lucene can read
	      // and index very large chunks of text, without storing the
	      // entire content verbatim in the index. In this example we
	      // can just wrap the content string in a StringReader.
	      doc.add(new Field("content", content, Field.Store.YES, Field.Index.ANALYZED));
	 
	      return doc;
	   }*/
	public static IndexWriter getIndexWriter() throws IOException {

		//Directory indexDir = FSDirectory.open(new File(crawlerpath));

		IndexWriterConfig luceneConfig = new IndexWriterConfig(
				Version.LUCENE_34, new StandardAnalyzer(Version.LUCENE_34));

		return (new IndexWriter(SearchServlet.indexDir, luceneConfig));
	}
}