#include <ctime>
#include <algorithm>

#include "include/com_randazzo_mario_sparkbwt_jni_PartialSorting.h"

/**
 * 	Stable sort aI array in bI array of size n with keys in {0,...,K} from in r
 *
 * */
void radixPass(int* c, int* aI, int* bI,
		int* r, int n, int K) {
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

/**
 * 	Check if array a and array b are not equal.
 *
 * */
bool isNotEqual(int* a, int* b, int n) {
	for (int i = 0; i < n; i++)
		if (a[i] != b[i])
			return true;

	return false;
}

/**
 *
 *
 * */
int assignNames(int* s, int* p, int* t, int sSize, int n, int K) {
	int* keys = new int[n];
	int* indices = new int[n];
	int* sortedIndices = new int[n];

	for (int i = 0; i < n; i++) indices[i] = i;

	// find the maximum lenght of substrings
	int lMax = sSize - p[n - 1]+ 1, l;
	for (int i = 1; i < n; i++) {
		l = p[i] - p[i - 1] + 1;
		if (l > lMax)
			lMax = l;
	}

	// sort lexicographically substrings
	int* c = new int[K + 1];              // counter array
	for (int i = lMax - 1; i >= 0; i--) {
		for (int j = 0; j < n; j++)
			if (p[j] + i < sSize)
				keys[j] = s[p[j] + i];
			else
				keys[j] = 0;

		radixPass(c, indices, sortedIndices, keys, n, K);
		std::swap(indices, sortedIndices);
	}

	std::swap(indices, sortedIndices);

	delete[] c;
	delete[] keys;
	delete[] indices;

	int name = 0;
	int* lastSubstring = new int[lMax],
			   *tmpSubstring = new int[lMax];
	for (int i = 0; i < lMax; i++) lastSubstring[i] = -1;

	for (int i = 0; i < n; i++) {
		int k = 0;
		for (int j = p[sortedIndices[i]]; j < std::min(p[sortedIndices[i]] + lMax, sSize); j++, k++)
			tmpSubstring[k] = s[j];

		for (; k < lMax; k++)
			tmpSubstring[k] = 0;

		if (isNotEqual(lastSubstring, tmpSubstring, lMax)) {
			name++;
			std::swap(lastSubstring, tmpSubstring);
		}

		t[sortedIndices[i]] = name;
	}

	delete[] sortedIndices;
	delete[] lastSubstring;
	delete[] tmpSubstring;

	return name;
}

JNIEXPORT void JNICALL Java_com_randazzo_mario_sparkbwt_jni_PartialSorting_calculatePartialSA(
		JNIEnv * env, jclass thiz, jintArray js, jintArray jp, jintArray jpsorted, jint K) {
	int sSize = (int) env->GetArrayLength(js);
	int pSize = (int) env->GetArrayLength(jp);

	int* s = reinterpret_cast<int*>(env->GetIntArrayElements(js, 0));	// input string
	int* p = reinterpret_cast<int*>(env->GetIntArrayElements(jp, 0));	// input indices (for partial array)
	int* pSorted = reinterpret_cast<int*>(env->GetIntArrayElements(jpsorted, 0));	// input indices (for partial array)

	int* t = new int[pSize];			// reducted string from s

	int tK = assignNames(s, p, t, sSize, pSize, (int)K);

	// construct the suffix array for t
	for(int i = 0; i < pSize; i++) pSorted[t[i]-1] = p[i];

	env->ReleaseIntArrayElements(js, reinterpret_cast<jint*>(s), 0);
	env->ReleaseIntArrayElements(jp, reinterpret_cast<jint*>(p), 0);
	env->ReleaseIntArrayElements(jpsorted, reinterpret_cast<jint*>(pSorted), 0);
	delete[] t;
}