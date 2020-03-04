package com.randazzo.mario.sparkbwt.jni;

import com.randazzo.mario.sparkbwt.util.Util;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

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
 * <p>
 * See {@link com.randazzo.mario.sparkbwt.BWT}}.
 *
 * @author Mario Randazzo
 */
class SAPartialTest {

    /**
     * Check if the suffixes indexed in partial array are sorted in ascendant order.
     *
     * @param s       a int array
     * @param partial the partial array of s
     */
    private void assertOrder(String s, int[] partial) {
        for (int i = 0; i < partial.length - 1; i++)
            if (s.substring(partial[i]).compareTo(s.substring(partial[i + 1])) > 0) {
                throw new AssertionFailedError(String.format(
                        "Found suffix %d and %s\n" +
						"S%d -> %s\n" +
						"S%d -> %s",
						partial[i], partial[i + 1],
						partial[i], s.substring(partial[i]),
						partial[i + 1], s.substring(partial[i + 1]))
                );
            }
    }

    @Test
    void calculatePartialSATest() throws URISyntaxException, IOException {
        URL path = SAPartial.class.getClassLoader().getResource("test_genome.txt");

        assert path != null;
        byte[] sBytes = Files.readAllBytes(Paths.get(path.toURI()));

        int[] sInts = new int[sBytes.length];
        for (int i = 0; i < sInts.length; i++)
            sInts[i] = sBytes[i];

        String sString = Util.array2str(sInts);

        for (int nPartition = 2; nPartition < 20; nPartition += 2) {
            System.out.println("[+] Number of partitions: " + nPartition);
            UtilityTester test = new UtilityTester(7, nPartition);

            Stream.iterate(0, n -> n + 1).limit(sBytes.length)
                    .map((i) -> new ImmutablePair<>(test.toRange(sString, i), i))
                    .collect(Collectors.groupingBy(ImmutablePair::getLeft)).forEach((k, list) -> {
                int[] p = new int[list.size()];
                int[] pSorted = new int[p.length];
                for (int i = 0; i < list.size(); i++)
                    p[i] = list.get(i).getRight();

                long start = System.currentTimeMillis();
                SAPartial.calculatePartialSA(sInts, p, pSorted, 256);
                System.out.println(
                        "\tTime (" + p.length + " bytes): " + (System.currentTimeMillis() - start) / 1000.0 + " sec");


                System.out.println("[+] check correctness");
                assertOrder(sString, pSorted);
            });


            System.out.println("\n");
        }
    }


    static class UtilityTester {

		private int k;
		private int r;
		private int alphaSize = 4;
		private long maxValue;
		private HashMap<Character, Integer> alpha;

		UtilityTester(int k, int r) {
			super();
			this.k = k;
			this.r = r;

			maxValue = calcMaxValue();

			alpha = new HashMap<>();
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
	}
}
