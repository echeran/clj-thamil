#!/bin/sh

mkdir -p js
lein do clean, cljx once, cljsbuild once
#cp -r ../../resources/public/js/clj-thamil.js ./js
#cp -r ../../resources/public/js/out/* ./js
rsync --recursive ../../resources/public/js/clj-thamil.js ./js
rsync --recursive ../../resources/public/js/out/ ./js
