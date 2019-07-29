package com.randazzo.mario.sparkbwt.jni.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.randazzo.mario.sparkbwt.BWT;
import com.randazzo.mario.sparkbwt.jni.SAPartial;

/**
 * Test class for native method in {@link SAPartial} class.
 * 
 * @author Mario Randazzo
 *
 */
public class SAPartialTest {
	
	/*
	 * 	Convert a int array to a string, each integer is interpreted as a char. 
	 * 
	 * @param s a int array
	 * @return a string that represents s 
	 */
	private static String array2str(int[] s) {
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < s.length; i++)
			builder.append((char)s[i]);
		
		return builder.toString();
	}
	
	/*
	 * 	Check if the suffixes indexed in partial array are sorted in ascendent order.
	 * 
	 * @param s a int array 
	 * @param partial the partial array of s
	 * @return true if all suffixes in partial are sorted, false otherwise.
	 */
	private static boolean check(String s, int[] partial) {
		System.out.println("Test string: " + s + "\n");
		
		//for(int i = 0; i < partial.length; i++) 
		//	System.out.println(test.substring(partial[i]));
		
		boolean isOk = true;
		for(int i = 0; i < partial.length-1; i++) 
			if(s.substring(partial[i]).compareTo(s.substring(partial[i+1])) > 0) {
				System.out.println("Found suffix " + partial[i] + " and " + partial[i+1]);
				System.out.println("S"+ partial[i] + " -> " + s.substring(partial[i]));
				System.out.println("S"+ partial[i+1] + " -> " +s.substring(partial[i+1]));
				isOk = false;
			}
		
		return isOk;
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
		for(int i = 0; i < sInts.length; i++) 
			sInts[i] = sBytes[i];
		
		String sString = array2str(sInts);
		
		BWT util = new BWT();
		
		Stream.iterate(0, n -> n+1)
			.limit(sBytes.length)
			.map( (i) -> {
				return new ImmutablePair<Long, Integer>(util.toRange(sString.substring(i, Math.min(i+3, sString.length())).toLowerCase()), i);
			})
			.collect(Collectors.groupingBy(
						ImmutablePair::getLeft
					)
			)
			.forEach( (k, list) -> {			
				int[] p = new int[list.size()];
				int[] pSorted = new int[p.length];
				for(int i = 0; i < list.size(); i++) p[i] = list.get(i).getRight();
								
				long start = System.currentTimeMillis();
				SAPartial.calculatePartialSA(sInts, p, pSorted, 256);
				System.out.println("Time ("+ p.length +" bytes): " + (System.currentTimeMillis() - start)/1000.0 + " sec");
				
				for(int i = 0; i < pSorted.length; i++) 
					System.out.print(pSorted[i] + ", ");
				System.out.println("\n");
				
				System.out.println("Suffix array check: " + check(sString, pSorted) + "\n\n");				
			});
		
	}

}
