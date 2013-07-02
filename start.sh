#!/bin/bash

# all input files in cp1251 encooding
# java -Dfile.encoding=cp1251 -jar ir-labs-jar.jar -lab 1 -file "fb2/*.fb2" -detector lratio poisson ttest -top 50|iconv -f cp1251 -t utf-8

# run the first lab with all detectors for all files in fb2 subdirectory and print the most 50 top hits
java -Dfile.encoding=cp1251 -jar ir-labs-jar.jar -lab 1 -file "fb2/*.fb2" -detector lratio poisson ttest -top 50
