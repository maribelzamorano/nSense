#
# The author of this plot script is Minh Hoang Nguyen
#
# The script uses the output of chord2 analyzers, 
# defined in package 'de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer'.
#

#Number of Node
set key right
set xlabel "Simulation-Time [Min]"
set ylabel "Peer"
set title "Number of online Peers"

set xrange [0:] 
set yrange [0:]

set terminal png giant
set output "../graphics/peer.png"
set style data linespoints
plot "../data/chord.dat" using 1:2 axes x1y1 ti"online Peers",\
"../data/chord.dat" using 1:3 axes x1y1 ti"online events",\
"../data/chord.dat" using 1:4 axes x1y1 ti"offline events"

set terminal postscript eps "Helvetica" 24
set output "../graphics/peer.eps"
replot

reset