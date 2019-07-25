package com.randazzo.mario.sparkbwt.util;

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

import com.randazzo.mario.sparkbwt.jni.SAPartial;

public class Partial {

	private static void radixPass(int[] a, int[] aI, int[] bI, int[] r, int n, int K) {
		// count occurrences
		int[] c = new int[K + 1];              		// counter array
		for (int i = 0; i <= K; i++) c[i] = 0;     	// reset counters
		for (int i = 0; i < n; i++) c[r[aI[i]]]++;	// count occurrences

		for (int i = 0, sum = 0; i <= K; i++) {     // exclusive prefix sums 
			int t = c[i]; 
			c[i] = sum; 
			sum += t; 
		}

		for (int i = 0; i < n; i++) 
			bI[c[r[aI[i]]]++] = aI[i];
	}

	private static boolean isNotEqual(int[] a, int[] b) {
		for (int i = 0; i < a.length; i++)
			if (a[i] != b[i])
				return true;

		return false;
	}
	
	private static void swap(int[] a, int[] b) {
		int tmp;
		for(int i = 0; i < a.length; i++) {
			tmp = a[i];
			a[i] = b[i]; 
			b[i] = tmp;
		}
	}

	private static int assignNames(int[] s, int[] p, int[] t, int K) {
		int n = p.length;
		int[] keys = new int[n];

		int[] pIdx = new int[n];
		int[] pIdxSorted = new int[n];

		for (int i = 0; i < n; i++)
			pIdx[i] = i;

		// find the maximum lenght of substrings
		int lMax = s.length - p[n - 1], l;
		for (int i = 1; i < n; i++) {
			l = p[i] - p[i - 1];
			if (l > lMax)
				lMax = l;
		}

		// sort lexicographically substrings
		for (int i = lMax - 1; i >= 0; i--) {
			int j;
			for (j = 0; j < n; j++)
				if (p[j] + i < s.length)
					keys[j] = s[p[j] + i];
				else
					keys[j] = 0;

			radixPass(p, pIdx, pIdxSorted, keys, n, K);
			if(i != 0 ) swap(pIdx, pIdxSorted);
		}
		

		int name = 0;
		int[] lastSubstring = new int[lMax], 
			  tmpSubstring = new int[lMax];
		for (int i = 0; i < lMax; i++) lastSubstring[i] = -1;

		for (int i = 0; i < n; i++) {
			int k = 0;
	
			for (int j = p[i]; j < Math.min(p[i] + lMax, s.length); j++, k++)
				tmpSubstring[k] = s[j];

			for (; k < lMax; k++)
				tmpSubstring[k] = 0;

			if (isNotEqual(lastSubstring, tmpSubstring)) {
				name++;
				swap(lastSubstring, tmpSubstring);
			}

			t[pIdxSorted[i]] = name;
		}

		return name;
	}

	public static void calculateSAPartial(int[] s, int[] p, int [] pSorted) {
		int[] t = new int[p.length];		// reducted string from s

		int K = assignNames(s, p, t, 256);
		
		if(K == p.length)
			for(int i = 0; i < p.length; i++) pSorted[t[i]-1] = p[i];
		else {
			int[] tSA = new int[p.length];		// t suffix array
			SAPartial.calculateSA(t, tSA);
			for(int i = 0; i < p.length; i++) pSorted[tSA[i]] = p[i];
		}
	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		URL path = SAPartial.class.getClassLoader().getResource("ecoli_genome.txt");

		byte[] sBytes = Files.readAllBytes(Paths.get(path.toURI()));

		int[] s = new int[sBytes.length];
		for (int i = 0; i < s.length; i++)
			s[i] = (int) sBytes[i];

		List<Integer> pList = Stream.iterate(0, n -> n + 1).limit(sBytes.length)
				.collect(Collectors.collectingAndThen(Collectors.toCollection(ArrayList::new), list -> {
					Collections.shuffle(list);
					return list;
				})).subList(0, sBytes.length / 10);
		Collections.sort(pList);

		int[] p = new int[pList.size()];
		int[] pSorted = new int[p.length];
		for (int i = 0; i < pList.size(); i++)
			p[i] = pList.get(i);

		long start = System.currentTimeMillis();
		calculateSAPartial(s, p, pSorted);
		System.out.println("Time: " + (System.currentTimeMillis() - start)/1000.0 + " sec");

		for (int i = 0; i < pSorted.length && i < 15; i++)
			System.out.print(pSorted[i] + ", ");
		System.out.println("\n\n");
	}
}
