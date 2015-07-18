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

import java.lang.ref.ReferenceQueue;

public final class NumaMemoryReference extends MemoryReference {
    public final int node;

    /**
     * Create a phantom reference to NUMA memory
     * @param m the allocated NUMA memory
     * @param queue the reference queue to which GCed reference
     *              of the Memory will be inserted
     */
    public NumaMemoryReference(Memory m, ReferenceQueue<Memory> queue) {
        super(m, queue);
        assert(m instanceof NumaMemory);
        this.node = ((NumaMemory) m).node();
    }

    public Memory toMemory() {
        if(address != 0) return new NumaMemory(address, size, node);
        else return new NumaMemory();
    }

    public String name() { return "numa"; }
}