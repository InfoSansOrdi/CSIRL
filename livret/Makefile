include LaTeX.mk
#SMN.pdf:latexmk -pdf SMN.tex

default: SMN.pdf baseball.pdf crepier.pdf nim.pdf tsp.pdf

standalone:
	/usr/bin/pdflatex baseball.tex
	/usr/bin/pdflatex crepier.tex
	/usr/bin/pdflatex nim.tex
	/usr/bin/pdflatex tsp.tex

clean::
	rm -f *.log *~ *.toc *.bbl *.blg *.ps *.brf \
	*.nav *.out *.snm *.vrb *.pag auto *.fdb_latexmk

clean.all: clean
	rm -f *.pdf

#2up.pdf: SMN.pdf
#	/usr/bin/pdfjam --suffix nup --nup '2x1' --landscape --paper a4paper --landscape --delta '1mm 1mm' --frame 'true' --scale '.97' --outfile 2up.pdf -- SMN.pdf

booklet.pdf: SMN.pdf
	pdftops	-paper match SMN.pdf - | psbook \
	    | psnup -p"a4" -P"a5" -c -d -2 \
	    | ps2pdf -sPAPERSIZE=a4 - booklet.pdf
