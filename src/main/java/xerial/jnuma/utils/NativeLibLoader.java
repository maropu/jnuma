package xerial.jnuma.utils;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;

/**
 * Load a native library into JVM.
 */
public class NativeLibLoader {
  private final String TEMPDIR = "xerial.jnuma.tempdir";
  private final String NATIVELIBDIR = "/xerial/jnuma/native";
  private String libName;
  private boolean isLoaded;
  private File nativeLibFile;

  public NativeLibLoader(String name) {
    this.libName = name;
    isLoaded = false;
    nativeLibFile = null;
  }

  // This is an entry point of a loader and
  // it needs to be thread-safe.
  public void load() {
    if (isLoaded)
      return;
    try {
      nativeLibFile = findNativeLibrary();
      // Load a specified native library
      System.load(nativeLibFile.getAbsolutePath());
      isLoaded = true;
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  private File findNativeLibrary() {
    // Resolve the library file name with a suffix (e.g., dll, .so, etc.)
    String nativeLibraryName = System.mapLibraryName(libName);

    // Load an OS-dependent native library inside a jar file
    String nativeLibraryPath =
        NATIVELIBDIR + "/" + OSInfo.getNativeLibFolderPath();
    boolean hasNativeLib =
        NativeLibLoader.class.getResource(
            nativeLibraryPath + "/" + nativeLibraryName) != null;
    if(!hasNativeLib) {
        if(OSInfo.getOSName().equals("mac")) {
            // Fix for openjdk7+ for mac
            String altName = "lib" + libName + ".jnilib";
            if(NativeLibLoader.class.getResource(
                    nativeLibraryPath + "/" + altName) != null) {
                nativeLibraryName = altName;
                hasNativeLib = true;
            }
        }
    }
    if (!hasNativeLib) {
      throw new RuntimeException();
    }

    // Temporary folder for the native lib.
    final String tempFolder =
        new File(System.getProperty(
            TEMPDIR, System.getProperty("java.io.tmpdir"))).getAbsolutePath();

    // Extracts and load a native library inside the jar file
    return extractLibraryFile(
        nativeLibraryPath, nativeLibraryName, tempFolder);
  }

  /**
   * Extract the specified library file to the target folder.
   *
   * @param libFolderForCurrentOS
   * @param libraryFileName
   * @param targetFolder
   * @return
   */
  private File extractLibraryFile(
      String libFolderForCurrentOS, String libraryFileName,
      String targetFolder) {
    final String nativeLibraryFilePath =
        libFolderForCurrentOS + "/" + libraryFileName;

    // Attach UUID to the native library file to ensure multiple class
    // loaders can read it multiple times.
    final String uuid = UUID.randomUUID().toString();
    final String extractedLibFileName = String.format(
        "nativelib-%s-%s-%s", getVersion(), uuid, libraryFileName);

    File extractedLibFile = new File(targetFolder, extractedLibFileName);

    try {
      // Extract a native library file into the target directory
      InputStream reader = NativeLibLoader.class.getResourceAsStream(nativeLibraryFilePath);
      FileOutputStream writer = new FileOutputStream(extractedLibFile);
      try {
        byte[] buffer = new byte[8192];
        int bytesRead = 0;
        while ((bytesRead = reader.read(buffer)) != -1) {
          writer.write(buffer, 0, bytesRead);
        }
      } finally {
        // Delete the extracted lib file on JVM exit.
        extractedLibFile.deleteOnExit();

        if (writer != null)
          writer.close();
        if (reader != null)
          reader.close();
      }

      // Set executable (x) flag to enable Java to
      // load the native library.
      extractedLibFile.setReadable(true);
      extractedLibFile.setWritable(true, true);
      extractedLibFile.setExecutable(true);

      // Check whether the contents are properly copied
      // from the resource folder.
      {
        InputStream nativeIn = NativeLibLoader.class.getResourceAsStream(nativeLibraryFilePath);
        InputStream extractedLibIn = new FileInputStream(extractedLibFile);
        try {
          if (!contentsEquals(nativeIn, extractedLibIn))
            throw new IOException();
        } finally {
          if (nativeIn != null)
            nativeIn.close();
          if (extractedLibIn != null)
            extractedLibIn.close();
        }
      }
      return new File(targetFolder, extractedLibFileName);
    } catch (IOException e) {
      return null;
    }
  }

  private boolean contentsEquals(
      InputStream in1, InputStream in2) throws IOException {
    if (!(in1 instanceof BufferedInputStream)) {
      in1 = new BufferedInputStream(in1);
    }
    if (!(in2 instanceof BufferedInputStream)) {
      in2 = new BufferedInputStream(in2);
    }

    int ch = in1.read();
    while (ch != -1) {
      int ch2 = in2.read();
      if (ch != ch2)
        return false;
      ch = in1.read();
    }
    int ch2 = in2.read();
    return ch2 == -1;
  }

  /**
   * Get the library version by reading pom.properties
   * embedded in jar.
   *
   * @return the version string
   */
  public String getVersion() {
    String version = "unknown";
    try {
      URL versionFile = NativeLibLoader.class.getResource("/VERSION");
      if (versionFile != null) {
        Properties versionData = new Properties();
        versionData.load(versionFile.openStream());
        version = versionData.getProperty("version", version);
        if (version.equals("unknown"))
          version = versionData.getProperty("VERSION", version);
        version = version.trim().replaceAll("[^0-9M\\.]", "");
      }
    } catch (IOException e) {
      System.err.println(e);
    }
    return version;
  }
}
