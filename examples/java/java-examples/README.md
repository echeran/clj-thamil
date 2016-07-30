# Java Examples for clj-thamil

## Requirements

The Java example code requires the clj-thamil artifact to be built and installed.  Refer to the [Building](../../../README.md) section on how to build and install the artifact.

## Building

All of the Java examples can be built together by
```
lein clean
lein install
cd examples/java/java-examples
mvn clean package
```

Building the Java examples is separate from the clj-thamil artifact that they depend on.

## Usage

After following the build instructions above, a shaded jar/uberjar will be in the `target` subdirectory, but is not
in itself executable since it contains multiple main methods.
Instead, the uberjar should be provided in the classpath followed by
the class name of the example being run:
```
java -cp target/java-examples-1.0.jar clj_thamil.examples.java.WordSort01
java -cp target/java-examples-1.0.jar clj_thamil.examples.java.WordSort02
```

## Overview

* WordSort01 - sorts words based on Thamil alphabetical order
* WordSort02 - sorts words based on Thamil alphabetical order
