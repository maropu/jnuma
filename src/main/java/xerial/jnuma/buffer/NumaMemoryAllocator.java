/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xerial.jnuma.buffer;

import xerial.jnuma.Numa;
import xerial.jnuma.utils.Logging;

import java.lang.ref.ReferenceQueue;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class NumaMemoryAllocator extends Logging {

    // Map from address -> MemoryReference
    private Map<Long, MemoryReference> mapAddrToRef = new ConcurrentHashMap<>();
    private ReferenceQueue<Memory> queue = new ReferenceQueue<>();

    // Hold the amount of allocated NUMA memory
    private AtomicLong[] numaAllocatedSize = new AtomicLong[Numa.numNodes()];

    {
        for (int i = 0; i < Numa.numNodes(); i++) {
            numaAllocatedSize[i] = new AtomicLong(0);
        }

        // Start a NumaMemory collector to releases the allocated memory
        // when the corresponding Memory object is collected by GC.
        Thread collector = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        MemoryReference ref = MemoryReference.class.cast(queue.remove());
                        release(ref);
                        assert(ref instanceof NumaMemoryReference);
                        logger.info(String.format(
                                "NUMA memory (address:%x, size:%d, node:%d) collected by GC",
                                ref.address, ref.size,
                                ((NumaMemoryReference) ref).node));
                    } catch(Exception e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
        });
        collector.setDaemon(true);
        collector.start();
    }

    // Return the amount of allocated memory in a givn node
    public long allocatedSize(int node) {
        return numaAllocatedSize[node].get();
    }

    public Memory allocate(long capacity, int node) {
        if(capacity == 0L) return new NumaMemory();
        final long address = Numa.allocateOnNode(capacity, node);
        NumaMemory m = new NumaMemory(address, capacity, node);
        register(m);
        return m;
    }

    public Memory allocate(long capacity) {
        return this.allocate(capacity, Numa.currentNode());
    }

    private void register(NumaMemory m) {
        MemoryReference ref = m.toRef(queue);
        mapAddrToRef.put(ref.address, ref);
        numaAllocatedSize[m.node()].getAndAdd(m.size());
    }

    public void release(Memory m) {
        synchronized(this) {
            final long address = m.address();
            if(mapAddrToRef.containsKey(address)) {
                numaAllocatedSize[((NumaMemory) m).node()].getAndAdd(-m.size());
                mapAddrToRef.remove(address);
                m.release();
            }
        }
    }

    public void release(MemoryReference ref) {
        release(ref.toMemory());
    }
}