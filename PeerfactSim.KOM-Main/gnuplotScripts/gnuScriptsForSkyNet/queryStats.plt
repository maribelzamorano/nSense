reset

#------------------------------------------------------------
# Statistics about the queries within SkyNet
#------------------------------------------------------------

#level of query-origination
set xlabel "Tree-level"
set ylabel "Percentage of originated Queries [%]"
set title "Level of Query-origination"

set terminal png giant
set output "../graphics/queries/queryOrigination.png"
set style data linespoints
plot "../data/StartedQueries.dat" using 1:($2*100) notitle lt rgb "black"
set terminal postscript eps "Helvetica" 24
set output "../graphics/queries/queryOrigination.eps"
replot

reset

#level of query-solving
set xlabel "Tree-level"
set ylabel "Percentage of solved Queries [%]"
set title "Level of Query-solving"

set terminal png giant
set output "../graphics/queries/querySolving.png"
set style data linespoints
plot "../data/AbsoluteSolvedQueries.dat" using 1:($2*100) notitle lt rgb "black"
set terminal postscript eps "Helvetica" 24
set output "../graphics/queries/querySolving.eps"
replot

reset

#number of hops
set xlabel "Number of Hops"
set ylabel "Percentage of solved Queries [%]"
set title "Number of Hops of solved Queries"

set terminal png giant
set output "../graphics/queries/queryHops.png"
set style data linespoints
plot "../data/HopsOfSolvedQueries.dat" using 1:($2*100) notitle lt rgb "black"
set terminal postscript eps "Helvetica" 24
set output "../graphics/queries/queryHops.eps"
replot

reset

#quality of answers
set xlabel "Percentage of present Peers in Query-Answer [%]"
set ylabel "Percentage of solved Query-Answers [%]"
set title "Quality of solved Query-Answers"

set terminal png giant
set output "../graphics/queries/qualityqueryAnswer.png"
set style data points
#set xrange[*:1]
set yrange [0:*]
plot "../data/QualityOfQueries.dat" using ($1*100):($2*100) notitle lc rgb "black"
set terminal postscript eps "Helvetica" 24
set output "../graphics/queries/qualityqueryAnswer.eps"
replot

reset

#hits per level of every query
set xlabel "Tree-level"
set ylabel "Number of Hits"
set title "Query-Hits per Level"

set terminal png giant
set output "../graphics/queries/queryHitsPerLevel.png"
set style data linespoints
plot "../data/HitsPerLevel.dat" using 1:2 notitle lt rgb "black"
set terminal postscript eps "Helvetica" 24
set output "../graphics/queries/queryHitsPerLevel.eps"
replot

reset

# percentage of found peers vs requested peers
set xlabel "Percentage of found Peers [%]"
set ylabel "Percentage of unsolved Query-Answers [%]"
set title "Percentage of found Peers in unsolved Query-Answers" 
set terminal png giant
set output "../graphics/queries/percHitsOfUnsolvedQueries.png"
set style data points
set yrange [0:*]
plot "../data/PercHitsOfUnsolvedQueries.dat" using ($1*100):($2*100) notitle lc rgb "black"
set terminal postscript eps "Helvetica" 24
set output "../graphics/queries/percHitsOfUnsolvedQueries.eps"
replot

reset

#------------------------------------------------------------
# Matrix for the levels of Query-origination and -resolution
#------------------------------------------------------------


# Flat figure for the levels of Query-origination and -resolution
set title "Levels of Query-origination and -resolution" 
set terminal png giant
set output "../graphics/queries/queryMatrix.png"
set style data dots
set xlabel "Query-Originator"
set xrange [0:*]
set ylabel "Query-Solver"
set yrange [0:*]
set palette gray negative gamma 0.5
set cbrange [0:*]

set border 4095
set pm3d map
splot "../data/QueryMatrix.dat" using 1:2:3 notitle 
set terminal postscript eps "Helvetica" 24
set output "../graphics/queries/queryMatrix.eps"
replot

reset

# 3D-Figure for the levels of Query-origination and -resolution
set title "Levels of Query-origination and -resolution" 
set xlabel "Query-Originator"
set xrange [0:*] reverse
set ylabel offset 0,-1
set ylabel "Query-Solver"
set yrange [0:*]
set zlabel offset 7,6
set zlabel "Number of\nsolved Queries"

