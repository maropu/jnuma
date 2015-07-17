/*
 * Copyright 2012 Taro L. Saito
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xerial.jnuma;

import java.nio.ByteBuffer;

/**
 * Native code interface. Do not describe this class name in an import statement
 * or source codes, because it will break the native code loading mechanism.
 * Use static methods in {@link xerial.jnuma.Numa} to access NUMA.
 *
 * @author Taro L. Saito
 */
public class NumaNative implements NumaInterface {

    // The functions below are correspoinding to those in NumaNative.c
    @Override public native boolean isAvailable();
    @Override public native int maxNode();
    @Override public native long nodeSize(int node);
    @Override public native long freeSize(int node);
    @Override public native int distance(int node1, int node2);
    @Override public native void runOnNode(int node);
    @Override public native int preferredNode();
    @Override public native void setPreferred(int node);
    @Override public native void setLocalAlloc();
    @Override public native long allocMemory(long capacity);
    @Override public native void free(long address, long capacity);
    @Override public native ByteBuffer alloc(int capacity);
    @Override public native ByteBuffer allocLocal(int capacity);
    @Override public native ByteBuffer allocOnNode(int capacity, int node);
    @Override public native ByteBuffer allocInterleaved(int capacity);
    @Override public native void free(ByteBuffer buf);
    @Override public native void toNodeMemory(Object array, int length, int node);

    // Used in a native code
    private void throwError(int errorCode) throws Exception {
        throw new NumaException(String.format("NUMA error occurred %d", errorCode));
    }
}
