package main;

public class FirstLuceneUse {
	
	public static void main(String[] args) {
		try{
			FirstLucene firstLucene = new FirstLucene();
			firstLucene.createDocument();
			firstLucene.addDocToIndex();
			firstLucene.searchTerm();
			firstLucene.showResults();
			firstLucene.highlightResults();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
