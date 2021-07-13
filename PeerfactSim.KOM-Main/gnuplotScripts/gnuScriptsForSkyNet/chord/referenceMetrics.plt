#------------------------------------------------------------
# Plot of the reference metrics
#------------------------------------------------------------

# Graph for simple ZigZag metric
set title "ZigZag metric at root"
set terminal png giant
set output "../graphics/ReferenceMetrics_ZigZag.png"
set style data linespoints
#set xlabel "Number of peers"
#set xrange [1:*]
set ylabel "Value"
#set yrange [0:*]
plot "../data/Metrics.dat" using 1:120 title 'ZigZag T=1m',\
"../data/Metrics.dat" using 1:122 title 'ZigZag T=3m',\
"../data/Metrics.dat" using 1:119 title 'ZigZag T=10m',\
"../data/Metrics.dat" using 1:121 title 'ZigZag T=30m'
set terminal postscript eps "Helvetica" 24
set output "../graphics/ReferenceMetrics_ZigZag.eps"
replot

set title "ZigZag metric at root"
set terminal png giant
set output "../graphics/ReferenceMetrics_ZigZag_1m.png"
set style data linespoints
#set xlabel "Number of peers"
#set xrange [1:*]
set ylabel "Value"
#set yrange [0:*]
plot "../data/Metrics.dat" using 1:120 title 'SKYNET ZigZag T=1m',\
"../data/Metrics.dat" using 1:57 title 'ZigZag T=1m'
set terminal postscript eps "Helvetica" 24
set output "../graphics/ReferenceMetrics_ZigZag_1m.eps"
replot

set title "ZigZag metric at root"
set terminal png giant
set output "../graphics/ReferenceMetrics_ZigZag_3m.png"
set style data linespoints
#set xlabel "Number of peers"
#set xrange [1:*]
set ylabel "Value"
#set yrange [0:*]
plot "../data/Metrics.dat" using 1:122 title 'SKYNET ZigZag T=3m',\
"../data/Metrics.dat" using 1:59 title 'ZigZag T=3m'
set terminal postscript eps "Helvetica" 24
set output "../graphics/ReferenceMetrics_ZigZag_3m.eps"
replot

set title "ZigZag metric at root"
set terminal png giant
set output "../graphics/ReferenceMetrics_ZigZag_10m.png"
set style data linespoints
#set xlabel "Number of peers"
#set xrange [1:*]
set ylabel "Value"
#set yrange [0:*]
plot "../data/Metrics.dat" using 1:119 title 'SKYNET ZigZag T=10m',\
"../data/Metrics.dat" using 1:56 title 'ZigZag T=10m'
set terminal postscript eps "Helvetica" 24
set output "../graphics/ReferenceMetrics_ZigZag_10m.eps"
replot

set title "ZigZag metric at root"
set terminal png giant
set output "../graphics/ReferenceMetrics_ZigZag_30m.png"
set style data linespoints
#set xlabel "Number of peers"
#set xrange [1:*]
set ylabel "Value"
#set yrange [0:*]
plot "../data/Metrics.dat" using 1:121 title 'SKYNET ZigZag T=30m',\
"../data/Metrics.dat" using 1:58 title 'ZigZag T=30m'
set terminal postscript eps "Helvetica" 24
set output "../graphics/ReferenceMetrics_ZigZag_30m.eps"
replot


# Graph for Sine metric
set title "Sine metric at root"
set terminal png giant
set output "../graphics/ReferenceMetrics_Sine.png"
set style data linespoints
#set xlabel "Number of peers"
#set xrange [1:*]
set ylabel "Value"
#set yrange [0:*]
plot "../data/Metrics.dat" using 1:124 title 'Sine T=1m',\
"../data/Metrics.dat" using 1:126 title 'Sine T=3m',\
"../data/Metrics.dat" using 1:123 title 'Sine T=10m',\
"../data/Metrics.dat" using 1:125 title 'Sine T=30m'
set terminal postscript eps "Helvetica" 24
set output "../graphics/ReferenceMetrics_Sine.eps"
replot

set title "Sine metric at root"
set terminal png giant
set output "../graphics/ReferenceMetrics_Sine_1m.png"
set style data linespoints
#set xlabel "Number of peers"
#set xrange [1:*]
set ylabel "Value"
#set yrange [0:*]
plot "../data/Metrics.dat" using 1:124 title 'SKYNET Sine T=1m',\
"../data/Metrics.dat" using 1:61 title 'Sine T=1m'
set terminal postscript eps "Helvetica" 24
set output "../graphics/ReferenceMetrics_Sine_1m.eps"
replot

set title "Sine metric at root"
set terminal png giant
set output "../graphics/ReferenceMetrics_Sine_3m.png"
set style data linespoints
#set xlabel "Number of peers"
#set xrange [1:*]
set ylabel "Value"
#set yrange [0:*]
plot "../data/Metrics.dat" using 1:126 title 'SKYNET Sine T=3m',\
"../data/Metrics.dat" using 1:63 title 'Sine T=3m'
set terminal postscript eps "Helvetica" 24
set output "../graphics/ReferenceMetrics_Sine_3m.eps"
replot

set title "Sine metric at root"
set terminal png giant
set output "../graphics/ReferenceMetrics_Sine_10m.png"
set style data linespoints
#set xlabel "Number of peers"
#set xrange [1:*]
set ylabel "Value"
#set yrange [0:*]
plot "../data/Metrics.dat" using 1:123 title 'SKYNET Sine T=10m',\
"../data/Metrics.dat" using 1:60 title 'Sine T=10m'
set terminal postscript eps "Helvetica" 24
set output "../graphics/ReferenceMetrics_Sine_10m.eps"
replot

set title "Sine metric at root"
set terminal png giant
set output "../graphics/ReferenceMetrics_Sine_30m.png"
set style data linespoints
#set xlabel "Number of peers"
#set xrange [1:*]
set ylabel "Value"
#set yrange [0:*]
plot "../data/Metrics.dat" using 1:125 title 'SKYNET Sine T=30m',\
"../data/Metrics.dat" using 1:62 title 'Sine T=30m'
set terminal postscript eps "Helvetica" 24
set output "../graphics/ReferenceMetrics_Sine_30m.eps"
replot