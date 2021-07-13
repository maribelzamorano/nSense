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
plot "../data/Metrics.dat" using 1:93 title 'ZigZag T=1m',\
"../data/Metrics.dat" using 1:95 title 'ZigZag T=3m',\
"../data/Metrics.dat" using 1:92 title 'ZigZag T=10m',\
"../data/Metrics.dat" using 1:94 title 'ZigZag T=30m'
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
plot "../data/Metrics.dat" using 1:93 title 'SKYNET ZigZag T=1m',\
"../data/Metrics.dat" using 1:44 title 'ZigZag T=1m'
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
plot "../data/Metrics.dat" using 1:95 title 'SKYNET ZigZag T=3m',\
"../data/Metrics.dat" using 1:46 title 'ZigZag T=3m'
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
plot "../data/Metrics.dat" using 1:92 title 'SKYNET ZigZag T=10m',\
"../data/Metrics.dat" using 1:43 title 'ZigZag T=10m'
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
plot "../data/Metrics.dat" using 1:94 title 'SKYNET ZigZag T=30m',\
"../data/Metrics.dat" using 1:45 title 'ZigZag T=30m'
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
plot "../data/Metrics.dat" using 1:48 title 'Sine T=1m',\
"../data/Metrics.dat" using 1:50 title 'Sine T=3m',\
"../data/Metrics.dat" using 1:47 title 'Sine T=10m',\
"../data/Metrics.dat" using 1:49 title 'Sine T=30m'
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
plot "../data/Metrics.dat" using 1:97 title 'SKYNET Sine T=1m',\
"../data/Metrics.dat" using 1:48 title 'Sine T=1m'
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
plot "../data/Metrics.dat" using 1:99 title 'SKYNET Sine T=3m',\
"../data/Metrics.dat" using 1:50 title 'Sine T=3m'
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
plot "../data/Metrics.dat" using 1:96 title 'SKYNET Sine T=10m',\
"../data/Metrics.dat" using 1:47 title 'Sine T=10m'
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
plot "../data/Metrics.dat" using 1:98 title 'SKYNET Sine T=30m',\
"../data/Metrics.dat" using 1:49 title 'Sine T=30m'
set terminal postscript eps "Helvetica" 24
set output "../graphics/ReferenceMetrics_Sine_30m.eps"
replot