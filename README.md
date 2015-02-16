# clj-thamil

clj-thamil is a Clojure library designed to be a multi-purpose library for Thamil
computing.

It can be used for natural language processing, designing input
methods, the UI for text editing, etc.

It can also be used as a basis for programming in Clojure in the
Thamil language.

Currently, it can support the following:
* programming in Clojure (programming language) in Thamil (natural language)
* natural language processing for Thamil language text
  * split a string into Thamil letters (not characters) and phonemes
  * combine a sequence of Thamil phonemes back into a proper Thamil string
  * sort letters, words, etc. by Thamil alphabetical order
  * convert a string between Thamil (Unicode format) and:
    * English-transliterated formats
    * TAB format
    * TSCII format
    * Bamini format
    * Webulagam format
  * basic grammar functions - pluralize, add suffixes, and add noun
    case suffixes
* perform a letter frequency analysis on input Thamil text

For examples of programming in Thamil (natural language), see:
* `test/cljx/clj_thamil/demo/core_test.cljx` - a gradual
replacement of English into Thamil
* `test/cljx/clj_thamil/demo/trans_demo_01.cljx` - just about entirely
  in Thamil.  Demonstrates squaring numbers differently.
* `src/cljx/clj_thamil/மொழியியல்.cljx`- just about
entirely in Thamil.  Contains functions for basic grammar in Thamil
(making plurals, adding suffixes, adding noun case suffixes)

## Building

Compile the source into separate Clojure and ClojureScript sources using the
command `lein cljx`. 

Compile the source into an executable JAR file (runnable on the JVM,
based on Clojure sources) using the command `lein uberjar`.  This is
all you need to do to run one of the standalone processes or
create a jar artifact.  `lein install` will install the artifact and
pom.xml into your local Maven cache.

## Usage

### Examples

#### Java Examples

The Java example code is in the Maven project in this repostiory at
[`examples/java/java-examples`](examples/java/java-examples/README.md).
See that page for instructions and building and running.

### Command-line process

Currently, the only standalone process supported by the library is the
letter frequency analysis in the namespace
`clj-thamil.format.analysis`.

The frequency analysis program can be
run after building the uberjar by
```
cat input | java -jar <uberjar> freqs > output
```
where `<uberjar>` will be called something like
`clj-thamil-0.1.2-standalone.jar`.   The frequency analysis program
takes input from the standard input stream and outputs to the standard
output stream.

## Editing

For programming in Thamil, if using a computer running Mac OS X, use
the Aquamacs program (a Mac OS X-friendly version of Emacs) to ensure that support for Thamil letters works
correctly.  If installing the `clojure-mode` package for Clojure
support in Aquamacs, find the `clojure-mode.el` file in your Aquamacs
MELPA/ELPA repository, and replace it with the file
`emacs/clojure-mode.el` in this repository.  Then load the newly-saved
`clojure-mode.el` file in the Aquamacs package repository into
Aquamacs, and run the command `M-x emacs-lisp-byte-compile-and-load`. 

## License

Distributed under the Eclipse Public License, the same as Clojure.
