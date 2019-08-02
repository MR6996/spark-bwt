package com.randazzo.mario.sparkbwt.jni.test;

import com.randazzo.mario.sparkbwt.BWT;
import com.randazzo.mario.sparkbwt.jni.SAPartial;
import com.randazzo.mario.sparkbwt.util.Util;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Test class for native method in {@link SAPartial} class.
 * 
 * See {@link BWT}}.
 * 
 * @author Mario Randazzo
 *
 */
public class SAPartialTest {

	private int k;
	private int r;
	private int alphaSize = 4;
	private long maxValue;
	private HashMap<Character, Integer> alpha;

	public SAPartialTest(int k, int r) {
		super();
		this.k = k;
		this.r = r;
		
		maxValue = calcMaxValue();
		
		alpha = new HashMap<Character, Integer>();
		alpha.put('A', 0);
		alpha.put('C', 1);
		alpha.put('G', 2);
		alpha.put('T', 3);
	}

	private long calcMaxValue() {
		long max = 0;
		for (int i = k - 1; i >= 0; i--)
			max = max + (alphaSize - 1) * (long) Math.pow(alphaSize, i);

		return max;
	}

	private long toRange(String s, int i) {
		long value = 0;

		for (int j = k - 1, l = i; j >= 0 && l < Math.min(i + k - 1, s.length() - 1); j--, l++)
			value = value + alpha.get(s.charAt(l)) * (long) Math.pow(alphaSize, j);

		return Math.round(value / (double) maxValue * (r - 1));
	}

	/**
	 * 
	 * 
	 * @param args
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void main(String[] args) throws IOException, URISyntaxException {
		URL path = SAPartial.class.getClassLoader().getResource("test_genome.txt");

		byte[] sBytes = Files.readAllBytes(Paths.get(path.toURI()));

		int[] sInts = new int[sBytes.length];
		for (int i = 0; i < sInts.length; i++)
			sInts[i] = sBytes[i];

		String sString = Util.array2str(sInts);

		SAPartialTest test = new SAPartialTest(3, 4);

		Stream.iterate(0, n -> n + 1).limit(sBytes.length).map((i) -> {
			return new ImmutablePair<Long, Integer>(test.toRange(sString, i), i);
		}).collect(Collectors.groupingBy(ImmutablePair::getLeft)).forEach((k, list) -> {
			int[] p = new int[list.size()];
			int[] pSorted = new int[p.length];
			for (int i = 0; i < list.size(); i++)
				p[i] = list.get(i).getRight();

			long start = System.currentTimeMillis();
			SAPartial.calculatePartialSA(sInts, p, pSorted, 256);
			System.out.println(
					"Time (" + p.length + " bytes): " + (System.currentTimeMillis() - start) / 1000.0 + " sec");

            for (int value : pSorted) System.out.print(value + ", ");
			System.out.println("\n");

			System.out.println("Suffix array check: " + Util.check(sString, pSorted) + "\n\n");
		});

	}

}
