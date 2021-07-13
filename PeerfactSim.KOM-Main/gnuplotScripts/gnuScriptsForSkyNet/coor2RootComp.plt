reset

# This plt-file will be used for later visualization of the data situated at CompleteCoor2Root.dat!
# At the moment this file is used for testing how to access the different recordings of the dat-file

# Visualizing the amount of nodes at different levels
set xlabel "Time"
set ylabel "Amount of Nodes"
set title "Amount of Nodes at different levels"

set terminal png giant
set output "../graphics/coor2RootCompare/AmountOfNodes.png"
set style data linespoints
set key left top
set grid ytics
load 'nodeAmount.plt'
set terminal postscript eps "Helvetica" 24
set output "../graphics/coor2RootCompare/AmountOfNodes.eps"
replot

reset

# Visualizing the interpolated amount of nodes
set xlabel "Time"
set ylabel "Amount of Nodes"
set title "Interpolated amount of Nodes"

set terminal png giant
set output "../graphics/coor2RootCompare/InterpolatedAmountOfNodes.png"
set style data linespoints
set key right bottom
set grid ytics
load 'interpolatedAmountOfNodes.plt'
#plot "../data/CompleteCoor2Root.dat" index 0:0 using 1:3 lw 2 ti"Amount of nodes, root",\
#"../data/CompleteCoor2Root.dat" index 0:0 using 1:5 lw 1 ti"Interp. amount of nodes, level 1",\
#"../data/CompleteCoor2Root.dat" index 1:1 using 1:5 lw 1 ti"Interp. amount of nodes, level 2",\
#"../data/CompleteCoor2Root.dat" index 2:2 using 1:5 lw 1 ti"Interp. amount of nodes, level 3"
set terminal postscript eps "Helvetica" 24
set output "../graphics/coor2RootCompare/InterpolatedAmountOfNodes.eps"
replot

reset

# Visualizing the total error of node-interpolation
set xlabel "Time"
set ylabel "Total Error[# Nodes]"
set title "Total error of Node-Interpolation"

set terminal png giant
set output "../graphics/coor2RootCompare/TotalErrNodeInterpolation.png"
set style data linespoints
set key right bottom
set grid ytics
load 'totalErrNodeInterpolation.plt'
#plot [x=0:] x-x lw 2 lt rgb "#000000" notitle,\
#"../data/CompleteCoor2Root.dat" index 0:0 using 1:6 lw 1 ti"Tot err of Node-Interp, level 1",\
#"../data/CompleteCoor2Root.dat" index 1:1 using 1:6 lw 1 ti"Tot err of Node-Interp, level 2",\
#"../data/CompleteCoor2Root.dat" index 2:2 using 1:6 lw 1 ti"Tot err of Node-Interp, level 3"
set terminal postscript eps "Helvetica" 24
set output "../graphics/coor2RootCompare/TotalErrNodeInterpolation.eps"
replot

reset

# Visualizing the relative error of node-interpolation
set xlabel "Time"
set ylabel "Relative Error[%]"
set title "Relative error of Node-Interpolation"

set terminal png giant
set output "../graphics/coor2RootCompare/RelErrNodeInterpolation.png"
set style data linespoints
set key right bottom
set grid ytics
load 'relErrNodeInterpolation.plt'
#plot [x=0:] x-x lw 2 lt rgb "#000000" notitle,\
#"../data/CompleteCoor2Root.dat" index 0:0 using 1:($7*100) lw 1 ti"Rel err of Node-Interp, level 1",\
#"../data/CompleteCoor2Root.dat" index 1:1 using 1:($7*100) lw 1 ti"Rel err of Node-Interp, level 2",\
#"../data/CompleteCoor2Root.dat" index 2:2 using 1:($7*100) lw 1 ti"Rel err of Node-Interp, level 3"
set terminal postscript eps "Helvetica" 24
set output "../graphics/coor2RootCompare/RelErrNodeInterpolation.eps"
replot

reset

#--------------------------------------------------------------------
# Visualization of a metric(sent size complete messages) from the root and different Coordinators
#--------------------------------------------------------------------

# Visualizing the average value of a metric(sent size complete messages) at different levels
set xlabel "Time"
set ylabel "Average value of a metric [bytes/s]"
set title "Average value of a metric\nat different levels"

set terminal png giant
set output "../graphics/coor2RootCompare/AvgValueMetric.png"
set style data linespoints
set key left top
set grid ytics
load 'avgValueMetric.plt'
#plot "../data/CompleteCoor2Root.dat" index 0:0 using 1:77 lw 2 ti"Avg value of a metric, root",\
#"../data/CompleteCoor2Root.dat" index 0:0 using 1:76 lw 1 ti"Avg value of a metric, level 1",\
#"../data/CompleteCoor2Root.dat" index 1:1 using 1:76 lw 1 ti"Avg value of a metric, level 2",\
#"../data/CompleteCoor2Root.dat" index 2:2 using 1:76 lw 1 ti"Avg value of a metric, level 3"
set terminal postscript eps "Helvetica" 24
set output "../graphics/coor2RootCompare/AvgValueMetric.eps"
replot

reset

# Visualizing the total error of the different avg values of a metric
set xlabel "Time"
set ylabel "Total Error[bytes/s]"
set title "Total error of different\naverage values of a metric"

set terminal png giant
set output "../graphics/coor2RootCompare/TotalErrAvgMetric.png"
set style data linespoints
set key right bottom
set grid ytics
load 'totalErrAvgMetric.plt'
#plot [x=0:] x-x lw 2 lt rgb "#000000" notitle,\
#"../data/CompleteCoor2Root.dat" index 0:0 using 1:78 lw 1 ti"Tot err of avg Metric, level 1",\
#"../data/CompleteCoor2Root.dat" index 1:1 using 1:78 lw 1 ti"Tot err of Node-Interp, level 2",\
#"../data/CompleteCoor2Root.dat" index 2:2 using 1:78 lw 1 ti"Tot err of Node-Interp, level 3"
set terminal postscript eps "Helvetica" 24
set output "../graphics/coor2RootCompare/TotalErrAvgMetric.eps"
replot

reset

# Visualizing the relative error of the different avg values of a metric
set xlabel "Time"
set ylabel "Relative Error[%]"
set title "Relative error of different\naverage values of a metric"

set terminal png giant
set output "../graphics/coor2RootCompare/RelErrAvgMetric.png"
set style data linespoints
set key right bottom
set grid ytics
load 'relErrAvgMetric.plt'
#plot [x=0:] x-x lw 2 lt rgb "#000000" notitle,\
#"../data/CompleteCoor2Root.dat" index 0:0 using 1:($79*100) lw 1 ti"Rel err of Node-Interp, level 1",\
#"../data/CompleteCoor2Root.dat" index 1:1 using 1:($79*100) lw 1 ti"Rel err of Node-Interp, level 2",\
#"../data/CompleteCoor2Root.dat" index 2:2 using 1:($79*100) lw 1 ti"Rel err of Node-Interp, level 3"
set terminal postscript eps "Helvetica" 24
set output "../graphics/coor2RootCompare/RelErrAvgMetric.eps"
replot

reset
