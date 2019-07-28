#include <ctime>
#include <algorithm>
#include <iostream>

#include "sais/build/include/sais.h"
#include "sais/build/include/lfs.h"
#include "sais/build/include/sais_config.h"

#include "include/sa_partial.h"


void radixPass(sa_int32_t* c, sa_int32_t* aI, sa_int32_t* bI,
		sa_int32_t* r, sa_int32_t n, sa_int32_t K) {
	for (sa_int32_t i = 0; i <= K; i++) c[i] = 0;     	// reset counters

	for (sa_int32_t i = 0; i < n; i++) c[r[aI[i]]]++;	// count occurrences

	for (sa_int32_t i = 0, sum = 0; i <= K; i++) {     // exclusive prefix sums
		int t = c[i];
		c[i] = sum;
		sum += t;
	}

	for (sa_int32_t i = 0; i < n; i++)
		bI[c[r[aI[i]]]++] = aI[i];
}

bool isNotEqual(sa_int32_t* a, sa_int32_t* b, sa_int32_t n) {
	for (sa_int32_t i = 0; i < n; i++)
		if (a[i] != b[i])
			return true;

	return false;
}

sa_int32_t assignNames(sa_int32_t* s, sa_int32_t* p, sa_int32_t* t, sa_int32_t sSize, sa_int32_t n, sa_int32_t K) {
	sa_int32_t* keys = new sa_int32_t[n];
	sa_int32_t* indices = new sa_int32_t[n];
	sa_int32_t* sortedIndices = new sa_int32_t[n];

	for (sa_int32_t i = 0; i < n; i++) indices[i] = i;

	// find the maximum lenght of substrings
	sa_int32_t lMax = sSize - p[n - 1], l;
	for (sa_int32_t i = 1; i < n; i++) {
		l = p[i] - p[i - 1];
		if (l > lMax)
			lMax = l;
	}

	// sort lexicographically substrings
	sa_int32_t* c = new sa_int32_t[K + 1];              // counter array
	for (sa_int32_t i = lMax - 1; i >= 0; i--) {
		for (sa_int32_t j = 0; j < n; j++)
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

	sa_int32_t name = 0;
	sa_int32_t* lastSubstring = new sa_int32_t[lMax],
			   *tmpSubstring = new sa_int32_t[lMax];
	for (sa_int32_t i = 0; i < lMax; i++) lastSubstring[i] = -1;

	for (sa_int32_t i = 0; i < n; i++) {
		sa_int32_t k = 0;
		for (sa_int32_t j = p[sortedIndices[i]]; j < std::min(p[sortedIndices[i]] + lMax, sSize); j++, k++)
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

JNIEXPORT void JNICALL Java_com_randazzo_mario_sparkbwt_jni_SAPartial_getPartialSA (
		JNIEnv * env, jclass thiz, jintArray js, jintArray jp, jintArray jpsorted, jint K) {
	sa_int32_t sSize = (sa_int32_t) env->GetArrayLength(js);
	sa_int32_t pSize = (sa_int32_t) env->GetArrayLength(jp);

	sa_int32_t* s = reinterpret_cast<sa_int32_t*>(env->GetIntArrayElements(js, 0));	// input string
	sa_int32_t* p = reinterpret_cast<sa_int32_t*>(env->GetIntArrayElements(jp, 0));	// input indices (for partial array)
	sa_int32_t* pSorted = reinterpret_cast<sa_int32_t*>(env->GetIntArrayElements(jpsorted, 0));	// input indices (for partial array)

	sa_int32_t* t = new sa_int32_t[pSize];			// reducted string from s

	sa_int32_t tK = assignNames(s, p, t, sSize, pSize, (sa_int32_t)K);

	// construct the suffix array for t
	if(tK == pSize)
		for(int i = 0; i < pSize; i++) pSorted[t[i]-1] = p[i];
	else {
		// construct the suffix array for t
		sa_int32_t* tSA = new sa_int32_t[pSize];		// t suffix array
		if (sais_i32(t, tSA, pSize, tK+1) != 0) {
			fprintf(stderr, " Could not allocate memory.\n");
		}
		for(int i = 0; i < pSize; i++) pSorted[tSA[i]] = p[i];

		delete[] tSA;
	}


	env->ReleaseIntArrayElements(js, reinterpret_cast<jint*>(s), 0);
	env->ReleaseIntArrayElements(jp, reinterpret_cast<jint*>(p), 0);
	env->ReleaseIntArrayElements(jpsorted, reinterpret_cast<jint*>(pSorted), 0);
	delete[] t;
}

JNIEXPORT void JNICALL Java_com_randazzo_mario_sparkbwt_jni_SAPartial_calculateSA
	(JNIEnv* env, jclass thiz, jintArray js, jintArray jSA, jint K) {
	sa_int32_t n = (sa_int32_t) env->GetArrayLength(js);

	sa_int32_t* s = reinterpret_cast<sa_int32_t*>(env->GetIntArrayElements(js, 0));	// input string
	sa_int32_t* SA = reinterpret_cast<sa_int32_t*>(env->GetIntArrayElements(jSA, 0));	// input string

	if (sais_i32(s, SA, n, (sa_int32_t)K) != 0) {
		fprintf(stderr, "[sais] Could not allocate memory.\n");
	}

	env->ReleaseIntArrayElements(js, reinterpret_cast<jint*>(s), 0);
	env->ReleaseIntArrayElements(jSA, reinterpret_cast<jint*>(SA), 0);
}
