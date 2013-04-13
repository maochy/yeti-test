% This file defines a thesis class for
% the University of York. The class 
% complied with the York Regulation 2.7 in 2002.
%
% based on the UWA thesis class by R Huss modified by J.Pickering and subsequently by K.Clegg
% 

% ----------- Document Class:
\ProvidesClass{yThesis2}
%\LoadClass[10pt,titlepage,openany,oneside]{report}
\LoadClass[12pt,titlepage,openany,twoside]{report}

% ----------- Required packages:
\RequirePackage{ifthen}
\RequirePackage{setspace}
\RequirePackage{setspace}
\RequirePackage{fancyhdr}
\RequirePackage{vmargin,graphicx}

% ----------- Margins:
% Required to be 40mm from the left at least, and at least 15mm elsewhere.
% Standard is 25mm all round.
% \setmarginsrb{40mm}{15mm}{20mm}{15mm}{20pt}{11mm}{20pt}{11mm} % default
% \setmarginsrb{40mm}{15mm}{25mm}{15mm}{20pt}{11mm}{20pt}{11mm} % original
% \setmarginsrb{50mm}{45mm}{40mm}{45mm}{20pt}{11mm}{20pt}{11mm} % and the font to 12pt - Pierre
\setmarginsrb{40mm}{30mm}{35mm}{30mm}{20pt}{11mm}{20pt}{11mm}

% ----------- Set parameters for floats:
\setcounter{totalnumber}{10}               % Pretty unlikely
\renewcommand{\topfraction}{0.85}          % Floats may occupy up to 85%
\renewcommand{\bottomfraction}{0.85}       %  of the page. This is slightly
\renewcommand{\textfraction}{0.15}         %  different to the old style
\renewcommand{\floatpagefraction}{0.60}    % Can allow quite a lot of whitespace on a float page

