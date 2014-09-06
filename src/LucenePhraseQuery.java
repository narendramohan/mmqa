import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class LucenePhraseQuery {
    public static void main(String[] args) throws Exception {
        // setup Lucene to use an in-memory index
        Directory directory = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
        MaxFieldLength mlf = MaxFieldLength.UNLIMITED;
        IndexWriter writer = new IndexWriter(directory, analyzer, true, mlf);

        // index a few documents
        writer.addDocument(createDocument("What is the difference between an Interface and an Abstract class?", "An abstract class can have instance methods that implement a default behavior. An Interface can only declare constants and instance methods, but cannot implement default behavior and all methods are implicitly abstract. An interface has all public members and no implementation. An abstract class is a class which may have the usual flavors of class members (private, protected, etc.), but has some abstract methods.", "http://i.stack.imgur.com/Ci5Wx.png", "<iframe width=\"420\" height=\"315\" src=\"//www.youtube.com/embed/gN15K6X2B_Q\" frameborder=\"0\" allowfullscreen></iframe>"));
        writer.addDocument(createDocument("What is the purpose of garbage collection in Java, and when is it used?", "The purpose of garbage collection is to identify and discard objects that are no longer needed by a program so that their resources can be reclaimed and reused. A Java object is subject to garbage collection when it becomes unreachable to the program in which it is used.", "http://www.w3resource.com/java-tutorial/images/garbagecollectioninjava1.jpg","<iframe width=\"560\" height=\"315\" src=\"//www.youtube.com/embed/DoJr5QQYsl8\" frameborder=\"0\" allowfullscreen></iframe>"));
        writer.addDocument(createDocument("Describe synchronization in respect to multithreading.", "With respect to multithreading, synchronization is the capability to control the access of multiple threads to shared resources. Without synchonization, it is possible for one thread to modify a shared variable while another thread is in the process of using or updating same shared variable. This usually leads to significant errors.","http://t1.gstatic.com/images?q=tbn:ANd9GcQuI3_rfybYvj1E1Cd3Hqi8-y0C8bXEbDhjyzVjHkIosz_7Ceb_","<iframe width=\"560\" height=\"315\" src=\"//www.youtube.com/embed/IYBrtzOvfqo\" frameborder=\"0\" allowfullscreen></iframe>"));
        writer.close();

        // search for documents that have "foo bar" in them
        String sentence = "What is the difference between an Interface and an Abstract class";
        IndexSearcher searcher = new IndexSearcher(directory);
        PhraseQuery query = new PhraseQuery();
        String[] words = sentence.split(" ");
        for (String word : words) {
            query.add(new Term("question", word));
        }

        // display search results
        TopDocs topDocs = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println(doc);
        }
    }

	private static Document createDocument(String question, String content, String image, String video) {
        Document doc = new Document();
        doc.add(new Field("question", question, Store.YES, Index.NOT_ANALYZED,Field.TermVector.WITH_POSITIONS_OFFSETS));
        doc.add(new Field("contents", content, Store.YES, Index.ANALYZED,
                Field.TermVector.WITH_POSITIONS_OFFSETS));
        doc.add(new Field("image", image, Store.YES, Index.ANALYZED,
                Field.TermVector.WITH_POSITIONS_OFFSETS));
        doc.add(new Field("video", video, Store.YES, Index.ANALYZED,
                Field.TermVector.WITH_POSITIONS_OFFSETS));
        return doc;
    }
}