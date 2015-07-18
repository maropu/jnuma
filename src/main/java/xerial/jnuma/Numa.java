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

import xerial.jnuma.utils.Logging;
import xerial.jnuma.utils.OSInfo;

/** Numa API. */
public class Numa extends Logging {

    // The NUMA API implementation
    private static NumaInterface impl = null;

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
     * Returns true if the NUMA is available in this machine.
     */
    public static boolean isAvailable() {
        return impl.isAvailable();
    }

    /**
     * Max number of numa nodes (local memories).
     * @return
     */
    public static int numNodes() {
        return impl.maxNode() + 1;
    }

    /**
     * Return a current node of this thread.
     * @return
     */
    public static int currentNode() {
        return impl.currentNode();
    }

    /**
     * Get the number of CPUs available to this machine.
     * @return
     */
    public static int numCPUs() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * The memory size of the node.
     * @param node node number
     * @return memory byte size
     */
    public static long nodeSize(int node) {
        return impl.nodeSize(node);
    }

    /**
     * The free memory size of the node.
     * @param node node number
     * @return free memory byte size
     */
    public static long freeSize(int node) {
        return impl.freeSize(node);
    }

    /**
     * Distance between two NUMA nodes and the distance
     * is a multiple of 10s.
     * @param node1
     * @param node2
     * @return node distance
     */
    public static int distance(int node1, int node2) {
        return impl.distance(node1, node2);
    }

    /**
     * Returns the preferred node of the current thread.
     * @return preferred numa node.
     */
    public static int getPreferredNode() {
        return impl.preferredNode();
    }

    /**
     * Sets the preferred node for the current thread.
     * The system will attempt to allocate memory from the preferred node,
     * but will fall back to other nodes if no memory is available
     * on the preferred node. To reset the node preference,
     * pass a node of -1 argument or call {@link #setLocalAlloc()}.
     * @param node
     */
    public static void setPreferred(int node) {
        impl.setPreferred(node);
    }

    /**
     * Set the memory allocation policy for the calling thread
     * to local allocation.
     */
    public static void setLocalAlloc() {
        impl.setLocalAlloc();
    }

    /**
     * Set the affinity of this thread to a single CPU.
     * @param cpu cpu number
     */
    public static void setAffinity(int cpu) {
        setAffinityImpl(newCPUBitMaskForOneCPU(cpu));
    }

    /**
     * Reset the affinity of the current thread to CPUs.
     */
    public static void resetAffinity() {
        impl.setAffinity(0, newCPUBitMaskForAllCPUs(), numCPUs());
    }

    /**
     * Set the affinity of this thread to CPUs specified
     * in the bit vector.
     * @param cpuBitMask bit vector.
     */
    private static void setAffinityImpl(long[] cpuBitMask) {
        impl.setAffinity(0, cpuBitMask, numCPUs());
    }

    /**
     * Create a bit mask for specifying CPU sets.
     * From the LSB, it corresponds CPU0, CPU1, CPU2, ...
     */
    private static long[] newCPUBitMask() {
        return new long[(numCPUs() + 64 -1) / 64];
    }

    /**
     * Create a bit mask setting a single CPU on.
     * @param cpu number
     */
    private static long[] newCPUBitMaskForOneCPU(int cpu) {
        long[] cpuMask = newCPUBitMask();
        cpuMask[cpu / 64] |= 1L << (cpu % 64);
        return cpuMask;
    }

    /**
     * Create a bit mask setting all CPUs on.
     */
    private static long[] newCPUBitMaskForAllCPUs() {
        long[] cpuMask = newCPUBitMask();
        int M = numCPUs();
        for(int i=0; i<cpuMask.length; ++i)
            cpuMask[i] |= (i*64 > M) ? ~0L : ~(~0L << (M % 64));
        return cpuMask;
    }

    /**
     * Run the current thread and its children on a specified node.
     * To reset the binding call {@link #runOnAllNodes()}.
     * @param node
     */
    public static void runOnNode(int node) {
        impl.runOnNode(node);
    }

    public static void runOnAllNodes() {
        runOnNode(-1);
    }

    /**
     * Allocate a new NUMA buffer of the specified capacity.
     */
    public static long allocate(long capacity) {
        return impl.allocate(capacity);
    }
    public static long allocateLocal(long capacity) {
        return impl.allocateLocal(capacity);
    }
    public static long allocateOnNode(long capacity, int node) {
        return impl.allocateOnNode(capacity, node);
    }
    public static long allocateInterleaved(long capacity) {
        return impl.allocateInterleaved(capacity);
    }

    /**
     * Release the memory resource allocated at the specified
     * address and capacity.
     */
    public static void free(long address, long capacity) {
        impl.free(address, capacity);
    }

    /**
     * Move given memory range to a node.
     */
    public static void toNode(long address, int byteLength, int node) {
        impl.toNode(address, byteLength, node);
    }

    /**
     * Send the primitive-typed array to a given node.
     * To send the array to a node correctly,
     * the array should not be touched before calling this method.
     */
    public static void toNode(Object array, int byteLength, int node) {
        impl.toNode(array, byteLength, node);
    }
}
