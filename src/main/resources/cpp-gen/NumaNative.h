/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class xerial_jnuma_NumaNative */

#ifndef _Included_xerial_jnuma_NumaNative
#define _Included_xerial_jnuma_NumaNative
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    isAvailable
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_xerial_jnuma_NumaNative_isAvailable
  (JNIEnv *, jobject);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    maxNode
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_xerial_jnuma_NumaNative_maxNode
  (JNIEnv *, jobject);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    currentNode
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_xerial_jnuma_NumaNative_currentNode
  (JNIEnv *, jobject);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    currentCpu
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_xerial_jnuma_NumaNative_currentCpu
  (JNIEnv *, jobject);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    nodeSize
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_xerial_jnuma_NumaNative_nodeSize
  (JNIEnv *, jobject, jint);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    freeSize
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_xerial_jnuma_NumaNative_freeSize
  (JNIEnv *, jobject, jint);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    distance
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_xerial_jnuma_NumaNative_distance
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    runOnNode
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_xerial_jnuma_NumaNative_runOnNode
  (JNIEnv *, jobject, jint);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    preferredNode
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_xerial_jnuma_NumaNative_preferredNode
  (JNIEnv *, jobject);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    setPreferred
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_xerial_jnuma_NumaNative_setPreferred
  (JNIEnv *, jobject, jint);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    setLocalAlloc
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_xerial_jnuma_NumaNative_setLocalAlloc
  (JNIEnv *, jobject);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    getAffinity
 * Signature: (I[JI)V
 */
JNIEXPORT void JNICALL Java_xerial_jnuma_NumaNative_getAffinity
  (JNIEnv *, jobject, jint, jlongArray, jint);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    setAffinity
 * Signature: (I[JI)V
 */
JNIEXPORT void JNICALL Java_xerial_jnuma_NumaNative_setAffinity
  (JNIEnv *, jobject, jint, jlongArray, jint);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    allocate
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_xerial_jnuma_NumaNative_allocate
  (JNIEnv *, jobject, jlong);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    allocateLocal
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_xerial_jnuma_NumaNative_allocateLocal
  (JNIEnv *, jobject, jlong);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    allocateOnNode
 * Signature: (JI)J
 */
JNIEXPORT jlong JNICALL Java_xerial_jnuma_NumaNative_allocateOnNode
  (JNIEnv *, jobject, jlong, jint);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    allocateInterleaved
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_xerial_jnuma_NumaNative_allocateInterleaved
  (JNIEnv *, jobject, jlong);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    free
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_xerial_jnuma_NumaNative_free
  (JNIEnv *, jobject, jlong, jlong);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    toNode
 * Signature: (JII)V
 */
JNIEXPORT void JNICALL Java_xerial_jnuma_NumaNative_toNode__JII
  (JNIEnv *, jobject, jlong, jint, jint);

/*
 * Class:     xerial_jnuma_NumaNative
 * Method:    toNode
 * Signature: (Ljava/lang/Object;II)V
 */
JNIEXPORT void JNICALL Java_xerial_jnuma_NumaNative_toNode__Ljava_lang_Object_2II
  (JNIEnv *, jobject, jobject, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
