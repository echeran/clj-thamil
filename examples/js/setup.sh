#!/bin/sh

mkdir -p test01/js
lein do clean, cljx once, cljsbuild once
#cp -r ../../resources/public/js/clj-thamil.js ./test01/js
#cp -r ../../resources/public/js/out/* ./test01/js
rsync --recursive ../../resources/public/js/clj-thamil.js ./test01/js
rsync --recursive ../../resources/public/js/out/ ./test01/js
