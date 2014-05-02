# EditorConfig Java Binding

This directory is for [EditorConfig][] Core Java Binding.

## EditorConfig Project

EditorConfig makes it easy to maintain the correct coding style when switching
between different text editors and between different projects.  The
EditorConfig project maintains a file format and plugins for various text
editors which allow this file format to be read and used by those editors.  For
information on the file format and supported text editors, see the
[EditorConfig website][EditorConfig].

## Build the Library and Generate the Doc

First be sure that the submodule is initialized:

    cd /path/to/editorconfig-core-java
    git submodule init
    git submodule update

With [Ant][]:

    ant && ant doc

The built jar file is in the `build` directory and the documentation is in the
`doc` directory.

## Use as a Library

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

A more complex example is in the `example` directory. There is an
[online documentation][] for API details, or you could run `ant doc` to
generate html documentation. The generated documentation will locate in `doc`
directory.

## License

All source files of the Java binding are distributed under the Apache license. See
LICENSE for details.

Copyright (C) 2012-2013, EditorConfig Team

[Ant]: http://ant.apache.org
[EditorConfig]: http://editorconfig.org
[online documentation]: http://javadocs.editorconfig.org
