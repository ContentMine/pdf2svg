#!/bin/sh

# to use this shell script, create an install location (e.g. /opt/pdf2svg)
# then create a bin and lib directory within this location. copy this script
# into the bin directory as "pdf2svg" and ensure it is executable and that
# the variables PDF2SVG_HOME, JAVA_HOME and JAVA_OPTS have the correct
# values, then copy all the required jar files into the lib directory (see
# list below - NOTE: the pom.xml contains a maven plugin that will collect
# these jars for you into a dir called "target/lib", as part of the install
# goal, but it is commented out). finally, put "$PDF2SVG_HOME/bin" into
# your PATH.

PDF2SVG_HOME="${PDF2SVG_HOME-/opt/pdf2svg}"
JAVA_HOME="${JAVA_HOME-/usr/lib/jvm/java-6-sun}"
jopts="-Djava.awt.headless=true"	  # font graphics accesses X11 display
jopts="${jopts} -XX:+UseConcMarkSweepGC"  # prevent OOM?
jopts="${jopts} -XX:-UseGCOverheadLimit"  # prevent OOM?
jopts="${jopts} ${JAVA_HEAPMIN--Xms128m}" # initial heap
jopts="${jopts} ${JAVA_HEAPMAX--Xmx512m}" # use 1024m for lots of big docs
JAVA_OPTS="${JAVA_OPTS-${jopts}}"
export PDF2SVG_HOME JAVA_HOME JAVA_OPTS

lib="$PDF2SVG_HOME/lib"
cp="$lib/pdf2svg-0.1-SNAPSHOT.jar"
cp="${cp}:$lib/bcmail-jdk15-1.44.jar"
cp="${cp}:$lib/bcprov-jdk15-1.44.jar"
cp="${cp}:$lib/cmlxom-3.2-SNAPSHOT.jar"
cp="${cp}:$lib/commons-io-2.0.1.jar"
cp="${cp}:$lib/commons-logging-1.1.1.jar"
cp="${cp}:$lib/commons-math-2.2.jar"
cp="${cp}:$lib/euclid-1.1-SNAPSHOT.jar"
cp="${cp}:$lib/fontbox-1.7.1.jar"
cp="${cp}:$lib/guava-13.0.1.jar"
cp="${cp}:$lib/html-0.1-SNAPSHOT.jar"
cp="${cp}:$lib/javatuples-1.2.jar"
cp="${cp}:$lib/jempbox-1.7.1.jar"
cp="${cp}:$lib/joda-time-1.6.2.jar"
cp="${cp}:$lib/jtidy-4aug2000r7-dev.jar"
cp="${cp}:$lib/junit-4.8.1.jar"
cp="${cp}:$lib/levigo-jbig2-imageio-1.3.jar"
cp="${cp}:$lib/log4j-1.2.16.jar"
cp="${cp}:$lib/pdfbox-1.7.1.jar"
cp="${cp}:$lib/svg-0.1-SNAPSHOT.jar"
cp="${cp}:$lib/tagsoup-1.2.jar"
cp="${cp}:$lib/xalan-2.7.0.jar"
cp="${cp}:$lib/xercesImpl-2.8.0.jar"
cp="${cp}:$lib/xml-apis-1.3.03.jar"
cp="${cp}:$lib/xom-1.2.5.jar"

exec java -cp "$cp" $JAVA_OPTS org.xmlcml.pdf2svg.PDF2SVGConverter "$@"
