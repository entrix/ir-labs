#!/bin/bash

# all input files in cp1251 encooding
# java -Dfile.encoding=cp1251 -jar ir-labs-jar.jar -lab 1 -file "fb2/*.fb2" -detector lratio poisson ttest -top 50|iconv -f cp1251 -t utf-8

# run the first lab with all detectors for all files in fb2 subdirectory and print the most 50 top hits
java -Dfile.encoding=cp1251 -jar ir-labs-jar.jar -lab 1 -file "fb2/*.fb2" -detector lratio poisson ttest -top 50
java -Dfile.encoding=cp1251 -jar target/ir-labs-jar.jar -lab 4 -file fb2/*.fb2 -estimator heldout  -ratio 0.7 -top 50  -spell simple -queryfile queries_cp1251>result
