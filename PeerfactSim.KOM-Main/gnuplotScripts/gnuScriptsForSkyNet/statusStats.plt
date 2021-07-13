#------------------------------------------------------------
# Structure of the SkyNet-tree during a simulation
#------------------------------------------------------------

# Flat figure of the Structure of the SkyNet-tree during a simulation
set title "Structure of the SkyNet-tree during a simulation"
set terminal png giant
set output "../graphics/NumberLevelMatrix.png"
set style data dots
set xlabel "Periode [m]"
set xrange [1:*]
set ylabel "TreeLevel"
set yrange [0:*]
set cbrange [0:*]

set palette gray negative # gamma 0.5
set border 4095
set pm3d map
splot "../data/NumberLevelMatrix.dat" using 1:2:3 notitle
set terminal postscript eps "Helvetica" 24
set output "../graphics/NumberLevelMatrix.eps"
replot

reset

# Graph to show tree depth
set title "Depth of the SkyNet-tree"
set terminal png giant
set output "../graphics/TreeDepthOverPeerCount.png"
set style data linespoints
set xlabel "Number of peers"
set ylabel "Tree Depth"

plot "../data/TreeDepth.dat" using 2:4 title 'max. level', \
"../data/TreeDepth.dat" using 2:5 title 'max. level (95-percentile)'
set terminal postscript eps "Helvetica" 24
set output "../graphics/TreeDepthOverPeerCount.eps"
replot

reset

set title "Depth of the SkyNet-tree during a simulation"
set terminal png giant
set output "../graphics/TreeDepthOverTime.png"
set style data linespoints
set xlabel "Periode [m]"
set ylabel "Tree Depth"

plot "../data/TreeDepth.dat" using 1:4 title 'max. level', \
"../data/TreeDepth.dat" using 1:5 title 'max. level (95-percentile)'
set terminal postscript eps "Helvetica" 24
set output "../graphics/TreeDepthOverTime.eps"
replot

reset

# Graph to show deviation of tree depth
set title "Deviation of tree depth"
set terminal png giant
set output "../graphics/TreeDepthDeviationOverPeerCount.png"
set style data linespoints
set xlabel "Number of peers"
#set xrange [1:*]
set ylabel "Absolute deviation"
set y2label "Relative deviation"
#set yrange [0:*]

plot "../data/TreeDepth.dat" using 2:10 axes x1y1 title 'abs. deviation',\
"../data/TreeDepth.dat" using 2:11 axes x1y1 title 'abs. deviation (95-percentile)',\
"../data/TreeDepth.dat" using 2:12 axes x1y2 title 'rel. deviation',\
"../data/TreeDepth.dat" using 2:13 axes x1y2 title 'rel. deviation (95-percentile)'
set terminal postscript eps "Helvetica" 24
set output "../graphics/TreeDepthDeviationOverPeerCount.eps"
replot

reset

set title "Deviation of tree depth during a simulation"
set terminal png giant
set output "../graphics/TreeDepthDeviationOverTime.png"
set style data linespoints
set xlabel "Periode [m]"
#set xrange [1:*]
set ylabel "Absolute deviation"
set y2label "Relative deviation"
#set yrange [0:*]

plot "../data/TreeDepth.dat" using 1:10 axes x1y1 title 'abs. deviation',\
"../data/TreeDepth.dat" using 1:11 axes x1y1 title 'abs. deviation (95-percentile)',\
"../data/TreeDepth.dat" using 1:12 axes x1y2 title 'rel. deviation',\
"../data/TreeDepth.dat" using 1:13 axes x1y2 title 'rel. deviation (95-percentile)'
set terminal postscript eps "Helvetica" 24
set output "../graphics/TreeDepthDeviationOverTime.eps"
replot

reset

# Graph to show average level of peers incl. stDev. hull
set title "Average level of peers"
set terminal png giant
set output "../graphics/TreeAverageLevelOverPeerCount.png"
set style data linespoints
set xlabel "Number of peers"
#set xrange [1:*]
set ylabel "Average level of peers"
#set yrange [0:*]

