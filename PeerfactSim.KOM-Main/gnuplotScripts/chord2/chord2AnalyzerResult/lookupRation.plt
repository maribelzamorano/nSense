#
# The author of this plot script is Minh Hoang Nguyen
#
# The script uses the output of chord2 analyzers, 
# defined in package 'de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer'.
#

#Ration of Successful and Valid Lookup 
set key right
set xlabel "Simulation-Time [Min]"
set ylabel "Ration"

set title "Ration of Successful and Valid Lookup "

set xrange [0:]


set terminal png giant
set output "../graphics/lookupRation.png"
set style data linespoints
plot "../data/chord.dat" using 1:16 ti"SuccLookup",\
"../data/chord.dat" using 1:17 ti"ValidLookupResult"


set terminal postscript eps "Helvetica" 24
set output "../graphics/lookupRation.eps"
replot

reset