package com.randazzo.mario.sparkbwt.util;


/**
 * This class provide some utility methods.
 * 
 * @author Mario Randazzo
 *
 */
public class Util {
	
	/**
	 * 	Convert a int array to a string, each integer is interpreted as a char. 
	 * 
	 * @param s a int array
	 * @return a string that represents s 
	 */
	public static String array2str(int[] s) {
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < s.length; i++)
			builder.append((char)s[i]);
		
		return builder.toString();
	}
	
	/**
	 * 
	 * 
	 * @param str
	 * @return
	 */
	public static int[] str2array(String str) {
		int[] result = new int[str.length()];
		
		for(int i = 0; i < str.length(); i++)
			result[i] = (int)str.charAt(i);
		
		return result;
	}
	
	/**
	 * 	Check if the suffixes indexed in partial array are sorted in ascendent order.
	 * 
	 * @param s a int array 
	 * @param partial the partial array of s
	 * @return true if all suffixes in partial are sorted, false otherwise.
	 */
	public static boolean check(String s, int[] partial) {
		System.out.println("Test string: " + s + "\n");
				
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
	
}
