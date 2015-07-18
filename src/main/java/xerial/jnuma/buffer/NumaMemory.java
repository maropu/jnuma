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

import java.lang.ref.ReferenceQueue;

public final class NumaMemory implements Memory {

    private final long address;
    private final long size;
    private final int  node;

    // Create an empty memory
    public NumaMemory() {
        this.address = 0;
        this.size = 0;
        this.node = -1;
    }

    public NumaMemory(long address, long size, int node) {
        this.address = address;
        this.size = size;
        this.node = node;
    }

    @Override
    public long address() {
        return this.address;
    }

    @Override
    public long size() {
        return this.size;
    }

    public int node() {
        return this.node;
    }

    @Override
    public MemoryReference toRef(ReferenceQueue<Memory> queue) {
        return new NumaMemoryReference(this, queue);
    }

    @Override
    public void release() {
        if(this.address != 0) { Numa.free(address, size); }
    }
}
