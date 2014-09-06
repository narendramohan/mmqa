package mmqa.parsehtmldata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mmqa.parser.HtmlParseData;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseHtmlContent {

	public static Document parseAndAnalyzeTextFrom(IndexWriter iwriter, HtmlParseData htmlParseData, String url) {
		
		//String text = htmlParseData.getText();
		String html = htmlParseData.getHtml();
		try {
			parseHtml(iwriter, html, url);
		} catch(Exception e){
			System.out.println(e);
		}
		/*System.out.println(html);
		String title = htmlParseData.getTitle();
		List<WebURL> links = htmlParseData.getOutgoingUrls();


		// Store the index in memory:
		
		doc.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED));
		//String text1 = "This is the text to be indexed.";
		doc.add(new Field("text", text, Field.Store.YES, Field.Index.ANALYZED));
		//System.out.println(html);
		doc.add(new Field("html", html, Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("links", links.toString(), Field.Store.YES, Field.Index.ANALYZED));
		*/
		return null;
	}

	private static void parseHtml(IndexWriter iwriter, String html, String url) {
		org.jsoup.nodes.Document doc =Jsoup.parse(html.toString());
		String title = doc.title();
		String regex = "(Who|What|Where|Why|How|Describe|Define)";
		//Elements elements = doc.getAllElements();
		Document lucDoc = new Document();
		Elements elems = doc.body().getElementsMatchingText(regex);//"(Who|what|when|where|why|how|Describe|Define)");
		for(Element ele:elems){
			System.out.println(ele.html());
			lucDoc = new Document();
			String question = ele.text();
			
			if(question!=null && question.length()>=2 && question.length()<=200) {
				//List<String> asList = Arrays.asList(regex.replaceAll("[\\(\\)]", "").split("\\|"));

				
				StringBuilder contents = new StringBuilder();
				//ele.text()
				String qArr[] = question.toLowerCase().replaceAll("[^a-zA-Z'\\s]","").split(" ");//replace all the special characters.
				List<String> list = new ArrayList<String>(Arrays.asList(qArr));
				list.removeAll(stopWords);
				//System.out.println("list:"+list);
				Set<String> identicalStrSet = new HashSet<String>(list);
				//System.out.println("identicalStrSet: "+identicalStrSet);
				for(String s:identicalStrSet){
					//System.out.println("elec.text(): " +elec.text());
					if( s!=null && s.length()>2) {
						Elements elemc = doc.body().getElementsMatchingText("^"+regex);
					/*	Elements elemc = doc.body().getElementsByClass(answerClass);
						System.out.println("Question: " +ele.text());
						System.out.println("Answer: " +elemc.text());
						lucDoc.add(new Field("question", question, Field.Store.YES, Index.ANALYZED));
						lucDoc.add(new Field("contents", elemc.text(), Field.Store.YES, Index.NOT_ANALYZED));
						lucDoc.add(new Field("url", url, Field.Store.YES, Index.NOT_ANALYZED));*/
						for(Element elec:elemc){
							
							//boolean  isQuestionThere = false;
							String answer = elec.text();
							if(answer!=null && answer.length()>2 && answer.toLowerCase().contains(s)) {
								
								contents.append("<p>");
								contents.append(answer);
								contents.append("</p>");
								String content = contents.toString();
								lucDoc.add(new Field("question", question, Field.Store.YES, Index.ANALYZED));
								lucDoc.add(new Field("contents", content, Field.Store.YES, Index.NOT_ANALYZED));
								lucDoc.add(new Field("url", url, Field.Store.YES, Index.NOT_ANALYZED));
								/*System.out.println(ele.text());
								System.out.println("Answer: " +elec.html());
									for(String qStr: asList){
									if(answer.contains(qStr)){
									//	isQuestionThere = true;
										break;
									} 
										
								}
								if(!isQuestionThere) {
									System.out.println("Answer: " +answer);
									System.out.println("Answer: " +elec.html());
									
								}
								break;*/
							}
						}
					}
						
				}
				
				//if(content!=null && content.length()>300)
				//	content = content.substring(0, 300);
				
				//lucDoc.add(new Field("html", ele.text(), Store.YES, Index.ANALYZED));
				//System.out.println("---------------------------\n"+ele.text());
				try {
					//iwriter.updateDocument(new Term(ele.text()), lucDoc);//(lucDoc);
					iwriter.addDocument(lucDoc);
					
				} catch (CorruptIndexException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		}
		
		
		System.out.println("title : " + title);
		
	}

	public static List<String> stopWords= Arrays.asList(new String("a,able,about,across,after,all,almost,also,am,among,an,and,any,are,as,at,"
			+ "be,because,been,but,by,can,cannot,could,dear,did,do,does,either,else,ever,every,for,from,get,"
			+ "got,had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,its,just,least,let,like,likely"
			+ ",may,me,might,most,must,my,neither,no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,"
			+ "says,she,should,since,so,some,than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us,"
			+ "wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would,yet,you,your").split(","));
	public static Document parseAndAnalyzeImgFrom(IndexWriter iwriter, HtmlParseData htmlParseData, String url) {
        //Validate.isTrue(args.length == 1, "usage: supply url to fetch");
       // String url = args[0];
       // print("Fetching %s...", url);
		Document lucDoc = new Document();
        org.jsoup.nodes.Document doc = Jsoup.parse(htmlParseData.getHtml());
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("img[src~=(?i)\\.(png|jpe?g|jpg|bmp|tiff|gif)]"); //.select("link[href]");".*(\\.(bmp|gif|jpe?g|png|tiff?))$"

        print("\nMedia: (%d)", media.size());
        for (Element src : media) {
            if (src.tagName().equals("img")){
            	//System.out.println(src.attr("abs:src")+ " "+src.attr("alt"));
            	lucDoc.add(new Field("questionimg", src.attr("alt"), Store.YES, Index.ANALYZED));
            	lucDoc.add(new Field("url", url, Field.Store.YES, Index.NOT_ANALYZED));
            	lucDoc.add(new Field("contents", "", Store.YES, Index.NOT_ANALYZED));
            	lucDoc.add(new Field("image", src.attr("abs:src"), Store.YES, Index.NOT_ANALYZED));
                print(" * %s: <%s> %sx%s (%s)",
                        src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
                        trim(src.attr("alt"), 20));
                try {
        			iwriter.addDocument(lucDoc);
        		} catch (CorruptIndexException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
            } else
                print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
        }

        print("\nImports: (%d)", imports.size());
        for (Element link : imports) {
            print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
        }

        print("\nLinks: (%d)", links.size());
        for (Element link : links) {
            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
        }
		return lucDoc;
    }

    private static void print(String msg, Object... args) {
       // System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }

	public static Document parseAndAnalyzeQuestion(IndexWriter iwriter, HtmlParseData htmlParseData, String url) {
		Document lucDoc = new Document();
		org.jsoup.nodes.Document doc =Jsoup.parse(htmlParseData.getHtml());
		//String title = doc.title();
		//Elements elements = doc.getAllElements();
		String regex = "(Who|What|Where|Why|How|Describe|Define)";
		String className = "question";
		String className2 = "answer";
		//System.out.println(url);
		if(url.contains("http://www.tutorialspoint.com/cloud_computing"))
			getQuestionAnswer1(iwriter, lucDoc, doc, regex, "h2", "p", url);
		else if(url.contains("http://www.tutorialspoint.com"))
			getQuestionAnswer(iwriter, lucDoc, doc, regex, className, className2, url);
		else if(url.contains("http://www.indiabix.com/"))
			getQuestionAnswer(iwriter, lucDoc, doc, regex, "tech-question", "tech-answer", url);
		else if(url.contains("answers.com/"))
			getQuestionAnswer(iwriter, lucDoc, doc, regex, "answer_text", url);
		else {
			parseAndAnalyzeTextFrom(iwriter, htmlParseData, url);
		}
		return lucDoc;
	}

	public static Document getQuestionAnswer1(IndexWriter iwriter,
			Document lucDoc, org.jsoup.nodes.Document doc, String regex,
			String tagName, String tagName2, String url) {
		Elements elems = doc.body().getElementsByTag(tagName); // doc.body().getElementsMatchingText(regex);
		//int i=0;
		for(Element ele:elems){
			lucDoc = new Document();
			String question = ele.text();
			//System.out.println(ele.text());
			if(question!=null && question.length()>=2 && question.length()<=200 && question.contains("?")) {
				List<String> asList = Arrays.asList(regex.replaceAll("[\\(\\)]", "").split("\\|"));
				for(String qStr: asList){
					if(question.startsWith(qStr)){
						break;
					} else if (question.indexOf(qStr)>0) {
						question = question.substring(question.indexOf(qStr));
						break;
					}
						
				}

				String qest = question.substring(0, question.indexOf("?")+1); 
				System.out.println("Question: "+qest);
				lucDoc.add(new Field("question", qest, Field.Store.YES, Index.ANALYZED));
				lucDoc.add(new Field("url", url, Field.Store.YES, Index.NOT_ANALYZED));
				
				StringBuilder contents = new StringBuilder();
				//ele.text()
				String qArr[] = question.toLowerCase().replaceAll("[^a-zA-Z'\\s]","").split(" ");//replace all the special characters.
				List<String> list = new ArrayList<String>(Arrays.asList(qArr));
				list.removeAll(stopWords);
				//System.out.println("list:"+list);
				Set<String> identicalStrSet = new HashSet<String>(list);
				System.out.println("identicalStrSet: "+identicalStrSet);
				for(String s:identicalStrSet){
					//System.out.println("elec.text(): " +elec.text());
					if( s!=null && s.length()>2) {
						
						Elements elemc = doc.body().getElementsByTag(tagName2);
						
						for(Element elec:elemc){
							//System.out.println("Answer: " +elec.html());
							boolean  isQuestionThere = false;
							String answer = elec.text();
							if(answer!=null && answer.length()>2 && answer.toLowerCase().contains(s)) {
								for(String qStr: asList){
									if(answer.contains(qStr)){
										isQuestionThere = true;
										break;
									} 
										
								}
								if(!isQuestionThere) {
									System.out.println("Answer: " +answer);
									System.out.println("Answer: " +elec.html());
									contents.append("<p>");
									contents.append(answer);
									contents.append("</p>");
								}
								break;
							}
						}
					}
						
				}
				String content = contents.toString();
				if(content!=null && content.length()>300)
					content = content.substring(0, 300)+" ....";
				lucDoc.add(new Field("contents", contents.toString(), Field.Store.YES, Index.NOT_ANALYZED));
				lucDoc.add(new Field("url", url, Field.Store.YES, Index.NOT_ANALYZED));
				//lucDoc.add(new Field("html", ele.text(), Store.YES, Index.ANALYZED));
				//System.out.println("---------------------------\n"+ele.text());
				try {
					//iwriter.updateDocument(new Term(ele.text()), lucDoc);//(lucDoc);
					iwriter.addDocument(lucDoc);
					
				} catch (CorruptIndexException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return lucDoc;
	}

	public static Document getQuestionAnswer(IndexWriter iwriter,
			Document lucDoc, org.jsoup.nodes.Document doc, String regex,
			String className, String className2, String url) {
		Elements elems = doc.body().getElementsByClass(className); // doc.body().getElementsMatchingText(regex);
		//int i=0;
		for(Element ele:elems){
			lucDoc = new Document();
			String question = ele.text();
			//System.out.println(ele.text());
			if(question!=null && question.length()>=2 && question.length()<=200 && question.contains("?")) {
				List<String> asList = Arrays.asList(regex.replaceAll("[\\(\\)]", "").split("\\|"));
				for(String qStr: asList){
					if(question.startsWith(qStr)){
						break;
					} else if (question.indexOf(qStr)>0) {
						question = question.substring(question.indexOf(qStr));
						break;
					}
						
				}

				String qest = question.substring(0, question.indexOf("?")+1); 
				System.out.println("Question: "+qest);
				lucDoc.add(new Field("question", qest, Field.Store.YES, Index.ANALYZED));
				lucDoc.add(new Field("url", url, Field.Store.YES, Index.NOT_ANALYZED));
				
				StringBuilder contents = new StringBuilder();
				//ele.text()
				String qArr[] = question.toLowerCase().replaceAll("[^a-zA-Z'\\s]","").split(" ");//replace all the special characters.
				List<String> list = new ArrayList<String>(Arrays.asList(qArr));
				list.removeAll(stopWords);
				//System.out.println("list:"+list);
				Set<String> identicalStrSet = new HashSet<String>(list);
				System.out.println("identicalStrSet: "+identicalStrSet);
				for(String s:identicalStrSet){
					//System.out.println("elec.text(): " +elec.text());
					if( s!=null && s.length()>2) {
						
						Elements elemc = doc.body().getElementsByClass(className2);
						
						for(Element elec:elemc){
							//System.out.println("Answer: " +elec.html());
							boolean  isQuestionThere = false;
							String answer = elec.text();
							if(answer!=null && answer.length()>2 && answer.toLowerCase().contains(s)) {
								for(String qStr: asList){
									if(answer.contains(qStr)){
										isQuestionThere = true;
										break;
									} 
										
								}
								if(!isQuestionThere) {
									System.out.println("Answer: " +answer);
									System.out.println("Answer: " +elec.html());
									contents.append("<p>");
									contents.append(answer);
									contents.append("</p>");
								}
								break;
							}
						}
					}
						
				}
				String content = contents.toString();
				if(content!=null && content.length()>300)
					content = content.substring(0, 300)+" ....";
				lucDoc.add(new Field("contents", contents.toString(), Field.Store.YES, Index.NOT_ANALYZED));
				lucDoc.add(new Field("url", url, Field.Store.YES, Index.NOT_ANALYZED));
				//lucDoc.add(new Field("html", ele.text(), Store.YES, Index.ANALYZED));
				//System.out.println("---------------------------\n"+ele.text());
				try {
					//iwriter.updateDocument(new Term(ele.text()), lucDoc);//(lucDoc);
					iwriter.addDocument(lucDoc);
					
				} catch (CorruptIndexException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return lucDoc;
	}

	public static Document getQuestionAnswer(IndexWriter iwriter, Document lucDoc, org.jsoup.nodes.Document doc, String regex, String answerClass, String url) {
		Elements elems = doc.getElementsByTag("title"); // doc.body().getElementsMatchingText(regex);
		//int i=0;
		for(Element ele:elems){
			lucDoc = new Document();
			String question = ele.text();
			
			if(question!=null && question.length()>=2 && question.length()<=200) {
				//List<String> asList = Arrays.asList(regex.replaceAll("[\\(\\)]", "").split("\\|"));

				
				//StringBuilder contents = new StringBuilder();
				//ele.text()
				String qArr[] = question.toLowerCase().replaceAll("[^a-zA-Z'\\s]","").split(" ");//replace all the special characters.
				List<String> list = new ArrayList<String>(Arrays.asList(qArr));
				list.removeAll(stopWords);
				//System.out.println("list:"+list);
				//Set<String> identicalStrSet = new HashSet<String>(list);
				//System.out.println("identicalStrSet: "+identicalStrSet);
				//for(String s:identicalStrSet){
					//System.out.println("elec.text(): " +elec.text());
				//	if( s!=null && s.length()>2) {
						
						Elements elemc = doc.body().getElementsByClass(answerClass);
						System.out.println("Question: " +ele.text());
						System.out.println("Answer: " +elemc.text());
						lucDoc.add(new Field("question", question, Field.Store.YES, Index.ANALYZED));
						lucDoc.add(new Field("contents", elemc.text(), Field.Store.YES, Index.NOT_ANALYZED));
						lucDoc.add(new Field("url", url, Field.Store.YES, Index.NOT_ANALYZED));
						/*for(Element elec:elemc){
							
							boolean  isQuestionThere = false;
							String answer = elec.text();
							if(answer!=null && answer.length()>2 && answer.toLowerCase().contains(s)) {
								
								contents.append("<p>");
								contents.append(answer);
								contents.append("</p>");
								String content = contents.toString();
								lucDoc.add(new Field("question", question, Field.Store.YES, Index.ANALYZED));
								lucDoc.add(new Field("contents", content, Field.Store.YES, Index.NOT_ANALYZED));
								lucDoc.add(new Field("url", url, Field.Store.YES, Index.NOT_ANALYZED));
								System.out.println(ele.text());
								System.out.println("Answer: " +elec.html());
									for(String qStr: asList){
									if(answer.contains(qStr)){
										isQuestionThere = true;
										break;
									} 
										
								}
								if(!isQuestionThere) {
									System.out.println("Answer: " +answer);
									System.out.println("Answer: " +elec.html());
									
								}
								break;
							}
						}*/
				//	}
						
				//}
				
				//if(content!=null && content.length()>300)
				//	content = content.substring(0, 300);
				
				//lucDoc.add(new Field("html", ele.text(), Store.YES, Index.ANALYZED));
				//System.out.println("---------------------------\n"+ele.text());
				try {
					//iwriter.updateDocument(new Term(ele.text()), lucDoc);//(lucDoc);
					iwriter.addDocument(lucDoc);
					
				} catch (CorruptIndexException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return lucDoc;
	}

	public static Document parseAndAnalyzeContent(IndexWriter iwriter, HtmlParseData htmlParseData) {
		Document lucDoc = new Document();
		org.jsoup.nodes.Document doc =Jsoup.parse(htmlParseData.getHtml());
		//String title = doc.title();
		//Elements elements = doc.getAllElements();
		Elements elems = doc.body().getElementsMatchingText("[^(Who|what|where|why|how|Describe|Define)]");
		for(Element ele:elems){
			lucDoc.add(new Field("contents", ele.text(), Store.YES, Index.ANALYZED));
			try {
				iwriter.addDocument(lucDoc);
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return lucDoc;
	}

	public static void parseAndAnalyzeVdoFrom(IndexWriter iwriter,
			HtmlParseData htmlParseData, String url) {
		Document lucDoc = new Document();
        org.jsoup.nodes.Document doc = Jsoup.parse(htmlParseData.getHtml());
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]"); //.select("link[href]");

        print("\nMedia: (%d)", media.size());
        for (Element src : media) {
            if (src.tagName().equals("img")){
            	System.out.println(src.attr("abs:src")+ " "+src.attr("alt"));
            	lucDoc.add(new Field("questionimg", src.attr("alt"), Store.YES, Index.ANALYZED));
            	lucDoc.add(new Field("url", url, Field.Store.YES, Index.NOT_ANALYZED));
            	lucDoc.add(new Field("contents", "", Store.YES, Index.NOT_ANALYZED));
            	lucDoc.add(new Field("image", src.attr("abs:src"), Store.YES, Index.NOT_ANALYZED));
                print(" * %s: <%s> %sx%s (%s)",
                        src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
                        trim(src.attr("alt"), 20));
                try {
        			iwriter.addDocument(lucDoc);
        		} catch (CorruptIndexException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
            } else
                print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
        }

        print("\nImports: (%d)", imports.size());
        for (Element link : imports) {
            print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
        }

        print("\nLinks: (%d)", links.size());
        for (Element link : links) {
            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
        }		
	}	
	

}
