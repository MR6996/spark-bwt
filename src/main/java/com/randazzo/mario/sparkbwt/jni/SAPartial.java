package com.randazzo.mario.sparkbwt.jni;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SAPartial {

	static {
		System.loadLibrary("sapartial");
	}

	public native static void getPartialSA(int[] s, int[] p, int[] pSorted);
	
	public native static void calculateSA(int[] s, int[] SA);

	public static void main(String[] args) throws IOException, URISyntaxException {
		URL path = SAPartial.class.getClassLoader().getResource("ecoli_genome.txt");
		
		byte[] sBytes = Files.readAllBytes(Paths.get(path.toURI()));
		
		int[] s = new int[sBytes.length];
		for(int i = 0; i < s.length; i++) 
			s[i] = (int)sBytes[i];
		
		List<Integer> pList = Stream.iterate(0, n -> n+1).limit(sBytes.length).collect(Collectors.collectingAndThen(
				Collectors.toCollection(ArrayList::new), 
				list -> {Collections.shuffle(list); return list;})
				).subList(0, sBytes.length/10);
		Collections.sort(pList);
		
		int[] p = new int[pList.size()];
		int[] pSorted = new int[p.length];
		for(int i = 0; i < pList.size(); i++) p[i] = pList.get(i);

		long start = System.currentTimeMillis();
		getPartialSA(s, p, pSorted);
		System.out.println("Time: " + (System.currentTimeMillis() - start)/1000.0 + " sec");
		
		for(int i = 0; i < pSorted.length && i < 15; i++) 
			System.out.print(pSorted[i] + ", ");
		System.out.println("\n\n");
	}
}