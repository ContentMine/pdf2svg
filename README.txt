=PDF2SVG=

==License==
Apache2, see LICENSE.txt

==Introduction== 
PDF2SVG is the first of (probably) three modules in the AMI2 system. Its role is to convert PDF to SVG
with as little loss as possible. The aims include:
  * removing the complexities of PDF from the average hacker
  * normalizing components (especially Fonts) where possible
  * providing a simpler representation to work with
PDF2SVG is particularly aimed at those who wish to process the documents into other representations 
(such as XHTML and other XML). PDF2SVG aims to:
  * normalize encoding to UTF-8
  * convert all code points to UNICODE
  * denormalize font Dictionary information
  * denormalize scaling and transformations
  
In the best case the SVG produced will be almost indistinguishable on the screen from the PDF. Moreover
the consumer should need to know nothing about PDF. The SVG can then be processed by the SVG2rawXML module
in AMI.

==Architecture== 
PDF2SVG is written in Java >= 1.5 and built/distributed under Maven. It relies on the following libraries:
 * PDFBox: (currently 1.7.1 though 1.6 should work)
 * Euclid and CMLXOM: (libraries for geometry/numeric, CML and general XML). The CML is a bit overkill and we 
   may refactor it later to separate out the non-chemical routines
 * SVG: a XOM (XML DOM) for SVG (limited set of (static) primitives and attributes, but enough to extract 
   most static semantics)
 * XOM: an XML DOM
 
To build:
 * hg clone https://bitbucket.org/petermr/pdf2svg (needs Mercurial)
 * cd pdf2svg
 * mvn clean install (on commandline)
This should create classes and a standalone jar

==Running==
Current usage is:
java pdf2svg [options] PDFFile
[options] include:
  -password password // for encrypted PDF - we can't help if this fails
  -nonSeq //"runs the non-sequential parser" in PDFBox
Expect other options to be added for page control, etc.

==Output==
An SVG document for each page in the input (page1.svg, page2.svg...). Each page has a completely flat structure
with a parent <svg:svg> and child <svg:text>, <svg:path> and <svg:image> elements. Each character is a separate 
<svg:text> object (there is no concept of words, lines, paragraphs, at this stage - that comes in SVG2RAWXML).
Images are either omitted for sace or rendered as bitmaps. Each graphics path is a separate element - concepts of
<svg:circle> etc come in SVG2RAWXML. Graphical attributes (stroke, etc.) are replicated on each element - there
is no default and no grouping. The order of elements (characters, paths, etc) is not preserved as all ordering
is done in SVG2RAWXML.

==Limitations==
We have extracted the commonest graphical elements and attributes. If you find something missing, try looking
at methods of getGraphicsState() in PDFPage2SVGConverter.
We have not (yet) analyzed the font dictionaries in PDFBox. Most characters are represented by Unicode points
and a PDFont. However some Fonts do not offer code points, but offsets into maps. In some cases we suspect that
character information can be recovered, but in others it may only be glyphs. In the latter case we shall need
heuristics to determine the best match for the glyph/character.
