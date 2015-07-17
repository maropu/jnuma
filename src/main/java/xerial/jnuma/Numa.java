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
import java.util.logging.Logger;

import xerial.jnuma.utils.OSInfo;

/**
 * Numa API.
 *
 *
 * When allocating new {@link java.nio.ByteBuffer}s using this API,
 * you must release these buffers by calling {@link Numa#free(java.nio.ByteBuffer)} because
 * the allocated buffers are out of control of the GC of the JVM.
 *
 * @author Taro L. Saito
 */
public class Numa {

    /**
     * This type must be Object rather than NumaInterface because some JVM implementation loads the the class
     * specified in the field before the completion of the injection of NumaInterface and NumaNative to the root class loader.
     */
    private static NumaInterface impl = null;

    private static Logger logger =
            Logger.getLogger(Numa.class.getName());

    static {
        if (OSInfo.getOSName().equals("linux")
                && OSInfo.getArchName().equals("x86_64")) {
            try {
                JnumaLibLoader.load();
                impl = new NumaNative();
            } catch (Exception e) {
                logger.warning("Can't load a jnuma native library into JVM");
            }
        }
        if (impl == null) {
            impl = new NoNuma();
        }
    }

    /**
     * Returns true if the NUMA is available in this machine
     * @return
     */
    public static boolean isAvailable() {
        return ((NumaInterface) impl).isAvailable();
    }

    /**
     * Max number of numa nodes (local memories)
     * @return
     */
    public static int numNodes() {
        return ((NumaInterface) impl).maxNode()+1;
    }

    /**
     * The memory size of the node
     * @param node node number
     * @return memory byte size
     */
    public static long nodeSize(int node) {
        return ((NumaInterface) impl).nodeSize(node);
    }

    /**
     * The free memory size of the node
     * @param node node number
     * @return free memory byte size
     */
    public static long freeSize(int node) {
        return ((NumaInterface) impl).freeSize(node);
    }

    /**
     * Distance between two NUMA nodes. The distance is a multiple of 10s
     * @param node1
     * @param node2
     * @return node distance
     */
    public static int distance(int node1, int node2) {
        return ((NumaInterface) impl).distance(node1, node2);
    }

    /**
     * Get the number of CPUs available to this machine
     * @return
     */
    public static int numCPUs() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * Create a bit mask for specifying CPU sets. From the LSB, it corresponds CPU0, CPU1, ...
     * @return
     */
    public static long[] newCPUBitMask() {
        int bufSize = (numCPUs() + 64 -1)/ 64;
        return new long[bufSize];
    }

    /**
     * Returns the preferred node of the current thread.
     * @return preferred numa node
     */
    public static int getPreferredNode() {
        return ((NumaInterface) impl).preferredNode();
    }

    /**
     * Set the memory allocation policy for the calling thread to local allocation.
     */
    public static void setLocalAlloc() {
        ((NumaInterface) impl).setLocalAlloc();
    }

    /**
     * Sets the preferred node for the current thread. The system will attempt to allocate memory from the preferred node, but will fall back to other
     * nodes if no memory is available on the preferred node. To reset the node preference, pass a node of -1 argument or call {@link #setLocalAlloc()} .
     * @param node
     */
    public static void setPreferred(int node) {
        ((NumaInterface) impl).setPreferred(node);
    }

    /**
     * Run the current thread and its children on a specified node. To reset the binding call {@link #runOnAllNodes()}
     * @param node
     */
    public static void runOnNode(int node) {
        ((NumaInterface) impl).runOnNode(node);
    }

    public static void runOnAllNodes() {
        runOnNode(-1);
    }


    /**
     * Allocate a new NUMA buffer using the current policy. You must release the acquired buffer by {@link #free(java.nio.ByteBuffer)} because
     * it is out of the control of GC.
     * @param capacity byte size of the buffer
     * @return new ByteBuffer
     */
    public static ByteBuffer alloc(int capacity) {
        return ((NumaInterface) impl).alloc(capacity);
    }

    /**
     * Allocate a new local NUMA buffer. You must release the acquired buffer by {@link #free(java.nio.ByteBuffer)} because
     * it is out of the control of GC.
     * @param capacity byte size of the buffer
     * @return new ByteBuffer
     */
    public static ByteBuffer allocLocal(int capacity) {
        return ((NumaInterface) impl).allocLocal(capacity);
    }

    /**
     * Allocate a new NUMA buffer on a specific node. You must release the acquired buffer by {@link #free(java.nio.ByteBuffer)} because
     * it is out of the control of GC.
     * @param capacity byte size of the buffer
     * @param node node number
     * @return new ByteBuffer
     */
    public static ByteBuffer allocOnNode(int capacity, int node) {
        return ((NumaInterface) impl).allocOnNode(capacity, node);
    }

    /**
     * Allocate a new NUMA buffer interleaved on multiple NUMA nodes. You must release the acquired buffer by {@link #free(java.nio.ByteBuffer)} because
     * it is out of the control of GC.
     * @param capacity byte size of the buffer
     * @return new ByteBuffer
     */
    public static ByteBuffer allocInterleaved(int capacity) {
        return ((NumaInterface) impl).allocInterleaved(capacity);
    }

    /**
     * Allocate a new NUMA buffer of the specified capacity
     * @param capacity
     * @return the raw memory address
     */
    public static long allocMemory(long capacity) {
        return ((NumaInterface) impl).allocMemory(capacity);
    }

    /**
     * Release the memory resource allocated at the specified address and capacity.
     * @param address
     * @param capacity
     */
    public static void free(long address, long capacity) {
        ((NumaInterface) impl).free(address, capacity);
    }

    /**
     * Release the memory resources of the numa ByteBuffer.
     * @param buf the buffer to release
     */
    public static void free(ByteBuffer buf) {
        ((NumaInterface) impl).free(buf);
    }


    /**
     * Send the array to a node. The array should be primitive type array. To send the array to a node correctly,
     * the array should not be touched before calling this method.
     * @param array
     * @param byteLength
     * @param node
     */
    public static void toNodeMemory(Object array, int byteLength, int node) {
        ((NumaInterface) impl).toNodeMemory(array, byteLength, node);
    }



}
