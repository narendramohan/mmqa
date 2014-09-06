package mmqa.util;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.services.samples.youtube.cmdline.Auth;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import mmqa.controller.Result;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Print a list of videos matching a search term.
 *
 * @author Jeremy Walker
 */
public class Search {

    /**
     * Define a global variable that identifies the name of a file that
     * contains the developer's API key.
     */
    private static final String PROPERTIES_FILENAME = "youtube.properties";

    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

    /**
     * Initialize a YouTube object to search for videos on YouTube. Then
     * display the name and thumbnail image of each video in the result set.
     *
     * @param args command line args.
     */
    public static void main(String[] args) {
    	String queryTerm;
		try {
			queryTerm = getInputQuery();
			videoSearch(queryTerm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Read the developer key from the properties file.
        
    }

	public static List<Map> videoSearch(String queryTerm) {
		Properties properties = new Properties();
		 List<Map> list = new ArrayList<Map>();
        /*try {
            InputStream in = Search.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
            properties.load(in);

        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
            System.exit(1);
        }*/

        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-search-sample").build();

            // Prompt the user to enter a query term.
            

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Set your developer key from the Google Developers Console for
            // non-authenticated requests. See:
            // https://cloud.google.com/console
            //String apiKey = properties.getProperty("youtube.apikey");
            search.setKey("AIzaSyCBHkqRBFYL1CTix9plq5iwWbzQYWIliKU");
            search.setQ(queryTerm);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            
            if (searchResultList != null) {
                prettyPrint(searchResultList.iterator(), queryTerm);
            }
            
            Iterator<SearchResult> iteratorSearchResults=searchResultList.iterator();
			//"<iframe width=\"560\" height=\"315\" src=\"//www.youtube.com/embed/IYBrtzOvfqo\" frameborder=\"0\" allowfullscreen></iframe>"
            if (!iteratorSearchResults.hasNext()) {
                System.out.println(" There aren't any results for your query.");
            }
           
            while (iteratorSearchResults.hasNext()) {

                SearchResult singleVideo = iteratorSearchResults.next();
                ResourceId rId = singleVideo.getId();

                // Confirm that the result represents a video. Otherwise, the
                // item will not contain a video ID.
                int i=0;
                if (rId.getKind().equals("youtube#video")) {
                	if(i==10)break;
                    Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("embed", "<iframe width=\"560\" height=\"315\" src=\"//www.youtube.com/embed/"+rId.getVideoId()+"\" frameborder=\"0\" allowfullscreen></iframe>");
                    map.put("title", singleVideo.getSnippet().getTitle());
                    list.add(map);
                    i++;
                   /* System.out.println(" Video Id" + rId.getVideoId());
                    System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                    System.out.println(" Thumbnail: " + thumbnail.getUrl());
                    System.out.println("\n-------------------------------------------------------------\n");*/
                }
            }
            
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return list;
	}

    /*
     * Prompt the user to enter a query term and return the user-specified term.
     */
    private static String getInputQuery() throws IOException {

        String inputQuery = "";

        System.out.print("Please enter a search term: ");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
        inputQuery = bReader.readLine();

        if (inputQuery.length() < 1) {
            // Use the string "YouTube Developers Live" as a default.
            inputQuery = "YouTube Developers Live";
        }
        return inputQuery;
    }

    /*
     * Prints out all results in the Iterator. For each result, print the
     * title, video ID, and thumbnail.
     *
     * @param iteratorSearchResults Iterator of SearchResults to print
     *
     * @param query Search query (String)
     */
    private static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {

        /*System.out.println("\n=============================================================");
        System.out.println(
                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
        System.out.println("=============================================================\n");
*/
        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();

               /* System.out.println(" Video Id" + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println("\n-------------------------------------------------------------\n");*/
            }
        }
    }
    
    public static void imageSearch(String searchKey, String userIp, List<Result> imgList){
    	try {

			URL url = new URL("https://ajax.googleapis.com/ajax/services/search/images?" +
                        "v=1.0&q="+java.net.URLEncoder.encode(searchKey, "UTF-8")+"&key=ABQIAAAAMDidA1PAO0alsihAElsy3xTLCrE5uk8Ud_JrDKiWLKYeT0PD8xQ9hbFvmXJ2enaXdFRHJflbRAe36A&userip="+userIp);

			URLConnection connection = url.openConnection();
			connection.addRequestProperty("Referer","http://www.techsevy.com");

			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}
			//System.out.println(builder);
			JSONObject json = new JSONObject(builder.toString());
			//System.out.println(json);
			//System.out.println(json.get("unescapedUrl"));
			JSONArray jArray = json.getJSONObject("responseData").getJSONArray("results");
			for (int i=0;i<jArray.length();i++){
				try {
				/*System.out.println(jArray.getJSONObject(i).getString("url"));
				System.out.println(jArray.getJSONObject(i).getString("title"));
				System.out.println(jArray.getJSONObject(i).getString("originalContextUrl"));
				System.out.println("---------------------------------------");*/
				Result result = new Result();
				
				//String question = hitDoc.get("questionimg");
				//System.out.println(jArray.getJSONObject(i).getString("title"));
				//if(question.contains(searchText)){
					result.setTitle(jArray.getJSONObject(i).getString("title"));
					result.setImage(jArray.getJSONObject(i).getString("url"));
					result.setLink(jArray.getJSONObject(i).getString("originalContextUrl"));
					imgList.add(result);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			//return jArray;
			


		} catch (Exception e) {
			e.printStackTrace();
		}
    	//return null;
    }
    
    public static void textSearch(String searchKey, String userIp, List<Result> txtList){
		String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
		//String search = "sachin tendulkar";
		String charset = "UTF-8";

		URL url;
		//Reader reader;
		try {
			url = new URL(google + java.net.URLEncoder.encode(searchKey, charset)+"&key=ABQIAAAAMDidA1PAO0alsihAElsy3xTLCrE5uk8Ud_JrDKiWLKYeT0PD8xQ9hbFvmXJ2enaXdFRHJflbRAe36A&userip="+userIp);
			//reader = new InputStreamReader(url.openStream(), charset);
			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader( new InputStreamReader(url.openStream(), charset));
			
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}
			//GoogleResults results = new Gson().fromJson(reader, GoogleResults.class);
			System.out.println(builder);
			//System.out.println(results.getResponseData().getResults().get(0).getUrl()); 
			//System.out.println(builder);
			JSONObject json = new JSONObject(builder.toString());
			//System.out.println(json);
			//System.out.println(json.get("unescapedUrl"));
			JSONArray jArray = json.getJSONObject("responseData").getJSONArray("results");
			for (int i=0;i<jArray.length();i++){
				try {
				/*System.out.println(jArray.getJSONObject(i).getString("url"));
				System.out.println(jArray.getJSONObject(i).getString("title"));
				System.out.println(jArray.getJSONObject(i).getString("url"));
				System.out.println("---------------------------------------");*/
				Result result = new Result();
				
				//String question = hitDoc.get("questionimg");
				//System.out.println(jArray.getJSONObject(i).getString("content"));
				//if(question.contains(searchText)){
					result.setTitle(jArray.getJSONObject(i).getString("title"));
					result.setText(jArray.getJSONObject(i).getString("content"));
					result.setLink(jArray.getJSONObject(i).getString("url"));
					txtList.add(result);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    
}