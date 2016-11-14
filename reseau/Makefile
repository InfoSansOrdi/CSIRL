TEXFILE=reseau

all: $(TEXFILE).tex
	latexmk -f -pdf $(TEXFILE)

clean:
	-rm -Rf *.log *.aux *~ *.toc *.bbl *.blg *.ps *.brf *.nav *.out *.snm \
	*.vrb *.pag auto *.fls *.fdb_latexmk *.glo *.idx *.ilg *.ind *.lof

clean.all: clean
	rm -Rf $(TEXFILE).pdf

