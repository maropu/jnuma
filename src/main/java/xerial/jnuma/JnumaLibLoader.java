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

import xerial.jnuma.utils.NativeLibLoader;

import java.util.HashSet;
import java.util.Set;

/**
 * Load a native library into JVM.
 */
public class JnumaLibLoader {
  private static final Set<String> LIBNUMA_PATHS;

  static {
    LIBNUMA_PATHS = new HashSet<String>();
    LIBNUMA_PATHS.add(System.getProperty("xerial.junma.libnuma.path"));
    LIBNUMA_PATHS.add(System.getenv("NUMALIB_LIBRARY_PATH"));
    LIBNUMA_PATHS.add("/usr/lib/x86_64-linux-gnu/libnuma.so");
  }

  private static NativeLibLoader loader = new NativeLibLoader("jnuma");
  private static boolean isLibNumaLoaded = false;
  private static boolean isLoaded = false;

  static synchronized public void load() throws Exception {
    if (isLoaded) return;
    // Load a library for libnuma in advance
    // TODO: Why System.loadLibrary("numa") does not work correctly?
    for (String libPath : LIBNUMA_PATHS) {
      if (tryLoad(libPath)) {
        isLibNumaLoaded  = true;
        break;
      }
    }
    if (!isLibNumaLoaded)
      throw new RuntimeException();
    loader.load();
    isLoaded = true;
  }

  static boolean tryLoad(String name) {
    try {
      System.load(name);
    } catch (Throwable e) {
      return false;
    }
    return true;
  }
}
