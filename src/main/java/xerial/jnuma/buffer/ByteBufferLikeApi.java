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

import xerial.jnuma.utils.PlatformDependent;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

abstract class ByteBufferLikeApi {
    private Memory m;

    public ByteBufferLikeApi(Memory m) {
        this.m = m;
    }

    public byte apply(int offset) {
        return getByte(offset);
    }

    public void update(int offset, byte value) {
        putByte(offset, value);
    }

    public byte apply(long offset) {
        return getByte(offset);
    }

    public void update(long offset, byte value) {
        putByte(offset, value);
    }

    public void release() {
        NumaBufferConfig.allocator.release(m);
        m = null;
    }

    public long address() {
        return m.address();
    }

    public long size() {
        return m.size();
    }

    public void clear() {
        fill(0, size(), (byte) 0);
    }

    public void fill(long offset, long length, byte value) {
        PlatformDependent.UNSAFE.setMemory(address() + offset, length, value);
    }

    public byte getByte(long offset) {
        return PlatformDependent.UNSAFE.getByte(address() + offset);
    }

    public short getShort(long offset) {
        return PlatformDependent.UNSAFE.getShort(address() + offset);
    }

    public int getInt(long offset) {
        return PlatformDependent.UNSAFE.getInt(address() + offset);
    }

    public float getFloat(long offset) {
        return PlatformDependent.UNSAFE.getFloat(address() + offset);
    }

    public long getLong(long offset) {
        return PlatformDependent.UNSAFE.getLong(address() + offset);
    }

    public double getDouble(long offset) {
        return PlatformDependent.UNSAFE.getDouble(address() + offset);
    }

    public void putByte(long offset, byte value) {
        PlatformDependent.UNSAFE.putByte(address() + offset, value);
    }

    public void putShort(long offset, short value) {
        PlatformDependent.UNSAFE.putShort(address() + offset, value);
    }

    public void putInt(long offset, int value) {
        PlatformDependent.UNSAFE.putInt(address() + offset, value);
    }

    public void putFloat(long offset, float value) {
        PlatformDependent.UNSAFE.putFloat(address() + offset, value);
    }

    public void putLong(long offset, long value) {
        PlatformDependent.UNSAFE.putLong(address() + offset, value);
    }

    public void putDouble(long offset, double value) {
        PlatformDependent.UNSAFE.putDouble(address() + offset, value);
    }

    /**
     * Return a ByteBuffer view of the specified range.
     * Writing to the returned ByteBuffer modifies
     * the contenets of this object.
     * @param offset
     * @param size
     * @return new ByteBuffer
     */
    public ByteBuffer toDirectByteBuffer(long offset, int size) {
        ByteBuffer bb = PlatformDependent.newDirectByteBuffer(address() + offset, size);
        bb.order(ByteOrder.nativeOrder());
        return bb;
    }
}