set palette gray negative gamma 0.75
set cbrange [0:*]
set border 4095
set terminal png giant
set output "../graphics/queries/queryMatrix2.png"
set border 4095 front linetype -1 linewidth 1.000
set style line 100  linetype rgb "white" linewidth 0.500 pointtype 100 pointsize default
set view 60, 30, 1, 1
unset surface
set pm3d implicit at s
set pm3d interpolate 1,1 flush begin noftriangles hidden3d 100 corners2color mean
splot "../data/QueryMatrix.dat" using 1:2:3 notitle
set terminal postscript eps "Helvetica" 24
set output "../graphics/queries/queryMatrix2.eps"
replot

reset

#------------------------------------------------------------
# Matrix for queries with varying amount of peers
#------------------------------------------------------------


# Flat figure for the average complexity of solved queries at a level
set title "Average Complexity of solved \nQueries in the SkyNet-tree" 
set terminal png giant
set output "../graphics/queries/queryPeerAverageMatrix.png"
set samples 200; set isosamples 200
set style data dots
set xlabel "Query-Originator"
set xrange [0:*]
set ylabel "Query-Solver"
set yrange [0:*]
set palette gray negative gamma 0.75
set cbrange [0:*]

set border 4095
set pm3d map corners2color mean
splot "../data/QueryMatrix.dat" using 1:2:10 notitle 
set terminal postscript eps "Helvetica" 24
set output "../graphics/queries/queryPeerAverageMatrix.eps"
replot

reset

# 3D-Figure for the average complexity of solved queries at a level
set title "Average Complexity of solved \nQueries in the SkyNet-tree" 
set xlabel "Query-Originator"
set xrange [0:*]
set ylabel "Query-Solver"
set yrange [0:*]
set zlabel offset 7,7
set zlabel "Complexity of\nsolved Queries"

set palette gray negative gamma 0.75
set border 4095
set view 60,210, 1, 1
set terminal png giant
set output "../graphics/queries/queryPeerAverageMatrix2.png"
set border 4095 front linetype -1 linewidth 1.000
set style line 100  linetype rgb "white" linewidth 0.500 pointtype 100 pointsize default
unset surface
set pm3d implicit at s
set pm3d interpolate 1,1 flush begin noftriangles hidden3d 100 corners2color mean
splot "../data/QueryMatrix.dat" using 1:2:10 notitle
set terminal postscript eps "Helvetica" 24
set output "../graphics/queries/queryPeerAverageMatrix2.eps"
replot

reset

#------------------------------------------------------------
# Matrix for queries with varying conditions
#------------------------------------------------------------


# Flat figure for the average complexity of solved queries at a level
set title "Average Complexity of solved \nQueries in the SkyNet-tree" 
set terminal png giant
set output "../graphics/queries/queryConditionAverageMatrix.png"
set samples 200; set isosamples 200
set style data dots
set xlabel "Query-Originator"
set xrange [0:*]
set ylabel "Query-Solver"
set yrange [0:*]
set palette gray negative gamma 0.75
set cbrange [0:*]

set border 4095
set pm3d map corners2color mean
splot "../data/QueryMatrix.dat" using 1:2:6 notitle 
set terminal postscript eps "Helvetica" 24
set output "../graphics/queries/queryConditionAverageMatrix.eps"
replot

reset

# 3D-Figure for the average complexity of solved queries at a level
set title "Average Complexity of solved \nQueries in the SkyNet-tree" 
set xlabel "Query-Originator"
set xrange [0:*]
set ylabel "Query-Solver"
set yrange [0:*]
set zlabel offset 7,7
set zlabel "Complexity of\nsolved Queries"

set palette gray negative gamma 0.75
set border 4095
set view 60,210, 1, 1
set terminal png giant
set output "../graphics/queries/queryConditionAverageMatrix2.png"
set border 4095 front linetype -1 linewidth 1.000
set style line 100  linetype rgb "white" linewidth 0.500 pointtype 100 pointsize default
unset surface
set pm3d implicit at s
set pm3d interpolate 1,1 flush begin noftriangles hidden3d 100 corners2color mean
splot "../data/QueryMatrix.dat" using 1:2:6 notitle
set terminal postscript eps "Helvetica" 24
set output "../graphics/queries/queryConditionAverageMatrix2.eps"
replot

reset
