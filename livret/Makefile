#include LaTeX.mk
#SMN.pdf:latexmk -pdf SMN.tex

default: SMN.pdf

couverture: SMN.pdf
	/usr/bin/pdfjam -o SMN-couv.pdf img/couverture.pdf 1 SMN.pdf 2-

standalone:
	/usr/bin/pdflatex baseball.tex
	/usr/bin/pdflatex crepier.tex
	/usr/bin/pdflatex nim.tex
	/usr/bin/pdflatex tsp.tex

SMN.pdf: 
	/usr/bin/pdflatex SMN.tex

clean:
	rm -Rf *.log *~ *.toc *.bbl *.blg *.ps *.brf \
	*.nav *.out *.snm *.vrb *.pag auto *.fdb_latexmk

clean.all: clean
	rm -Rf *pdf

#2up.pdf: SMN.pdf
#	/usr/bin/pdfjam --suffix nup --nup '2x1' --landscape --paper a4paper --landscape --delta '1mm 1mm' --frame 'true' --scale '.97' --outfile 2up.pdf -- SMN.pdf

#booklet.pdf: SMN.pdf
#	pdftops	-paper match SMN.pdf - | psbook \
	    | psnup -p"a4" -W"20cm" -H"20cm" -f -c -m"3mm" -b"1mm" -d -2 \
	    | ps2pdf -sPAPERSIZE=a4 - booklet.pdf
