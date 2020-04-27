import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

class Count {

	// This class is for storing words count and id's.
	int docs;
	int count;
	int id;
	Set<Integer> ids;

	Count(int id, int docs, int count) {
		this.id = id;
		this.docs = docs;
		this.count = count;
		ids = new TreeSet<Integer>();
	}
}

public class Main {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		File input = new File("C:\\Users\\alpuy\\Desktop\\School\\CMPE414\\radikal.corpus_preprocessed");
		File infofile = new File("Info.txt");
		File termsfile = new File("Terms.txt");
		File docsfile = new File("Docs.txt");
		File dvfile = new File("DV.txt");
		File idvfile = new File("IDV.txt");

		BufferedReader in = (new BufferedReader(new FileReader(input)));
		PrintWriter info = (new PrintWriter(new FileWriter(infofile)));
		PrintWriter terms = (new PrintWriter(new FileWriter(termsfile)));
		PrintWriter docs = (new PrintWriter(new FileWriter(docsfile)));
		PrintWriter dv = (new PrintWriter(new FileWriter(dvfile)));
		PrintWriter idv = (new PrintWriter(new FileWriter(idvfile)));

		Map<String, Count> lexicon = new HashMap<String, Count>();// Data Structure for lexicon

		int termsid = 1;// Start of words ID
		int docid = 1;// Start of documents ID

		in.readLine(); // CORPUS
		in.readLine(); // SOURCE
		String document = in.readLine();

		while (document != null && !document.equals("</CORPUS>")) {
			docid++;
			document = document.substring(document.indexOf("=") + 1, document.indexOf(">"));
			String type = in.readLine();
			type = type.substring(type.indexOf("=") + 1, type.indexOf(">"));
			String str = "";
			String line = in.readLine();

			while (!line.equals("</DOCUMENT>")) {
				str += line; // input string
				line = in.readLine();
			}
			str = str.replaceAll("\\s+", " ").trim(); // Remove all extra spaces

			String[] words = str.split(" ");// Split query to words
			ArrayList<String> doc = new ArrayList<String>();
			// ArrayList for calculate word's count for search
			// because a word could be in one query multiple times
			ArrayList<Integer> ids = new ArrayList<Integer>();
			// ArrayList for storing word's id when writing to file in right order
			ArrayList<String> distinct = new ArrayList<String>();

			for (String word : words) {
				if (!distinct.contains(word))
					distinct.add(word);
				if (lexicon.containsKey(word)) {
					if (!doc.contains(word))
						doc.add(word);
					Count val = lexicon.get(word);
					val.count = val.count + 1;// Add word's count
					ids.add(val.id);
					val.ids.add(docid);
					lexicon.put(word, val);
				} else {
					ids.add(termsid);
					Count count = new Count(termsid++, 1, 1); // id is increase one when a new word is in lexicon
					count.ids.add(docid);
					lexicon.put(word, count);
				}

			}

			for (int k = 0; k < doc.size(); k++) {// this for is for increasing word's document count
				String s = doc.get(k);
				Count val = lexicon.get(s);
				val.docs += 1;
				lexicon.put(s, val);
			}
			Collections.sort(ids);
			Map<Integer, Integer> hm = new HashMap<Integer, Integer>();
			for (Integer i : ids) {
				if (hm.containsKey(i)) {
					int count = hm.get(i) + 1;
					hm.put(i, count);
				} else {
					hm.put(i, 1);
				}
			}
			// displaying the occurrence of elements in the arraylist
			for (Object key : hm.keySet()) {
				int id = (Integer) key;
				String s = Integer.toString(id) + " ";
				s += Integer.toString(hm.get(key)) + "\t";
				dv.write(s); // Write to the dv file
			}
			dv.write("\n");

			docs.write(document + "\t" + type + "\t" + words.length + "\t" + distinct.size() + "\n"); // Write the docs
																										// file

			document = in.readLine();

		} // end of document

		// Write terms to file
		for (Object key : lexicon.keySet()) {
			Count count3 = lexicon.get(key);
			int l=count3.ids.size();
			Object[] a = count3.ids.toArray();
			idv.write(key + "\t");
			for(int k=0; k<l; k++)			
				idv.write(a[k] + "\t");
			idv.write("\n");
			terms.write(key + "\t" + count3.docs + "\t" + count3.count + "\n");// Write terms to a another file
		}
		// Write to info file
		info.write("numberOfDocuments " + docid + "\n" + "numberOfUniqueTerms " + termsid);

		in.close();
		terms.close();
		info.close();
		docs.close();
		dv.close();
		idv.close();

	}

}
