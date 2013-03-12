@ECHO OFF
SETLOCAL enabledelayedexpansion

REM Fixes indexing problem with natbib's automatically generated author list
REM removes double "{" and extra "}" so that indexes are under the first letter
REM of the first author's name not grouped under symbol.
REM
REM Thomas Lampert 2009



REM ** insert main tex project file name **
SET _INDEX_PREFIX=ThesisMain
REM ** insert mixtex path**
SET _MIKTEX_PATH=C:\Program Files\MiKTeX 2.7\



SET _AUTHOR_INDEX=%_INDEX_PREFIX%.adx
SET _SUBJECT_INDEX=%_INDEX_PREFIX%.idx

FOR /F "tokens=*" %%I IN (%_AUTHOR_INDEX%) DO (
    SET STR=%%I
    SET STR=!STR:{{={!
    SET STR=!STR:}\=\!

    ECHO !STR!


    ECHO !STR! >> %_AUTHOR_INDEX%.tmp
)
MOVE %_AUTHOR_INDEX%.tmp %_AUTHOR_INDEX%




"%_MIKTEX_PATH%miktex\bin\makeindex.exe" -s index.ist -o %_INDEX_PREFIX%.ind %_SUBJECT_INDEX%
"%_MIKTEX_PATH%miktex\bin\makeindex.exe" -s index.ist -o %_INDEX_PREFIX%.and %_AUTHOR_INDEX%
"%_MIKTEX_PATH%miktex\bin\makeindex.exe" %_INDEX_PREFIX%.nlo -s nomencl.ist -o %_INDEX_PREFIX%.nls
