package com.randazzo.mario.sparkbwt.util;

import com.randazzo.mario.sparkbwt.jni.SAPartial;

public class Partial {

	private static void radixPass(int[] aI, int[] bI, int[] r, int n, int K) {
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
		int lMax = s.length - p[n - 1] +1, l;
		for (int i = 1; i < n; i++) {
			l = p[i] - p[i - 1] + 1;
			if (l > lMax)
				lMax = l;
		}

		System.out.println("lMax: " + lMax);
		
		// sort lexicographically substrings
		for (int i = lMax - 1; i >= 0; i--) {
			for (int j = 0; j < n; j++)
				if (p[j] + i < s.length)
					keys[j] = s[p[j] + i];
				else
					keys[j] = 0;

			radixPass(pIdx, pIdxSorted, keys, n, K);
			if(i != 0 ) swap(pIdx, pIdxSorted);
		}

		int name = 0;
		int[] lastSubstring = new int[lMax], 
			  tmpSubstring = new int[lMax];
		for (int i = 0; i < lMax; i++) lastSubstring[i] = -1;

		for (int i = 0; i < n; i++) {
			int k = 0;
	
			for (int j = p[pIdxSorted[i]]; j < Math.min(p[pIdxSorted[i]] + lMax, s.length); j++, k++)
				tmpSubstring[k] = s[j];

			for (; k < lMax; k++)
				tmpSubstring[k] = 0;

			if (isNotEqual(lastSubstring, tmpSubstring)) {
				name++;

				System.out.println(name + " : " + array2str(tmpSubstring));
				
				swap(lastSubstring, tmpSubstring);
			}
			t[pIdxSorted[i]] = name;
		}

		return name;
	}

	private static String array2str(int[] s) {
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < s.length; i++)
			builder.append((char)s[i]);
		
		return builder.toString();
	}
	
	public static void calculateSAPartial(int[] s, int[] p, int [] pSorted) {
		int[] t = new int[p.length];		// Reduced string from s

		int K = assignNames(s, p, t, 256);
		
		if(K == p.length)
			for(int i = 0; i < p.length; i++) pSorted[t[i]-1] = p[i];
		else {
			int[] tSA = new int[p.length];		// t suffix array
			SAPartial.calculateSA(t, tSA, K+1);
			for(int i = 0; i < p.length; i++) pSorted[i] = p[tSA[i]];
		}
	}

}
