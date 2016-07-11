all: algo2-supports.pdf

include LaTeX.mk

# Fabrique le petit livre du participant
########################################

#algo2-petitlivre.pdf: algo2-petitlivre-page1.pdf algo2-petitlivre-page2.pdf
#	pdfjoin algo2-petitlivre-page1.pdf algo2-petitlivre-page2.pdf --outfile algo2-petitlivre.pdf
#
#algo2-petitlivre-page1.pdf: algo2-petitlivre-content.pdf
#	./petit-livre.sh algo2-petitlivre-content.pdf algo2-petitlivre-page1.pdf

# Fabrique les supports
#######################

algo2-supports.pdf: algo2-supports.svg
	inkscape --export-png=algo2-supports.png --export-dpi=360 algo2-supports.svg 
	convert algo2-supports.png algo2-supports-A5.pdf
	pdf180 algo2-supports-A5.pdf
	pdfjam --outfile algo2-supports.pdf --no-landscape --papersize {210mm,297mm} --nup 1x2 algo2-supports-A5.pdf algo2-supports-A5-rotated180.pdf
	rm algo2-supports-A5.pdf algo2-supports-A5-rotated180.pdf algo2-supports.png

clean-all:
	rm -f algo2-petitlivre-page1.pdf algo2-petitlivre-page2.pdf algo2-petitlivre-content.pdf
