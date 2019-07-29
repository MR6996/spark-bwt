package com.randazzo.mario.sparkbwt.jni;

import java.io.IOException;

import cz.adamh.utils.NativeUtils;

/**
 * 	This class provide a JNI for the functions that calculate 
 * Suffix Array and Partial Suffix Array.
 * 
 * @author Mario Randazzo
 *
 */
public class SAPartial {

	//Load shared library
	static {
		try {
			NativeUtils.loadLibraryFromJar("/sapartial.so");
		} catch (IOException ex) {
			try {
				NativeUtils.loadLibraryFromJar("/sapartial.dll");
			} catch (IOException e) {
				System.out.println("Can't load sapartial library!");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * 
	 * @param s
	 * @param p
	 * @param pSorted
	 * @param k
	 */
	public native static void calculatePartialSA(int[] s, int[] p, int[] pSorted, int k);
	
	/**
	 * 	Calculate the Suffix Array of string s in {0,...,k-1}, 
	 * the result is putted the array SA.
	 * 
	 * @param s an int array 
	 * @param SA an int array where put the result
	 * @param k the size of alphabet of s
	 */
	public native static void calculateSA(int[] s, int[] SA, int k);

}