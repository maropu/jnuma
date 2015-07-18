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

import xerial.jnuma.utils.PlatformDependent;

/**
 * A stub when accessing numa API is not supported in the system.
 * @author leo
 */
public class NoNuma implements NumaInterface {

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public int maxNode() {
        return 0;
    }

    @Override
    public int currentNode() {
        return 0;
    }

    @Override
    public int currentCpu() {
        return 0;
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
    public void getAffinity(int pid, long[] cpuBitMask, int numCPUs) {
        // do nothing
    }

    @Override
    public void setAffinity(int pid, long[] cpuBitMask, int numCPUs) {
        // do nothing
    }

    @Override
    public long allocate(long capacity) {
        return PlatformDependent.UNSAFE.allocateMemory(capacity);
    }

    @Override
    public long allocateLocal(long capacity) {
        return this.allocate(capacity);
    }

    @Override
    public long allocateOnNode(long capacity, int node) {
        return this.allocate(capacity);
    }

    @Override
    public long allocateInterleaved(long capacity) {
        return this.allocate(capacity);
    }

    @Override
    public void free(long address, long capacity) {
        PlatformDependent.UNSAFE.freeMemory(address);
    }

    @Override
    public void toNode(long address, int length, int node) {
        // do nothing
    }

    @Override
    public void toNode(Object array, int length, int node) {
        // do nothing
    }
}
