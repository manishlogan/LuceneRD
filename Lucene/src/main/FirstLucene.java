package main;

import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class FirstLucene {
	/**Document which is to be indexed and searched*/
	private Document doc;
	
	/**IndexWriter to write and update index*/
	private IndexWriter writer;
	
	/**
	 * Directory to store index
	 * */
	private Directory directory;
	
	/**
	 * Analyzer to analyze the data
	 * */
	private Analyzer analyzer;
	
	/**
	 * Index configurations
	 * */
	private IndexWriterConfig config;
	
	/**
	 * Stores result of the search
	 * */
	private TopDocs results;
	
	/**
	 * Searches the index for the term
	 * */
	private IndexSearcher searcher;
	
	/**
	 * Query used to search the document
	 * */
	private Query termQuery;

	private IndexReader reader;
	
	/**
	 * Highlights searched term in search result.
	 * */
	public void highlightResults() {

	}
	
	/**
	 * Shows Search result
	 * @throws InvalidTokenOffsetsException 
	 * */
	public void showResults() throws CorruptIndexException, IOException, InvalidTokenOffsetsException {
		QueryScorer scorer = new QueryScorer(termQuery);
		SimpleHTMLFormatter formatter = new SimpleHTMLFormatter();
		Highlighter highlighter = new Highlighter(formatter, scorer);
		
		for(ScoreDoc d : results.scoreDocs){
			String text = searcher.doc(d.doc).get("message");
			TokenStream stream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), d.doc, "message", analyzer);
			TextFragment[] fragments = highlighter.getBestTextFragments(stream, text, false, 10);
			for(TextFragment frag : fragments){
				if(frag != null && frag.getScore() > 0){
					System.out.println(frag.toString());
				}
			}
		}
	}

	/**
	 * Searches for term in the document
	 * @throws InvalidTokenOffsetsException 
	 * */
	public void searchTerm() throws ParseException, CorruptIndexException, IOException, InvalidTokenOffsetsException {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Please enter the term you want to search for:");
			String searchString = scanner.nextLine();
			Term term = new Term("message", searchString);
			// termQuery = new TermQuery(term);
			QueryParser parser = new QueryParser(Version.LUCENE_35, "message",
					analyzer);
			termQuery = parser.parse(searchString);
			writer.close();
			reader = IndexReader.open(directory);
			searcher = new IndexSearcher(reader);
			results = searcher.search(termQuery, 10);
	}

	/**
	 * Default Constructor
	 * */
	public FirstLucene() throws CorruptIndexException,
			LockObtainFailedException, IOException {
		directory = new RAMDirectory();
		analyzer = new StandardAnalyzer(Version.LUCENE_35);
		doc = new Document();
		config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
		writer = new IndexWriter(directory, config);
	}

	/**
	 * Add document to index
	 * */
	public void addDocToIndex() throws CorruptIndexException, IOException {
			writer.addDocument(doc);
	}
	/**
	 * Creates document from the available data
	 * */
	public void createDocument() {
		String firstName = "Manish";
		String lastName = "Jain";
		String message = "Open your wings evil angel!!";
		String profession = "Software Engineer";

		Field firstNameField = new Field("firstName", firstName,
				Field.Store.YES, Field.Index.ANALYZED);
		Field lastNameField = new Field("lastName", lastName, Field.Store.NO,
				Field.Index.NOT_ANALYZED);
		Field messageField = new Field("message", message, Field.Store.YES,
				Field.Index.ANALYZED);
		Field professionField = new Field("profession", profession,
				Field.Store.NO, Field.Index.NOT_ANALYZED);

		doc.add(professionField);
		doc.add(firstNameField);
		doc.add(lastNameField);
		doc.add(messageField);
	}
}
