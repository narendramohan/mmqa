package mmqa.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class Util {

	public static byte[] long2ByteArray(long l) {
		byte[] array = new byte[8];
		int i, shift;
		for (i = 0, shift = 56; i < 8; i++, shift -= 8) {
			array[i] = (byte) (0xFF & (l >> shift));
		}
		return array;
	}

	public static byte[] int2ByteArray(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			int offset = (3 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}

	public static void putIntInByteArray(int value, byte[] buf, int offset) {
		for (int i = 0; i < 4; i++) {
			int valueOffset = (3 - i) * 8;
			buf[offset + i] = (byte) ((value >>> valueOffset) & 0xFF);
		}
	}

	public static int byteArray2Int(byte[] b) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i] & 0x000000FF) << shift;
		}
		return value;
	}

	public static long byteArray2Long(byte[] b) {
		int value = 0;
		for (int i = 0; i < 8; i++) {
			int shift = (8 - 1 - i) * 8;
			value += (b[i] & 0x000000FF) << shift;
		}
		return value;
	}

	public static boolean hasBinaryContent(String contentType) {
		if (contentType != null) {
			String typeStr = contentType.toLowerCase();
			if (typeStr.contains("image") || typeStr.contains("audio")
					|| typeStr.contains("video")
					|| typeStr.contains("application")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param contentType
	 * @return
	 */
	public static boolean hasPlainTextContent(String contentType) {
		if (contentType != null) {
			String typeStr = contentType.toLowerCase();
			if (typeStr.contains("text/plain")) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 
	 * @param args
	 * @return
	 */
	public static Properties getProperties(String path, String properties) {

		Properties prop = new Properties();
		InputStream input = null;

		try {
			//String path = Util.class.getClass().getClassLoader().getResource("/").getPath();
			input = new FileInputStream(path+"\\"+properties);

			// load a properties file
			prop.load(input);
			//System.out.println(prop.getProperty("crawlerpath"));
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}
	
	/**
	 * 
	 * @param args
	 * @return
	 */
	public static Properties getConfigProperties(String path) {

		Properties prop = new Properties();
		InputStream input = null;

		try {
			//String path = Util.class.getClass().getClassLoader().getResource("/").getPath();
			input = new FileInputStream(path+"\\config.properties");

			// load a properties file
			prop.load(input);
			//System.out.println(prop.getProperty("crawlerpath"));
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}
	private static Document createDocument(String question, String content, String image, String video) {
        Document doc = new Document();
        doc.add(new Field("question", question, Store.YES, Index.ANALYZED));
        doc.add(new Field("contents", content, Store.YES, Index.ANALYZED,
                Field.TermVector.WITH_POSITIONS_OFFSETS));
        doc.add(new Field("image", image, Store.YES, Index.ANALYZED,
                Field.TermVector.WITH_POSITIONS_OFFSETS));
        doc.add(new Field("video", video, Store.YES, Index.ANALYZED,
                Field.TermVector.WITH_POSITIONS_OFFSETS));
        return doc;
    }
	
	public static void CreateIndex(Directory directory) throws Exception {
        // setup Lucene to use an in-memory index
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_34);
        MaxFieldLength mlf = MaxFieldLength.UNLIMITED;
        IndexWriter writer = new IndexWriter(directory, analyzer, true, mlf);

        // index a few documents
        writer.addDocument(createDocument("What is the difference between an Interface and an Abstract class?", 
        		"An abstract class can have instance methods that implement a default behavior. An Interface can only declare constants and instance methods, but cannot implement default behavior and all methods are implicitly abstract. An interface has all public members and no implementation. An abstract class is a class which may have the usual flavors of class members (private, protected, etc.), but has some abstract methods.", 
        		"http://i.stack.imgur.com/Ci5Wx.png", 
        		"<iframe width=\"420\" height=\"315\" src=\"//www.youtube.com/embed/gN15K6X2B_Q\" frameborder=\"0\" allowfullscreen></iframe>"));
        writer.addDocument(createDocument("What is the purpose of garbage collection in Java, and when is it used?", "The purpose of garbage collection is to identify and discard objects that are no longer needed by a program so that their resources can be reclaimed and reused. A Java object is subject to garbage collection when it becomes unreachable to the program in which it is used.", "http://www.w3resource.com/java-tutorial/images/garbagecollectioninjava1.jpg","<iframe width=\"560\" height=\"315\" src=\"//www.youtube.com/embed/DoJr5QQYsl8\" frameborder=\"0\" allowfullscreen></iframe>"));
        writer.addDocument(createDocument("Describe synchronization in respect to multithreading.", "With respect to multithreading, synchronization is the capability to control the access of multiple threads to shared resources. Without synchonization, it is possible for one thread to modify a shared variable while another thread is in the process of using or updating same shared variable. This usually leads to significant errors.","http://t1.gstatic.com/images?q=tbn:ANd9GcQuI3_rfybYvj1E1Cd3Hqi8-y0C8bXEbDhjyzVjHkIosz_7Ceb_","<iframe width=\"560\" height=\"315\" src=\"//www.youtube.com/embed/IYBrtzOvfqo\" frameborder=\"0\" allowfullscreen></iframe>"));
        writer.addDocument(createDocument("What are pass by reference and passby value?", "Pass By Reference means the passing the address itself rather than passing the value. Passby Value means passing a copy of the value to be passed. ", "http://i.stack.imgur.com/Ci5Wx.png", "<iframe width=\"420\" height=\"315\" src=\"//www.youtube.com/embed/gN15K6X2B_Q\" frameborder=\"0\" allowfullscreen></iframe>"));
        writer.addDocument(createDocument("What is HashMap and Map?", "Map is Interface and Hashmap is class that implements that.", "http://www.w3resource.com/java-tutorial/images/garbagecollectioninjava1.jpg","<iframe width=\"560\" height=\"315\" src=\"//www.youtube.com/embed/DoJr5QQYsl8\" frameborder=\"0\" allowfullscreen></iframe>"));
        writer.addDocument(createDocument("Difference between HashMap and HashTable?", "The HashMap class is roughly equivalent to Hashtable, except that it is unsynchronized and permits nulls. (HashMap allows null values as key and value whereas Hashtable doesnt allow). HashMap does not guarantee that the order of the map will remain constant over time. HashMap is unsynchronized and Hashtable is synchronized.","http://t1.gstatic.com/images?q=tbn:ANd9GcQuI3_rfybYvj1E1Cd3Hqi8-y0C8bXEbDhjyzVjHkIosz_7Ceb_","<iframe width=\"560\" height=\"315\" src=\"//www.youtube.com/embed/IYBrtzOvfqo\" frameborder=\"0\" allowfullscreen></iframe>"));
        writer.addDocument(createDocument("Difference between Vector and ArrayList?", "Vector is synchronized whereas arraylist is not.", "http://i.stack.imgur.com/Ci5Wx.png", "<iframe width=\"420\" height=\"315\" src=\"//www.youtube.com/embed/gN15K6X2B_Q\" frameborder=\"0\" allowfullscreen></iframe>"));
        writer.addDocument(createDocument("Difference between Swing and Awt?", "AWT are heavy-weight componenets. Swings are light-weight components. Hence swing works faster than AWT.", "http://www.w3resource.com/java-tutorial/images/garbagecollectioninjava1.jpg","<iframe width=\"560\" height=\"315\" src=\"//www.youtube.com/embed/DoJr5QQYsl8\" frameborder=\"0\" allowfullscreen></iframe>"));
        writer.addDocument(createDocument("What is the difference between a constructor and a method?", "A constructor is a member function of a class that is used to create objects of that class. It has the same name as the class itself, has no return type, and is invoked using the new operator."+
        			"A method is an ordinary member function of a class. It has its own name, a return type (which may be void), and is invoked using the dot operator.","http://t1.gstatic.com/images?q=tbn:ANd9GcQuI3_rfybYvj1E1Cd3Hqi8-y0C8bXEbDhjyzVjHkIosz_7Ceb_","<iframe width=\"560\" height=\"315\" src=\"//www.youtube.com/embed/IYBrtzOvfqo\" frameborder=\"0\" allowfullscreen></iframe>"));
        writer.addDocument(createDocument("What is an Iterator?", "Some of the collection classes provide traversal of their contents via a java.util.Iterator interface. This interface allows you to walk through a collection of objects, operating on each object in turn. Remember when using Iterators that they contain a snapshot of the collection at the time the Iterator was obtained; generally it is not advisable to modify the collection itself while traversing an Iterator.","http://t1.gstatic.com/images?q=tbn:ANd9GcQuI3_rfybYvj1E1Cd3Hqi8-y0C8bXEbDhjyzVjHkIosz_7Ceb_","<iframe width=\"560\" height=\"315\" src=\"//www.youtube.com/embed/IYBrtzOvfqo\" frameborder=\"0\" allowfullscreen></iframe>"));
        writer.addDocument(createDocument("State the significance of public, private, protected, default modifiers both singly and in combination and state the effect of package relationships on declared items qualified by these modifiers.", "public : Public class is visible in other packages, field is visible everywhere (class must be public too)"+
											"<br>private : Private variables or methods may be used only by an instance of the same class that declares the variable or method, A private feature may only be accessed by the class that owns the feature."+
											"<br>protected : Is available to all classes in the same package and also available to all subclasses of the class that owns the protected feature.This access is provided even to subclasses that reside in a different package from the class that owns the protected feature."+
											"<br>default :What you get by default ie, without any access modifier (ie, public private or protected).It means that it is visible to all within a particular package.", "http://i.stack.imgur.com/Ci5Wx.png", "<iframe width=\"420\" height=\"315\" src=\"//www.youtube.com/embed/gN15K6X2B_Q\" frameborder=\"0\" allowfullscreen></iframe>"));
        writer.addDocument(createDocument("What is an abstract class?", "Abstract class must be extended/subclassed (to be useful). It serves as a template. A class that is abstract may not be instantiated (ie, you may not call its constructor), abstract class may contain static data. Any class with an abstract method is automatically abstract itself, and must be declared as such."+
        								   "A class may be declared abstract even if it has no abstract methods. This prevents it from being instantiated.", "http://www.w3resource.com/java-tutorial/images/garbagecollectioninjava1.jpg","<iframe width=\"560\" height=\"315\" src=\"//www.youtube.com/embed/DoJr5QQYsl8\" frameborder=\"0\" allowfullscreen></iframe>"));

        writer.close();

       /* // search for documents that have "foo bar" in them
        String sentence = "Foo Bar";
        IndexSearcher searcher = new IndexSearcher(directory);
        PhraseQuery query = new PhraseQuery();
        String[] words = sentence.split(" ");
        for (String word : words) {
            query.add(new Term("contents", word));
        }

        // display search results
        TopDocs topDocs = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println(doc);
        }*/
    }


}
