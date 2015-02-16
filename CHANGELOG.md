# Change Log
All notable changes to this project will be documented in this file.

## [Unreleased][unreleased]
### Added
- Generative testing for font conversion functions

### Changed
- Font conversion information represented as map instead of seq
- தமிழ்->romanized transliteration for certain consonant clusters

### Fixed
- Test file namespace name
- Cljx configs for dirs for source and target for clj, cljs

## [0.1.1] - 2015-02-04
### Added
- 'Translations' of Clojure core library form names via a couple of maps and handful of macros
- Trie functions (create, get-in)
- Function using a trie to extract/convert a string into its elements
  - Fns to split a string into a sequence of Thamil letters/phonemes
  - Fn  to create a Thamil string from a sequence of phonemes
- Sorting fns and comparators for single- and multi-letter Thamil strings 
- Seq fns generalized from string functions (index-of, prefix)
- Function to adjust the cursor position in Thamil text
- Functions written in Thamil to perform Thamil grammatical operations
  - Pluralize, add suffixes generally, add noun case suffixes
- Letter frequency analysis and result output functions
- Functions to convert between old Thamil font character sets and Unicode
- Using cljx to be forward-compatible with compilation to JS via ClojureScript
- Configs for deploying GPG-signed releases to Clojars

[unreleased]: https://github.com/echeran/clj-thamil/compare/0.1.1...master
[0.1.1]: https://github.com/echeran/clj-thamil/tree/0.1.1
