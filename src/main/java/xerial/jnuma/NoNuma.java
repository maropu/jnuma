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

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * A stub when accessing numa API is not supported in the system.
 * @author leo
 */
public class NoNuma implements NumaInterface {

    // Reference to the Unsafe implementation
    static final Unsafe unsafe;

    static {
        Unsafe unsafeInstance = null;
        try {
            if (Class.forName("sun.misc.Unsafe") == null)
                throw new RuntimeException("sun.misc.Unsafe not found.");
            // Fetch theUnsafe object for Oracle and OpenJDK
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafeInstance = (Unsafe) field.get(null);
            if (unsafeInstance == null) {
                throw new RuntimeException("Unsafe is unavailable.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        unsafe = unsafeInstance;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public int maxNode() {
        return 1;
    }

    @Override
    public long nodeSize(int node) {
        return Runtime.getRuntime().maxMemory();
    }

    @Override
    public long freeSize(int node) {
        return Runtime.getRuntime().freeMemory();
    }

    @Override
    public int distance(int node1, int node2) {
        return 10;
    }

    @Override
    public void runOnNode(int node) {
        // do nothing
    }

    @Override
    public int preferredNode() {
        return 0;
    }

    @Override
    public void setPreferred(int node) {
        // do nothing
    }

    @Override
    public void setLocalAlloc() {
        // do nothing
    }

    @Override
    public long allocMemory(long capacity) {
        return unsafe.allocateMemory(capacity);
    }

    @Override
    public void free(long address, long capacity) {
        unsafe.freeMemory(address);
    }

    @Override
    public ByteBuffer alloc(int capacity) {
        return ByteBuffer.allocate(capacity);
    }

    @Override
    public ByteBuffer allocLocal(int capacity) {
        return alloc(capacity);
    }

    public ByteBuffer allocOnNode(int capacity, int node) {
        return allocLocal(capacity);
    }

    @Override
    public ByteBuffer allocInterleaved(int capacity) {
        return allocLocal(capacity);
    }

   @Override
    public void free(ByteBuffer buf) {
        // Simply clear the buffer and let the GC collect the freed memory.
        buf.clear();
    }

    @Override
    public void toNodeMemory(Object array, int length, int node) {
        // do nothing
    }
}
