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

/** NUMA API Interface. */
public interface NumaInterface {

    // Check if NUMA aviable
    public boolean isAvailable();

    // Return the heightes number of NUMA node
    public int maxNode();

    // Return a current node of this thread
    public int currentNode();

    // Return the amount of total and free memory
    // in a given node
    public long nodeSize(int node);
    public long freeSize(int node);

    // Return penalty cost between nodes
    public int distance(int node1, int node2);

    // Run this thread in a given node
    public void runOnNode(int node);

    // Operate on preferred properties
    public int preferredNode();
    public void setPreferred(int node);
    public void setLocalAlloc();

    // Allocate and free raw memory
    public long allocate(long capacity);
    public long allocateLocal(long capacity);
    public long allocateOnNode(long capacity, int node);
    public long allocateInterleaved(long capacity);
    public void free(long address, long capacity);

    // Move a given memory range into the node
    public void toNode(long address, int length, int node);
    public void toNode(Object array, int length, int node);
}
