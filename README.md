# EditorConfig Java Binding

This directory is for [EditorConfig][] Core Java Binding.

## EditorConfig Project

EditorConfig makes it easy to maintain the correct coding style when switching
between different text editors and between different projects.  The
EditorConfig project maintains a file format and plugins for various text
editors which allow this file format to be read and used by those editors.  For
information on the file format and supported text editors, see the
[EditorConfig website][EditorConfig].

## How to use EditorConfig Core in Java

Add the `editorconfig-core` dependency to your `pom.xml` file:

```xml
  ...
  <dependencies>
    <dependency>
      <groupId>org.editorconfig</groupId>
      <artifactId>editorconfig-core</artifactId>
      <version><!-- lookup the newest version on http://mvnrepository.com/artifact/org.editorconfig/editorconfig-core --></version>
    </dependency>
  </dependencies>
```

A basic example:

```java
EditorConfig ec = new EditorConfig();
List<EditorConfig.OutPair> l = null;
try {
    l = ec.getProperties("/home/user/src/editorconfig-core-py/a.py");
} catch(EditorConfigException e) {
    System.out.println(e);
    System.exit(1);
}

for(int i = 0; i < l.size(); ++i) {
    System.out.println(l.get(i).getKey() + "=" + l.get(i).getVal());
}
```
There is an [online documentation][] for API details.

## Build EditorConfig Core Java librarary

Prerequisistes: Java 6, [Maven][], Git, cmake 2.6+ (optional for tests)

Checkout the code

    git clone https://github.com/editorconfig/editorconfig-core-java.git

Build the library with [Maven][]:

    cd editorconfig-core-java
    mvn clean install

The built jar file is in the `target` directory.

## Run the testsuite

First make sure that the submodule is initialized:

    cd /path/to/editorconfig-core-java
    git submodule init
    git submodule update

Then prepare and run the tests using `cmake`:

    cmake .
    ctest .

## How to Contribute

Pull requests are welcome on [GitHub](https://github.com/editorconfig/editorconfig-core-java).


## License

All source files of the Java binding are distributed under the Apache license. See
LICENSE for details.

Copyright (C) 2012-2013, EditorConfig Team

[Maven]: https://maven.apache.org
[EditorConfig]: http://editorconfig.org
[online documentation]: http://javadocs.editorconfig.org
