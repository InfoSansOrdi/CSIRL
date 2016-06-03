all: algo1-petitlivre.pdf algo1-supports.pdf algo1-livret.pdf

include LaTeX.mk

# Fabrique le livret de l'animateur
###################################

# (bon, en fait, LaTeX.mk fait ça pour nous très bien)

# Fabrique le petit livre du participant
########################################

algo1-petitlivre.pdf: algo1-petitlivre-page1.pdf algo1-petitlivre-page2.pdf
	pdfjoin algo1-petitlivre-page1.pdf algo1-petitlivre-page2.pdf --outfile algo1-petitlivre.pdf

algo1-petitlivre-page1.pdf: algo1-petitlivre-content.pdf
	./petit-livre.sh algo1-petitlivre-content.pdf algo1-petitlivre-page1.pdf

# Fabrique les supports
#######################

algo1-supports.pdf: algo1-supports.svg
	inkscape --export-png=algo1-supports.png --export-dpi=360 algo1-supports.svg 
	convert algo1-supports.png algo1-supports-A5.pdf
	pdf180 algo1-supports-A5.pdf
	pdfjam --outfile algo1-supports.pdf --no-landscape --papersize {210mm,297mm} --nup 1x2 algo1-supports-A5.pdf algo1-supports-A5-rotated180.pdf
	rm algo1-supports-A5.pdf algo1-supports-A5-rotated180.pdf algo1-supports.png

clean-all:
	rm -f algo1-petitlivre-page1.pdf algo1-petitlivre-page2.pdf algo1-petitlivre-content.pdf
