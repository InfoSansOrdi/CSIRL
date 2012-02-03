all: livret-encodage.pdf

LATEXFILTER=/usr/share/texmf/scripts/latex-make/latexfilter.pl |\
  grep -v '^LaTeX Font Warning:'|\
  grep -v '^(Font) '

include LaTeX.mk

#livret-encodage.pdf:latexmk -pdf livret-encodage.tex

2up.pdf: livret-encodage.pdf
	/usr/bin/pdfjam --suffix nup --nup '2x1' --landscape --paper a4paper --landscape --delta '1mm 1mm' --frame 'true' --scale '.97' --outfile 2up.pdf -- livret-encodage.pdf

booklet.pdf: livret-encodage.pdf
	pdftops	-paper match $< - | psbook \
	    | psnup -p"a4" -W"20cm" -H"20cm" -f -c -m"3mm" -b"1mm" -d -2 \
	    | ps2pdf -sPAPERSIZE=a4 - booklet.pdf
