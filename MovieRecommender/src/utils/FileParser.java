package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import com.rapidminer.gui.look.painters.DeterminateProgressBarPainter;
import com.rapidminer.operator.text.io.tokenizer.StringTokenizerOperator;

import edu.uci.ics.jung.algorithms.shortestpath.BFSDistanceLabeler;

public class FileParser {
	private boolean negate;
	private POSTagger tagger;
	private SWN3 lexicon;
	
	public FileParser(){
		tagger = new POSTagger();
		lexicon = new SWN3();
	}

	public void parseTrainingData() {
		System.out.print("Parsing Training Set: ");
		int i = 0;
		
		File baseDir = new File("resources/training set/pos");
		File tempDir = new File("resources/temp/training set/pos/");
		tempDir.mkdirs();
		for (File file : baseDir.listFiles()) {
			if (i % 20 == 0)
				System.out.print(i);
			if (i % 10 == 0)
				System.out.print(".");
			parseFile(tempDir, file);
			i++;
		}

		baseDir = new File("resources/training set/neg");
		tempDir = new File("resources/temp/training set/neg/");
		tempDir.mkdirs();
		for (File file : baseDir.listFiles()) {
			if (i % 20 == 0)
				System.out.print(i);
			if (i % 10 == 0)
				System.out.print(".");
			parseFile(tempDir, file);
			i++;
		}
		System.out.println("done.(" + i + " files)\n");
	}
	
	public void parseTestingData() {
		System.out.print("Parsing Testing Set: ");
		int i = 0;
		
		File baseDir = new File("resources/testing set/pos");
		File tempDir = new File("resources/temp/testing set/pos/");
		tempDir.mkdirs();
		for (File file : baseDir.listFiles()) {
			if (i % 20 == 0)
				System.out.print(i);
			if (i % 10 == 0)
				System.out.print(".");
			parseFile(tempDir, file);
			i++;
		}

		baseDir = new File("resources/testing set/neg");
		tempDir = new File("resources/temp/testing set/neg/");
		tempDir.mkdirs();
		for (File file : baseDir.listFiles()) {
			if (i % 20 == 0)
				System.out.print(i);
			if (i % 10 == 0)
				System.out.print(".");
			parseFile(tempDir, file);
			i++;
		}
		System.out.println("done.(" + i + " files)\n");
	}

	private File parseFile(File tempDir, File file) {
		try {
			FileReader inStream = new FileReader(file);
			BufferedReader reader = new BufferedReader(inStream);

			File outFile = new File(tempDir.getAbsolutePath() + File.separator
					+ file.getName());
			outFile.createNewFile();
			FileWriter outStream = new FileWriter(outFile);
			BufferedWriter writer = new BufferedWriter(outStream);

			lexicon.reset();
			String line = reader.readLine();
			String taggedLine = "";
			String newLine = "";
			while (line != null) {
				line = tagger.tag(line);
				StringTokenizer tokenizer = new StringTokenizer(line);
				while (tokenizer.hasMoreTokens()) {
					String word = tokenizer.nextToken();
					lexicon.updateRating(word);
					word = negate(word.split("_")[0]);
					newLine += word + " ";
				}
				writer.append(newLine);
				writer.newLine();
				newLine = "";
				line = reader.readLine();
			}
			writer.append(lexicon.getTag());
			reader.close();
			writer.flush();
			writer.close();
			return outFile;
		} catch (FileNotFoundException e) {
			System.out.println(e);
			return null;
		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
	}
	
	private String negate(String word) {
		
		if (word.equals("not") || word.contains("n't")) {
			negate = true;
		} else if (word.matches("[!,.\"]")) {
			negate = false;
		} else {
			if (negate)
				word = "NOT_" + word;
		}
		return word;
	}

	public void close() {
		deleteTemp(new File("resources/temp"));
	}

	private void deleteTemp(File tempDir) {
		if (tempDir.isDirectory()){
			for (File file : tempDir.listFiles())
				deleteTemp(file);
		}
		tempDir.delete();
	}
}
