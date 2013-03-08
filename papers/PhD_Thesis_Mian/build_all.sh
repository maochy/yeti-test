pdflatex ThesisMain.tex
pdflatex ThesisMain.tex

bibtex ThesisMain

pdflatex ThesisMain.tex
pdflatex ThesisMain.tex

sh index.sh

pdflatex ThesisMain.tex
pdflatex ThesisMain.tex > /tmp/pdflatex.log

xpdf ThesisMain.pdf &

