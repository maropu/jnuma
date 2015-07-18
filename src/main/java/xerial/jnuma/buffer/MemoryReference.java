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

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

/**
 * Phantom reference to the allocated memory that will be queued
 * to the ReferenceQueue upon GC time.
 */
abstract class MemoryReference extends PhantomReference<Memory> {
    public final long address;
    public final long size;

    /**
     * Create a phantom reference for a given Memory.
     * @param m the allocated memory
     * @param queue the reference queue to which GCed reference
     *              of the Memory will be put
     */
    public MemoryReference(Memory m, ReferenceQueue<Memory> queue) {
        super(m, queue);
        this.address = m.address();
        this.size = m.size();
    }

    abstract public Memory toMemory();
    abstract public String name();
}
