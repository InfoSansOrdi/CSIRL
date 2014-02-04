all: petit-livre-algo.pdf

include LaTeX.mk

petit-livre-algo.pdf: algo-livret.pdf page2.pdf
	pdfjoin algo-livret.pdf page2.pdf --outfile petit-livre-algo.pdf

algo-livret.pdf: algo.pdf
	./petit-livre.sh algo.pdf