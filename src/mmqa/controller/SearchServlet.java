package mmqa.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmqa.util.Search;
import mmqa.util.Util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class SearchServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String crawlerpath = "C:\\MyProject\\eclipse\\workspace\\mmqa\\data";
	public static Directory indexDir;

	@Override
	public void init() {

		try {
			Properties prop = Util.getConfigProperties(getClass()
					.getClassLoader().getResource("/").getPath());
			String path = prop.getProperty("crawlerpath");
			String noofthread = prop.getProperty("noofthread");
			// System.out.println(Util.getConfigProperties(getClass().getClassLoader().getResource("/").getPath()));
			indexDir = FSDirectory.open(new File(path));
			BasicCrawlController bc = new BasicCrawlController(path, noofthread);
			bc.start();
			//Util.CreateIndex(indexDir);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String searchText = request.getParameter("searchText");
		String typeArr[] = request.getParameterValues("type");
		List<String> type = Arrays.asList(typeArr != null ? typeArr
				: new String[] { "" });
		System.out.println(type);
		//Video list
		List<Result> vdoList = new ArrayList<Result>();
		if (searchText != null && !searchText.trim().equals("")) {
			// System.out.println(userName);
			try{
			// Now search the index:
			IndexSearcher isearcher = getIndexSearcher();
			StandardAnalyzer sa = new StandardAnalyzer(Version.LUCENE_34);
			// Parse a simple query that searches for "text":
			MultiFieldQueryParser parser = null;

			//Query query = null;
			List<Result> list = new ArrayList<Result>();
			TopScoreDocCollector collector = TopScoreDocCollector.create(10, true);
			//text search
			System.out.println("---------------- text search------------- ");
			if (type.contains("text") ||type.contains("all")) {
				parser = new MultiFieldQueryParser(	Version.LUCENE_34, new String[] { "question" }, sa);
			
				try {
					Query query  = parser.parse(searchText);
					// System.out.println("query"+query);
					isearcher.search(query, collector);
					
					// System.out.println("isearchr");
					ScoreDoc[] hits = collector.topDocs().scoreDocs;
					for (int i = 0; i < hits.length; i++) {
						
						Result result = new Result();
						Document hitDoc = isearcher.doc(hits[i].doc); // getting actual
																		// document
						String question = hitDoc.get("question");
						System.out.println(question);
						if(question.contains(searchText.replaceAll("\\?", ""))){
							result.setTitle(question);
			
							String text = hitDoc.get("contents");
							if(text==null || text.trim().equals("")) continue;
							result.setText(text);
							result.setLink(hitDoc.get("url"));
							list.add(result);
						}
						
					}
					
					if(list.size()==0)
						Search.textSearch(searchText, request.getRemoteAddr(), list);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

			}
			System.out.println("---------------- Imagae search------------- ");
			//Image listing	
			List<Result> imgList = new ArrayList<Result>();
			if (type.contains("image") ||type.contains("all")) {
				parser = new MultiFieldQueryParser(Version.LUCENE_34, new String[] { "questionimg" }, sa);
				try {
					Query query  = parser.parse(searchText);
					collector = TopScoreDocCollector.create(10, true);
					isearcher.search(query, collector);
					ScoreDoc[] hits = collector.topDocs().scoreDocs;
					for (int i = 0; i < hits.length; i++) {
		
						Result result = new Result();
						Document hitDoc = isearcher.doc(hits[i].doc); // getting actual
																		// document
						String question = hitDoc.get("questionimg");
						System.out.println(question);
						//if(question.contains(searchText)){
							result.setTitle(question);
							result.setImage(hitDoc.get("image"));
							result.setLink(hitDoc.get("url"));
							imgList.add(result);
						//}
						
					}
					Search.imageSearch(searchText, request.getRemoteAddr(), imgList);
					// System.out.println("query"+query);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			
			if (type.contains("video")||type.contains("all")) {
				parser = new MultiFieldQueryParser(Version.LUCENE_34, new String[] { "questionvdo" }, sa);
				try {
					Query query  = parser.parse(searchText);
					isearcher.search(query, collector);
					ScoreDoc[] hits = collector.topDocs().scoreDocs;
					for (int i = 0; i < hits.length; i++) {
		
						Result result = new Result();
						Document hitDoc = isearcher.doc(hits[i].doc); // getting actual
																		// document

						String question = hitDoc.get("questionvdo");
						System.out.println(question);
						result.setTitle(question);
						result.setVideo(hitDoc.get("video"));
						result.setLink(hitDoc.get("url"));
						vdoList.add(result);
						
					}
					// System.out.println("query"+query);
				} catch (ParseException e) {
					e.printStackTrace();
				}				
			}
			isearcher.close();
			
			
			
			
			request.setAttribute("resultList", list);
			request.setAttribute("imgResultList", imgList);
			List<Map> videoList = Search.videoSearch(searchText);
			for(Map video:videoList){
				Result result = new Result();
	
				result.setTitle((String) video.get("title"));
				result.setVideo((String) video.get("embed"));
				result.setLink("");
				vdoList.add(result);	
			}
			request.setAttribute("vdoResultList", vdoList);
			
			} catch (Exception e){e.printStackTrace();}
			
			request.setAttribute("searchText", searchText);
			request.setAttribute("type", type);
			
		}

		response.setHeader("Content-Type", "application/x-javascript");
		response.setHeader("Connection", "keep-alive");
		response.setContentType("text/html");

		String nextJSP = "/searchResults.jsp";
		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher(nextJSP);
		dispatcher.forward(request, response);

	}

	/**
	 * Searches for the given string in the "content" field
	 */
	private static void search(Searcher searcher, String queryString)
			throws ParseException, IOException {

		// Build a Query object
		QueryParser parser = new QueryParser(Version.LUCENE_30, "content",
				new StandardAnalyzer(Version.LUCENE_30));
		Query query = parser.parse(queryString);

		int hitsPerPage = 10;
		// Search for the query
		TopScoreDocCollector collector = TopScoreDocCollector.create(
				5 * hitsPerPage, false);
		searcher.search(query, collector);

		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		int hitCount = collector.getTotalHits();
		System.out.println(hitCount + " total matching documents");

		// Examine the Hits object to see if there were any matches

		if (hitCount == 0) {
			System.out.println("No matches were found for \"" + queryString
					+ "\"");
		} else {
			System.out.println("Hits for \"" + queryString
					+ "\" were found in quotes by:");

			// Iterate over the Documents in the Hits object
			for (int i = 0; i < hitCount; i++) {
				ScoreDoc scoreDoc = hits[i];
				int docId = scoreDoc.doc;
				float docScore = scoreDoc.score;
				System.out.println("docId: " + docId + "\t" + "docScore: "
						+ docScore);

				Document doc = searcher.doc(docId);

				// Print the value that we stored in the "title" field. Note
				// that this Field was not indexed, but (unlike the
				// "contents" field) was stored verbatim and can be
				// retrieved.
				System.out.println("  " + (i + 1) + ". " + doc.get("title"));
				System.out.println("Content: " + doc.get("content"));
			}
		}
		System.out.println();
	}

	IndexSearcher getIndexSearcher() throws IOException {

		return (new IndexSearcher(indexDir));
	}

	private String getHighlightedField(Query query, Analyzer analyzer,
			String fieldName, String fieldValue) throws IOException,
			InvalidTokenOffsetsException {
		Formatter formatter = new SimpleHTMLFormatter(
				"<span class=\"MatchedText\">", "</span>");
		QueryScorer queryScorer = new QueryScorer(query);
		Highlighter highlighter = new Highlighter(formatter, queryScorer);
		highlighter.setTextFragmenter(new SimpleSpanFragmenter(queryScorer,
				Integer.MAX_VALUE));
		highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
		return highlighter.getBestFragment(analyzer, fieldName, fieldValue);
	}
}
