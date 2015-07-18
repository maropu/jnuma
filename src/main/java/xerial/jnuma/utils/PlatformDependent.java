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

package xerial.jnuma.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public final class PlatformDependent {

    public static final class UNSAFE {

        private UNSAFE() {}

        public static int getInt(long address) {
            return _UNSAFE.getInt(address);
        }

        public static void putInt(long address, int value) {
            _UNSAFE.putInt(address, value);
        }

        public static byte getByte(long address) {
            return _UNSAFE.getByte(address);
        }

        public static void putByte(long address, byte value) {
            _UNSAFE.putByte(address, value);
        }

        public static short getShort(long address) {
            return _UNSAFE.getShort(address);
        }

        public static void putShort(long address, short value) {
            _UNSAFE.putShort(address, value);
        }

        public static long getLong(long address) {
            return _UNSAFE.getLong(address);
        }

        public static void putLong(long address, long value) {
            _UNSAFE.putLong(address, value);
        }

        public static float getFloat(long address) {
            return _UNSAFE.getFloat(address);
        }

        public static void putFloat(long address, float value) {
            _UNSAFE.putFloat(address, value);
        }

        public static double getDouble(long address) {
            return _UNSAFE.getDouble(address);
        }

        public static void putDouble(long address, double value) {
            _UNSAFE.putDouble(address, value);
        }

        public static long allocateMemory(long size) {
            return _UNSAFE.allocateMemory(size);
        }

        public static void freeMemory(long address) {
            _UNSAFE.freeMemory(address);
        }

        public static void setMemory(long address, long size, byte value) {
            _UNSAFE.setMemory(address, size, value);
        }
    }

    // Reference to the Unsafe implementation
    private static final sun.misc.Unsafe _UNSAFE;

    static {
        sun.misc.Unsafe unsafeInstance = null;
        try {
            if (Class.forName("sun.misc.Unsafe") == null)
                throw new RuntimeException("sun.misc.Unsafe not found.");
            // Fetch theUnsafe object for Oracle and OpenJDK
            Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafeInstance = (sun.misc.Unsafe) field.get(null);
            if (unsafeInstance == null) {
                throw new RuntimeException("Unsafe is unavailable.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        _UNSAFE = unsafeInstance;
    }

    private static Constructor<?> findDirectByteBufferConstructor() {
        try {
          return Class.forName("java.nio.DirectByteBuffer")
                  .getDeclaredConstructor(Long.TYPE, Integer.TYPE);
        }
        catch(ClassNotFoundException e) {
            throw new IllegalStateException(
                    "Failed to find java.nio.DirectByteBuffer: "
                  + e.getMessage());
        }
        catch(NoSuchMethodException e) {
            throw new IllegalStateException(
                    "Failed to find java.nio.DirectByteBuffer constructor: "
                  + e.getMessage());
        }
    }

    private static Constructor<?> dbbCC = findDirectByteBufferConstructor();

    public static ByteBuffer newDirectByteBuffer(long address, int size) {
        dbbCC.setAccessible(true);
        Object b = null;
        try {
            b = dbbCC.newInstance(new Long(address), new Integer(size));
            return ByteBuffer.class.cast(b);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to create DirectByteBuffer: "
                  + e.getMessage());
        }
    }

  /**
   * Raise an exception bypassing compiler checks
   * for checked exceptions.
   */
  public static void throwException(Throwable t) {
    _UNSAFE.throwException(t);
  }
}
