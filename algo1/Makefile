all: algo1-petitlivre.pdf algo1-supports.pdf algo1-livret.pdf algo1-petitlivre.png

# Fabrique le livret de l'animateur
###################################

# (bon, en fait, LaTeX.mk fait ça pour nous très bien)

# Fabrique le petit livre du participant
########################################

algo1-petitlivre.pdf: algo1-petitlivre-page1.pdf algo1-petitlivre-page2.pdf
	pdfjoin algo1-petitlivre-page1.pdf algo1-petitlivre-page2.pdf --outfile algo1-petitlivre.pdf

algo1-petitlivre-page1.pdf: algo1-petitlivre-content.pdf
	./petit-livre.sh algo1-petitlivre-content.pdf algo1-petitlivre-page1.pdf

algo1-petitlivre-content.pdf: algo1-petitlivre-content.tex
	latexmk --synctex=1 -pdf algo1-petitlivre-content.tex

algo1-petitlivre-page2.pdf: algo1-petitlivre-page2.tex
	latexmk --synctex=1 -pdf algo1-petitlivre-page2.tex
	
algo1-petitlivre.png: algo1-petitlivre-page1.pdf
	inkscape --export-png=algo1-petitlivre.png --export-background='#ffffff' --export-height=300 --export-dpi=360 algo1-petitlivre-page1.pdf

# Fabrique les supports
#######################

SUPPORT=algo1-supports
#SUPPORT=algo1-supports-vierge

$(SUPPORT).pdf: $(SUPPORT).svg
	inkscape --export-png=$(SUPPORT).png --export-dpi=360 $(SUPPORT).svg
	convert $(SUPPORT).png $(SUPPORT)-A5.pdf
	#évite le raster (--export-dpi & convert) mais génère un pdf beaucoup plus gros
	#inkscape --without-gui --file=$(SUPPORT).svg --export-pdf=$(SUPPORT).pdf 
	pdf180 $(SUPPORT)-A5.pdf
	pdfjam --outfile $(SUPPORT).pdf --no-landscape --papersize '{210mm,297mm}' --nup 1x2 $(SUPPORT)-A5.pdf $(SUPPORT)-A5-rotated180.pdf
	rm $(SUPPORT)-A5.pdf $(SUPPORT)-A5-rotated180.pdf

clean:
	rm -Rf *.log *.aux *~ *.toc *.brf *.bbl *.blg *.ps *.fdb_latexmk \
	*.nav *.out *.snm *.vrb *.pag auto *.maf *.mtc *.mtc0 *.fls *.synctex.gz

clean.all: clean-all

clean-all: clean
	rm -f algo1-petitlivre-page1.pdf algo1-petitlivre-page2.pdf algo1-petitlivre-content.pdf
