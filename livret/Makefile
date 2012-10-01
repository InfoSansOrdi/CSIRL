#include LaTeX.mk
#CSIRL.pdf:latexmk -pdf CSIRL.tex

default: CSIRL-couv.pdf

CSIRL.pdf: 
	/usr/bin/pdflatex CSIRL.tex

CSIRL-couv.pdf: CSIRL.pdf
	/usr/bin/pdfjam -o CSIRL-couv.pdf img/couverture.pdf 1 CSIRL.pdf 2-


#2up.pdf: CSIRL.pdf
#	/usr/bin/pdfjam --suffix nup --nup '2x1' --landscape --paper a4paper --landscape --delta '1mm 1mm' --frame 'true' --scale '.97' --outfile 2up.pdf -- CSIRL.pdf

#booklet.pdf: CSIRL.pdf
#	pdftops	-paper match CSIRL.pdf - | psbook \
	    | psnup -p"a4" -W"20cm" -H"20cm" -f -c -m"3mm" -b"1mm" -d -2 \
	    | ps2pdf -sPAPERSIZE=a4 - booklet.pdf
