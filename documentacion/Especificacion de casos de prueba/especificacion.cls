\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{especificacion}
[2005/01/19 v1 Estilo Memoria de Titulo - ICC - UTalca (fmeza, jperez, gsanhueza)] 

\PassOptionsToClass{12pt,oneside,fleqn,spanish}{report}
\DeclareOption*{\PassOptionsToClass{\CurrentOption}{report}}
\ProcessOptions
\LoadClass{report}

\RequirePackage{xparse}
\RequirePackage{expl3}
\RequirePackage[latin1]{inputenc}
\RequirePackage[english,spanish]{babel}
\RequirePackage{graphicx}
\RequirePackage{setspace}

\selectlanguage{spanish}
\bibliographystyle{plain}

\def\utalca{Universidad de Talca}
\def\facdeing{Facultad de Ingenier\'{\i}a}
\def\escdeicc{Escuela de Ingenier\'{\i}a Civil en Computaci\'{o}n}

\def\tbldevelopment{Equipo de desarrollo:}
\def\tblcounterpart{Contraparte:}
\def\tblhistorychanges{Historial de cambios}
\def\tblcontname{Tabla de Contenidos}
\def\indfigname{Índice de Figuras}
\def\indtblname{Índice de Tablas}
\def\glosname{Glosario}
\def\appendixpartname{Anexos}

\def\titulo#1{\def\@titulo{#1}}
\def\subtitulo#1{\def\@subtitulo{#1}}

%%%
% Redimensión de márgenes
%%%

\setlength\paperheight{11in}  %
\setlength\paperwidth{8.5in}  %
\special{papersize=8.5in,11in}

\newdimen\realleftmargin
\newdimen\realrightmargin
\newdimen\realtopmargin
\newdimen\realbottommargin
\newdimen\realheadpos

\realtopmargin    = 4cm
\realleftmargin   = 4cm
\realbottommargin = 2.5cm
\realrightmargin  = 2.5cm
\realheadpos      = 1.5cm

\setlength\oddsidemargin   {\realleftmargin}
\addtolength\oddsidemargin {-1in}

\setlength\textheight{\paperheight}
  \addtolength\textheight {-\realtopmargin}
  \addtolength\textheight {-\realbottommargin}

\setlength\textwidth{\paperwidth}
 \addtolength\textwidth {-\realleftmargin}
 \addtolength\textwidth {-\realrightmargin}

\setlength\topmargin{\realheadpos}
 \addtolength\topmargin {-1in}

\setlength\headsep{\realtopmargin}
 \addtolength\headsep{-\realheadpos}
 \addtolength\headsep{-\headheight}

%%%
% Definiciones para la tapa
%%%

\def\makeencabezado
{
\begin{center}
\onehalfspacing
{\large\bf\MakeUppercase{\utalca}} \\
{\large\MakeUppercase{\facdeing}} \\
{\large\MakeUppercase{\escdeicc}}
\end{center}
\singlespacing
}

\def\logo
{
\begin{center}
\resizebox{2.3cm}{2.8cm}{\includegraphics{utalca1.eps}}
\end{center}
}


%%%
% PORTADA
%%%

\def\maketitle
{

\singlespacing

% TAPA 

\pagestyle{empty}
\setcounter{page}{-1}
\vspace*{-25mm}

% Logo UTalca
\logo

% Encabezado
\makeencabezado

% Título del documento
\bigskip
\begin{center}
  %\onehalfspacing
  {\Large \bf \@titulo\\}  
  %\singlespacing
\end{center}

% Subtitulo
\bigskip
\begin{center}
  {\bf \MakeUppercase{\@subtitulo}}
\end{center}

\vfill

\newpage
\pagestyle{headings} %% de ahora en adelante hay headings en las páginas
\onehalfspacing %% de ahora en adelante la separacion es de línea y 1/2
}

%%%
% Revision history
%%%

\ExplSyntaxOn
\tl_new:N \l_development_tl% to hold development team
\newcommand\adddevelopment[3]{% add to \l_development_tl
  % #1 name, #2 Rol, #3 contact
  \tl_put_right:Nn \l_development_tl {#1}% add #1 parameter for end of line
  \tl_put_right:Nn \l_development_tl {&#2}% add #2 parameter for end of line
  \tl_put_right:Nn \l_development_tl {&#3}% add #3 parameter for end of line
  \tl_put_right:Nn \l_development_tl {\\\hline}% add \\ for end of line
}
\tl_new:N \l_counterpart_tl% to hold counterpart team
\newcommand\addcounterpart[3]{% add to \l_counterpart_tl
  % #1 name, #2 Rol, #3 contact
  \tl_put_right:Nn \l_counterpart_tl {#1}% add #1 parameter for end of line
  \tl_put_right:Nn \l_counterpart_tl {&#2}% add #2 parameter for end of line
  \tl_put_right:Nn \l_counterpart_tl {&#3}% add #3 parameter for end of line
  \tl_put_right:Nn \l_counterpart_tl {\\\hline}% add \\ for end of line
}

\tl_new:N \l_revisions_tl% to hold revision history
\newcommand\addrevision[3]{% add to \l_revisions_tl
  % #1 version, #2 Fecha, #3 modificaciones
  \tl_put_right:Nn \l_revisions_tl {#1}% add #1 parameter for end of line
  \tl_put_right:Nn \l_revisions_tl {&#2}% add #2 parameter for end of line
  \tl_put_right:Nn \l_revisions_tl {&#3}% add #3 parameter for end of line
  \tl_put_right:Nn \l_revisions_tl {\\\hline}% add \\ for end of line
}

\newcommand\RevisionHistoryPage{%
  \tl_if_empty:NF \l_development_tl {
    \textbf{\MakeUppercase{\tbldevelopment}}\par
    \bigskip
    \begin{tabular}{|p{0.27\textwidth}|p{0.3\textwidth}|p{0.32\textwidth}|}
      \hline
      \textbf{Nombre} & \textbf{Rol} & \textbf{Contacto}\\
      \hline
      \tl_use:N \l_development_tl
    \end{tabular}
    \bigskip\bigskip\bigskip\par
  }

  \tl_if_empty:NF \l_counterpart_tl {
    \textbf{\bf\MakeUppercase{\tblcounterpart}}\par
    \bigskip
    \begin{tabular}{|p{0.27\textwidth}|p{0.3\textwidth}|p{0.32\textwidth}|}
      \hline
      \textbf{Nombre} & \textbf{Rol} & \textbf{Contacto}\\
      \hline
      \tl_use:N \l_counterpart_tl
    \end{tabular}
    \bigskip\bigskip\bigskip\par
  }

  %% if there is add revision commands add table
  \tl_if_empty:NF \l_revisions_tl {
    %\addcontentsline{toc}{chapter}{\tblhistorychanges}	%% agrega la entrada al toc
    %\def\thepage{\csname @roman\endcsname \c@page} %% define el estilo de numeración 
    \thispagestyle{plain} %% estilo de la página
    \centerline{\bf\MakeUppercase{\tblhistorychanges}} %% escribe \tblhistorychanges
    \bigskip
    \begin{tabular}{|c|c|p{0.65\textwidth}|}
      \hline
      \textbf{Version} & \textbf{Fecha} & \textbf{Modificaciones}\\
      \hline
      \tl_use:N \l_revisions_tl
      \end{tabular}
  }
  
  \bool_if:nT {! (\tl_if_empty_p:N \l_revisions_tl 
          && \tl_if_empty_p:N \l_counterpart_tl 
          &&  \tl_if_empty_p:N \l_development_tl )}
          {\newpage}
}
\ExplSyntaxOff

%%%
% Table of Contents
%%%

\renewcommand\tableofcontents{
  \addcontentsline{toc}{chapter}{\tblcontname}	%% agrega la entrada al toc
  \onehalfspacing	%% setea el espacio para el toc
  \def\thepage{\csname @roman\endcsname \c@page} %% define el estilo de numeración 
  \thispagestyle{plain} %% estilo de la página
  \centerline{\bf\MakeUppercase{\tblcontname}} %% escribe \tblcontname
  \begin{flushright} \pagename \end{flushright} %% escribe \pagename
  \par 
  \@starttoc{toc} %% abre el archivo y escribe el toc en el pdf
  
  \newpage %% nueva página
  \def\thepage{\csname @arabic\endcsname \c@page} %% por si no hay mas inicio
}

%%%
% List of Figures
%%%

\renewcommand\listoffigures{
  \addcontentsline{toc}{chapter}{\indfigname}
  \onehalfspacing
  \def\thepage{\csname @roman\endcsname \c@page}
  \thispagestyle{plain}
  \centerline{\bf\MakeUppercase{\indfigname}} 
  \begin{flushright} \pagename \end{flushright}
  \par
  \@starttoc{lof}
  
  \newpage
  \def\thepage{\csname @arabic\endcsname \c@page}
}

%%%
% List of Tables
%%%

\renewcommand\listoftables{
  \addcontentsline{toc}{chapter}{\indtblname}
  \onehalfspacing
  \def\thepage{\csname @roman\endcsname \c@page}
  \thispagestyle{plain}
  \centerline{\bf\MakeUppercase{\indtblname}}
  \begin{flushright} \pagename \end{flushright}
  \par
  \@starttoc{lot}
  
  \newpage
  \def\thepage{\csname @arabic\endcsname \c@page}
}

%%%
% Formato encabezado capítulo
%%%

\def\@makechapterhead#1{%
  \thispagestyle{plain}
  \vspace*{25mm}
  \edef\@svsec{\hbox to 1cm{\thechapter.}\hskip 1em }
  \begingroup \Huge\bf\relax
     \@hangfrom{\hskip \z@\relax\@svsec}
     {\interlinepenalty \@M {#1}\par}%
  \endgroup

  \vspace*{7mm}
  \hrule height 2pt width \textwidth

  \bigskip\bigskip\bigskip
}

\def\section{\@startsection {section}{1}{\z@}
  {-3.5ex plus -1ex minus -.2ex}
  {2.3ex plus .2ex}
  {\normalfont\large\bf}}
\def\subsection{\@startsection{subsection}{2}{\z@}
  {-3.25ex plus-1ex minus-.2ex}
  {1.5ex plus.2ex}
  {\normalfont\normalsize\bf}}
\def\subsubsection{\@startsection{subsubsection}{3}{\z@}
  {-3.25ex plus -1ex minus-.2ex}
  {1.5ex plus.2ex}
  {\normalfont\normalsize\bf}}
\def\paragraph{\@startsection
  {paragraph}{4}{\z@}
  {3.25ex plus1ex minus.2ex}
  {-1em}
  {\normalfont\normalsize\bf}}
\def\subparagraph{\@startsection
  {subparagraph}{4}
  {\parindent}
  {3.25ex plus1ex minus .2ex}
  {-1em}
  {\normalfont\normalsize\bf}}

%%%
% Glosario
%%%

\newenvironment{glosario}
{
	\newpage
	\addcontentsline{toc}{chapter}{\glosname}
	\thispagestyle{plain}
	\@mkboth{\MakeUppercase\glosname}{\MakeUppercase\glosname} % marca para los headongs
  \vspace*{26mm}
  {\Huge\bf\relax\noindent {\glosname}\par}
  
  \bigskip\bigskip\bigskip
	\begin{description}
}
{
	\end{description}
}

%%%
% Bibliografia
%%%

\renewenvironment{thebibliography}[1]{
	\section{\refname}
  %%% no tengo idea por qué pero esto genera la bibliografía
  \list{\@biblabel{\@arabic\c@enumiv}}%
  {\settowidth\labelwidth{\@biblabel{#1}}%
   \leftmargin\labelwidth
   \advance\leftmargin\labelsep
   \@openbib@code
   \usecounter{enumiv}%
   \let\p@enumiv\@empty
   \renewcommand\theenumiv{\@arabic\c@enumiv}}%
   \sloppy
   \clubpenalty4000
   \@clubpenalty \clubpenalty
   \widowpenalty4000%
   \sfcode`\.\@m
   {\def\@noitemerr
   {\@latex@warning{Empty `thebibliography' environment}}%
   \endlist}
}

%%%
% Anexos
%%%

\def\appendixpart{
  \cleardoublepage
  \thispagestyle{empty}
  \setcounter{chapter}{0}%
  \def\Thechapter{\Alph{chapter}}
  \def\thechapter{\Alph{chapter}}

  \addtocontents{toc}{\protect\contentsline{chapter}{\appendixpartname}{}}
  \addtocontents{lof}%
  {\protect\addvspace{10\p@}}% Adds between-chapter space
  \addtocontents{lot}%
  {\protect\addvspace{10\p@}}% to lists of figs & tables.

  
  \hbox{}\vfil
  \centerline{\Huge \bf \MakeUppercase\appendixpartname}\par
  \vfil\newpage
  \pagestyle{headings}
  }

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  
\def\appendix{\cleardoublepage
  \setcounter{section}{0}
  \secdef\@appendix\@sappendix}
  
  \def\@appendix[#1]#2{
  \ifnum \c@secnumdepth >-2\relax
  \refstepcounter{chapter}%
  \addcontentsline{toc}{chapter}{\Thechapter:\hspace{1em}#1}
  \else
  \addcontentsline{toc}{chapter}{#1}
  \fi
  \addtocontents{lof}%
  {\protect\addvspace{10\p@}}% Adds between-chapter space
  \addtocontents{lot}%
  {\protect\addvspace{10\p@}}% to lists of figs & tables.

  \thispagestyle{plain}
  \vspace*{25mm}
  \edef\@svsec{\hbox to 1cm{\thechapter.}\hskip 1em }
  \begingroup \Huge\bf\relax
     \@hangfrom{\hskip \z@\relax\@svsec}
     {\interlinepenalty \@M {#2}\par}%
  \endgroup

  \vspace*{7mm}
  \hrule height 2pt width \textwidth

  \bigskip\bigskip\bigskip
  \@mkboth{\MakeUppercase{\Thechapter:\hspace{1em}#1}}{\MakeUppercase{\Thechapter:\hspace{1em}#1}} % marca para los headings
  }

\def\@sappendix#1{{\centering
    \interlinepenalty \@M
    \Huge \bf
    #1\par}\@endappendix}

\endinput
