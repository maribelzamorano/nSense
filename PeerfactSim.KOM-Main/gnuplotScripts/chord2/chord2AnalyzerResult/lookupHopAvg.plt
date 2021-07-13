#
# The author of this plot script is Minh Hoang Nguyen
#
# The script uses the output of chord2 analyzers, 
# defined in package 'de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer'.
#

#Average Hops and Average Lookup Time
set key right
set xlabel "Simulation-Time [Min]"
set ylabel "Hop"
set y2label "Time[s]"
set y2tics
set title "Average Hops and Average Lookup Time"

set xrange [0:]
set yrange [0:]
set y2range [0:]
set terminal png giant
set output "../graphics/lookupHopAvg.png"
set style data linespoints
plot "../data/chord.dat" using 1:14 axes x1y1 ti"Average Hops",\
"../data/chord.dat" using 1:13 axes x1y2 ti"Average LookupTime"


set terminal postscript eps "Helvetica" 24
set output "../graphics/lookupHopAvg.eps"
replot

reset