plot "../data/TreeDepth.dat" using 2:6:($6-$8):($6+$9) title 'average level' with yerrorlines
set terminal postscript eps "Helvetica" 24
set output "../graphics/TreeAverageLevelOverPeerCount.eps"
replot

reset

set title "Average level of peers during a simulation"
set terminal png giant
set output "../graphics/TreeAverageLevelOverTime.png"
set style data linespoints
set xlabel "Periode [m]"
#set xrange [1:*]
set ylabel "Average level of peers"
#set yrange [0:*]

plot "../data/TreeDepth.dat" using 1:6:($6-$8):($6+$9) title 'average level' with yerrorlines
set terminal postscript eps "Helvetica" 24
set output "../graphics/TreeAverageLevelOverTime.eps"
replot

reset

# First 3D-figure of the Structure of the SkyNet-tree during a simulation
set title "Structure of the SkyNet-tree during a simulation" 
set xlabel "Periode [m]"
set xrange [1:*]
set ylabel "TreeLevel"
set yrange [0:*]
set zlabel offset 7,7
set zlabel "Number of Nodes"
set grid ztics

set terminal png giant
set output "../graphics/NumberLevelMatrix4.png"
set border 4095 front linetype -1 linewidth 1.000
set style line 100  linetype rgb "black" linewidth 0.500 pointtype 100 pointsize default
set view 60,340, 1, 1
set palette gray negative # gamma 0.5
set cbrange [0:*]
unset surface
set pm3d implicit at s
set pm3d interpolate 1,1 flush begin noftriangles hidden3d 100 corners2color mean
splot "../data/NumberLevelMatrix.dat" every 1:10 notitle
set terminal postscript eps "Helvetica" 24
set output "../graphics/NumberLevelMatrix4.eps"
replot

reset

# Second 3D-figure of the Structure of the SkyNet-tree during a simulation
set title "Structure of the SkyNet-tree during a simulation" 
set xlabel "Periode [m]"
set xrange [1:*]
set ylabel "TreeLevel"
set yrange [0:*]
set zlabel offset 7,7
set zlabel "Number of Nodes"
set grid ztics

set terminal png giant
set output "../graphics/NumberLevelMatrix5.png"
set border 4095 front linetype -1 linewidth 1.000
set style line 100  linetype rgb "black" linewidth 0.500 pointtype 100 pointsize default
set view 70,280, 1, 1
set palette gray negative # gamma 0.5
set cbrange [0:*]
unset surface
set pm3d implicit at s
set pm3d interpolate 1,1 flush begin noftriangles hidden3d 100 corners2color mean
splot "../data/NumberLevelMatrix.dat" every 1:10 notitle
set terminal postscript eps "Helvetica" 24
set output "../graphics/NumberLevelMatrix5.eps"
replot

reset

# Graph to show number of root changes over time
set title "Number of root changes"
set terminal png giant
set output "../graphics/RootChanges.png"
set style data linespoints
set xlabel "Periode [m]"
set ylabel "Number of root changes"

plot "../data/RootChanges.dat" using 1:2 title 'Number of root losses',\
"../data/RootChanges.dat" using 1:3 title 'Number of takeovers'
set terminal postscript eps "Helvetica" 24
set output "../graphics/RootChanges.eps"
replot

reset

# Graph to show the freshness of metric aggregates
set title "Freshness of Metric Aggregates"
set terminal png giant
set output "../graphics/freshnessMetricAgg.png"
set style data linespoints
set xlabel "Simulation Time [s]"
set ylabel "Update Time [s]"

plot "../data/AggregateStatistics.dat" using 1:($293/1000000) title 'Min. Update Time',\
"../data/AggregateStatistics.dat" using 1:($295/1000000) title 'Avg. Update Time',\
"../data/AggregateStatistics.dat" using 1:($292/1000000) title 'Max. Update Time'
set terminal postscript eps "Helvetica" 24
set output "../graphics/freshnessMetricAgg.eps"
replot

reset
