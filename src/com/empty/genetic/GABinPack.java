package com.empty.genetic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.softtechdesign.ga.Chromosome;
import com.softtechdesign.ga.Crossover;
import com.softtechdesign.ga.GAException;
import com.softtechdesign.ga.GASequenceList;

/**
 * GABinPack program based on GALib library
 * @author 3mpty
 *
 */
public class GABinPack extends GASequenceList {

	static int elements[];
	static int bucketSize;

	public GABinPack() throws GAException {

		super(elements.length, // size of chromosome
				300, // population has N chromosomes
				0.7, // crossover probability
				10, // random selection chance % (regardless of fitness)
				2000, // max generations
				0, // num prelim runs (to build good breeding stock for
					// final/full run)
				25, // max generations per prelim run
				0.06, // chromosome mutation prob.
				0, // number of decimal places in chrom
				geneSpace(), // gene space (possible gene values)
				Crossover.ctTwoPoint, // crossover type
				true); // compute statisitics?

		// setInitialSequence();
	}

	static String geneSpace() {

		String tmp = "";

		for (int i = 0; i < elements.length; i++) {
			tmp += (char) (i + 65);
		}

		return tmp;
	}

	static List<String> loadTextFile(String filename) {

		List<String> contents = new ArrayList<String>();

		try {
			BufferedReader input = new BufferedReader(new FileReader(filename));

			try {
				String line = null;

				while ((line = input.readLine()) != null) {
					contents.add(line);
				}
			} finally {
				input.close();
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		System.out.println("Loading '" + filename + "' complete...");
		return contents;
	}

	static void parseFile(String filename) {
		List<String> fileData = loadTextFile(filename);

		if (fileData.size() < 3) {
			System.err.println("Bad input file!!!");
			System.exit(1);
		}

		bucketSize = Integer.parseInt(fileData.get(0));

		int dataLength = Integer.parseInt(fileData.get(1));

		if (dataLength != fileData.size() - 2) {
			System.err.println("Bad argument number!!!");
			System.exit(2);
		}

		elements = new int[dataLength];

		for (int i = 2; i < fileData.size(); i++) {
			elements[i - 2] = Integer.parseInt(fileData.get(i));
		}
	}

	protected double getFitness(int iChromIndex) {
		char genes[] = this.getChromosome(iChromIndex).getGenes();
		int bucketCount = 1;

		int free = bucketSize;

		for (int i = 0; i < genes.length; i++) {
			int size = getLenght(genes[i]);
			if (size > free) {
				bucketCount++;
				free = bucketSize;
			}
			free -= size;
		}
		return -bucketCount;
	}

	private static int getLenght(char c) {
		return elements[((int) c) - 65];
	}

	public static void main(String[] args) {

		parseFile("dane.txt");

		String startTime = new Date().toString();
		System.out.println("GABinPack GA..." + startTime);

		Chromosome fit = null;

		try {
			GABinPack binpack = new GABinPack();
			Thread threadBin = new Thread(binpack);
			threadBin.start();
			threadBin.join();
			fit = binpack.getFittestChromosome();
		} catch (GAException gae) {
			System.out.println(gae.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (fit != null) {
			String best = fit.toString();

			char[] genes = best.toCharArray();

			int binsCount = 1;
			int free = bucketSize;

			System.out.println();
			System.out.print("[");

			for (int i = 0; i < genes.length; i++) {
				int size = getLenght(genes[i]);
				if (size > free) {
					binsCount++;
					free = bucketSize;
					System.out.print("][");
				}
				System.out.print(" " + size + " ");
				free -= size;
			}

			System.out.println("] free space: " + free);
			System.out.println("Used bins: " + binsCount);

		}

		System.out.println("Process started at " + startTime
				+ ". Process completed at " + new Date().toString());
	}

}