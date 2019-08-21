package com.randazzo.mario.sparkbwt.jni;

import com.randazzo.mario.sparkbwt.util.Util;
import cz.adamh.utils.NativeUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.io.Serializable;

/**
 * 	This class provide a JNI for the functions that calculate 
 * Suffix Array and Partial Suffix Array.
 * 
 * @author Mario Randazzo
 *
 */
public class SAPartial implements Serializable {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 5251326424419860278L;

	//Load shared library
	static {
		try {

			if(SystemUtils.IS_OS_WINDOWS)
				NativeUtils.loadLibraryFromJar("/sapartial.dll");
			else if(SystemUtils.IS_OS_LINUX)
				NativeUtils.loadLibraryFromJar("/sapartial.so");
			else
				throw new Exception("Can't load sapartial library. Error in OS detection.");
		} catch (IOException e) {
			System.out.println("Can't load sapartial library. I/O Error.");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 *
	 * @param s a string
	 * @param p
	 * @param pSorted
	 * @param k the size of the alphabet of s
	 */
	public native static void calculatePartialSA(int[] s, int[] p, int[] pSorted, int k);
	
	
	/**
	 * 
	 * 
	 * @param s a string
	 * @param p
	 * @param k the size of the alphabet of s
	 * @return
	 */
	public static int[] calculatePartialSA(String s, int[] p, int k) {
		int[] pSorted = new int[p.length];
		
		calculatePartialSA(Util.str2array(s), p, pSorted, k);
		
		return pSorted;
	}
	
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