% ----------- Headings:
\pagestyle{fancy}
% \renewcommand{\chaptermark}[1]{\markright{\chaptername\ \thechapter.\ #1}} % this was in one sided version
\renewcommand{\chaptermark}[1]{\markboth{#1\hspace{7mm}\chaptername\ \thechapter}{}}
\renewcommand{\sectionmark}[1]{\markright{Section\ \thesection\hspace{7mm}#1}{}}
\lhead[\fancyplain{}{\bfseries\thepage}]%
      {\fancyplain{}{\bfseries\rightmark}}
\chead[\fancyplain{}{}]%
      {\fancyplain{}{}}
\rhead[\fancyplain{}{\bfseries\leftmark}]%
      {\fancyplain{}{\bfseries\thepage}}
\lfoot[\fancyplain{}{}]%
      {\fancyplain{}{}}
\cfoot[\fancyplain{}{}]%
      {\fancyplain{}{}}
\rfoot[\fancyplain{}{}]%
      {\fancyplain{}{}}
       
% ----------- Headings: even with fancy header the first page
% has the plain style. This code bellow makes the page number
% bold in the first page of each chapter.
\fancypagestyle{plain}{
\fancyhf{}  % clear all header and footer fields
\fancyfoot[C]{\bfseries \thepage}   % except the center
\renewcommand{\headrulewidth}{0pt}
\renewcommand{\footrulewidth}{0pt}}

% ----------- Comply with York regs 2.7.3.j & 2.7.3.l:
\setcounter{secnumdepth}{4}
\pagenumbering{arabic}

% ----------- Macros to define department, supervisor and submission date:
\def\dept#1{\gdef\@dept{#1}}
\def\supervisor#1{\gdef\@supervisor{#1}}
\def\examiners#1{\gdef\@examiners{#1}}
\def\submitdate#1{\gdef\@submitdate{#1}}
\def\subtitle#1{\gdef\@subtitle{#1}}
\def\authoremail#1{\gdef\@authoremail{#1}}
\def\examiners#1{\gdef\@examiners{#1}}

  \def\@title{}
  \def\@subtitle{}
  \def\@author{}
  \def\@dept{}
  \def\@supervisor{}
  \def\@examiners{}
  \def\@submitdate{\ifcase\the\month\or
    January\or February\or March\or April\or May\or June\or
    July\or August\or September\or October\or November\or December\fi
    \space \number\the\year}


\DeclareFixedFont{\titlefont}{T1}{cmr}{bx}{sc}{28pt}
\DeclareFixedFont{\subtitlefont}{T1}{cmr}{bx}{sc}{20pt}


% Generate title page
\newcommand{\titlePage}{
        \begin{singlespace}
\thispagestyle{empty}
 \vspace*{2cm}
  \begin{center}
     
     {\sc{\normalfont\Huge\sffamily{\textbf{\@title}}}}
     \\[0.5cm]
     %{\sc{\normalfont\huge\sffamily{\textbf{\@subtitle}}}}
  \end{center}
  \vspace*{1cm}
  \begin{center}
    {\large{\sc{\@author}}}
    \\
%    {\footnotesize{\url{\@authoremail}}}
  \end{center}
  \vspace{5.0cm}
  \begin{center}
      {\sc{\scriptsize{A thesis submitted for the degree of\\Doctor of Philosophy}}}
  \end{center}
  \vfill
  \begin{center}
    %\hfill
    %{\parbox{8cm}{\footnotesize{\textbf{Ph.D. Thesis}}}}
    %{\sc{\scriptsize{A thesis submitted for the degree of\\Doctor of Philosophy}}}
    %\\[1.0cm]
    {\sc{Department of Computer Science}}
    \\[0.2cm]
    \includegraphics [width=8cm]{Woodcuts/theunivofyork}
    {\sc{\large{}\\
     \footnotesize{United Kingdom}
      }}
    \\[0.8cm]
    {\sc{\footnotesize{\@submitdate}}}
%    {\sc{\footnotesize{Supervisor: \@supervisor}}}
%    {\sc{\footnotesize{Examiners: \@examiners}}}
  \end{center}
        \end{singlespace}
}

% ----------- Dedication:
\newcommand{\dedication}[1]{
    \newpage
    \thispagestyle{empty}
    \begin{center}
        \vspace*{4cm}
        \textit{\small {#1}}
    \end{center}        
}

% ----------- Abstract:
\newcommand{\yabstract}[1]{
    \chapter*{Abstract}
    \lhead[\fancyplain{}{\bfseries\thepage}]%
        {\fancyplain{}{\bfseries{Abstract}}}
    \rhead[\fancyplain{}{\bfseries{Abstract}}]%
        {\fancyplain{}{\bfseries\thepage}}
    #1
    \newpage
% -- This has to be copied from: \pagestyle{fancy} -- FROM HERE --
\lhead[\fancyplain{}{\bfseries\thepage}]%
      {\fancyplain{}{\bfseries\rightmark}}
\chead[\fancyplain{}{}]%
      {\fancyplain{}{}}
\rhead[\fancyplain{}{\bfseries\leftmark}]%
      {\fancyplain{}{\bfseries\thepage}}
\lfoot[\fancyplain{}{}]%
      {\fancyplain{}{}}
\cfoot[\fancyplain{}{}]%
      {\fancyplain{}{}}
\rfoot[\fancyplain{}{}]%
      {\fancyplain{}{}}
% -- This has to be copied from: \pagestyle{fancy} -- TO HERE --
}

% ----------- Contents:
\newcommand{\contents}{
    \begin{spacing}{1.0}
        \tableofcontents
        \listoftables
        \addcontentsline{toc}{section}{List of Tables}
        \listoffigures
        \addcontentsline{toc}{section}{List of Figures}
    \end{spacing}
}

% ----------- Acknowledgement:
\newcommand{\acknowledgement}[1]{
    \chapter*{Acknowledgements}
    \lhead[\fancyplain{}{\bfseries\thepage}]%
        {\fancyplain{}{\bfseries{Acknowledgements}}}
    \addcontentsline{toc}{section}{Acknowledgements}
    #1
}

% ----------- Generate an Author's Declaration page:
\newcommand{\declaration}[1]{
    \chapter*{Declaration}
    \lhead[\fancyplain{}{\bfseries\thepage}]%
        {\fancyplain{}{\bfseries{Declaration}}}
    \rhead[\fancyplain{}{\bfseries{Declaration}}]%
        {\fancyplain{}{\bfseries\thepage}}
    \addcontentsline{toc}{section}{Declaration}
    
  \newlength{\candwidth}
  \settowidth{\candwidth}{(candidate)}
%  \begin{flushleft}
%  {\Huge {\bf Declaration}}
%  \end{flushleft}
%  \vspace{0.5in}
  \noindent This thesis has not previously been accepted in substance
  for any degree and is not being concurrently submitted in candidature
  for any degree other than Doctor of Philosophy of the University of York.
  This thesis is the result of my own investigations, except
  where otherwise stated. Other sources are acknowledged by explicit references.

  I hereby give consent for my thesis, if accepted, to be made
  available for photocopying and for inter-library loan, and for the
  title and summary to be made available to outside organisations.
  \vspace{0.5in}

  \begin{minipage}{5.0in}
    \noindent Signed \dotfill (candidate)\\[1.5em]
    \noindent Date \dotfill \hspace{\candwidth}
  \end{minipage}
  \vspace{0.5in}  
  \\
  \\
  \\
  \\
  \\
  \textbf{Publications:}\\
  \noindent Some of the material contained in this thesis has appeared
  in the following published conference and workshop papers:

 }

% ----------- York Chapter:
% This class should be used for insterting chapters after the use
% of classes for Acknowledgment or Declaration which modify the header.
% The first param is the short title for the header
% The second param is the actual title of the chapter
% The third param is the text of the chapter
\newcommand{\ychapter}[3]{
%    \begin{spacing}{2.0}
        \chapter[#1]{#2}
        \lhead[\fancyplain{}{\bfseries\thepage}]%
        {\fancyplain{}{\bfseries\rightmark}}
        \rhead[\fancyplain{}{\bfseries\leftmark}]%
        {\fancyplain{}{\bfseries\thepage}}
    #3
%    \end{spacing}
}

\newcommand{\yappendix}[3]{
    \renewcommand{\chaptername}{\bfseries Appendix}
    \chapter[#1]{#2}
    \lhead[\fancyplain{}{\bfseries\thepage}]%
        {\fancyplain{}{\bfseries\rightmark}}
    \rhead[\fancyplain{}{\bfseries\leftmark}]%
        {\fancyplain{}{\bfseries\thepage}}
    #3
}

% ----------- Index and Cite Index:
\newcommand{\yorkNomIndex}{
        \newpage
        \renewcommand{\nomname}{List of Symbols}
        \markright{LIST OF SYMBOLS}
        \lhead[\fancyplain{}{\bfseries\thepage}]%
            {\fancyplain{}{\bfseries\rightmark}}
        \rhead[\fancyplain{}{\bfseries LIST OF SYMBOLS}]%
            {\fancyplain{}{\bfseries\thepage}}
        % Do not need to add to toc here if you are suing [intoc] parameter with the nomencl package.
        % \addcontentsline{toc}{chapter}{List of Symbols}
        \printnomenclature
}

\newcommand{\yorkIndex}{
        \newpage
        \markright{Index}
        \addcontentsline{toc}{chapter}{Index}
        \printindex
}

\newcommand{\yorkCiteIndex}{
        \newpage
        \markright{Citation Index}
        \addcontentsline{toc}{chapter}{Citation Index}
        \printindex[aut]
}

% ----------- The Last Page of the Document:
\newcommand{\ylastpage}[1]{
    \newpage
    \renewcommand{\headrulewidth}{0pt}
\lhead[\fancyplain{}{}]%
      {\fancyplain{}{}}
\chead[\fancyplain{}{}]%
      {\fancyplain{}{}}
\rhead[\fancyplain{}{}]%
      {\fancyplain{}{}}
\lfoot[\fancyplain{}{}]%
      {\fancyplain{}{}}
\cfoot[\fancyplain{}{\bfseries\thepage}]%
      {\fancyplain{}{\bfseries\thepage}}
\rfoot[\fancyplain{}{}]%
      {\fancyplain{}{}} 
    #1
}
