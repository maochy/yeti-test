# Fixes indexing problem with natbib's automatically generated author list
# removes double "{" and extra "}" so that indexes are under the first letter
# of the first author's name not grouped under symbol.
#
# Thomas Lampert 2009


# ** insert main tex project file name **
INDEX_PREFIX="ThesisMain"


AUTHOR_INDEX=$INDEX_PREFIX.adx
SUBJECT_INDEX=$INDEX_PREFIX.idx
NOM_INDEX=$INDEX_PREFIX.nlo

#FOR /F "tokens=*" %%I IN (%_AUTHOR_INDEX%) DO (
#	SET STR=%%I
#	SET STR=!STR:{{={!
#	SET STR=!STR:}\=\!
	
#	ECHO !STR!


#	ECHO !STR! >> %_AUTHOR_INDEX%.tmp
#)
#MOVE %_AUTHOR_INDEX%.tmp %_AUTHOR_INDEX%


makeindex -o $INDEX_PREFIX.ind $SUBJECT_INDEX
makeindex -o $INDEX_PREFIX.and $AUTHOR_INDEX
makeindex -o $INDEX_PREFIX.nls $NOM_INDEX -s nomencl.ist


# This bellow is how to make the list of nomenclature:
# Abbreviation list

# You can make a list of abbreviations with the package nomencl [1].
# To enable the Nomenclature feature of LaTeX, the nomencl package must be loaded in the preamble with:
#\usepackage[⟨options ⟩]{nomencl}
#\makenomenclature
#Issue the \nomenclature[⟨preﬁx ⟩]{⟨symbol ⟩}{⟨description ⟩} command for each symbol you want to have included in the nomenclature list. The best place for this command is immediately after you introduce the symbol for the ﬁrst time. Put \printnomenclature at the place you want to have your nomenclature list.

#Run LaTeX 2 times then

#makeindex filename.nlo  -s nomencl.ist -o filename.nls

#followed by running LaTeX once again.


