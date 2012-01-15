LATEXFILTER=/usr/share/texmf/scripts/latex-make/latexfilter.pl |\
  grep -v '^LaTeX Font Warning:'|\
  grep -v '^(Font) '

#include LaTeX.mk

CSIRL.pdf:latexmk -pdf CSIRL.tex

2up.pdf: CSIRL.pdf
	/usr/bin/pdfjam --suffix nup --nup '2x1' --landscape --paper a4paper --landscape --delta '1mm 1mm' --frame 'true' --scale '.97' --outfile 2up.pdf -- CSIRL.pdf
