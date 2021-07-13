#
# The author of this plot script is Minh Hoang Nguyen
#
# The script uses the output of chord2 analyzers, 
# defined in package 'de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer'.
#

#constant values
#@see class ChordConfiguration
direct_neighbours = 2;
distant_neighbours = 6;
num_fingerPoints = 160;


#Stabilization stats for Chord overlay
set key right
set xlabel "Simulation-Time [Min]"
set ylabel "Number of Peers"
set y2label "Number of FingerPoints"
set y2tics
set title "Number of invalid neighbours \nand finger points"

set xrange [0:]
set yrange [0:]

set terminal png giant
set output "../graphics/stabilize.png"
set style data linespoints
plot "../data/stabilize.dat" using 1:3 axes x1y1 ti"Invalid Neighbours",\
"../data/stabilize.dat" using 1:4 axes x1y2 ti"Invalid FingerPoints"


set terminal postscript eps "Helvetica" 24
set output "../graphics/stabilize.eps"
replot

reset

#Stabilization ration
set key right
set xlabel "Simulation-Time [Min]"
set ylabel "Neighbours"
set y2label "FingerPoints"
set y2tics

set title "Ration of invalid neighbours \nand finger points"

set xrange [0:]
set yrange [0:]

set terminal png giant
set output "../graphics/stabilizeRation.png"
set style data linespoints
plot "../data/stabilize.dat" using 1:($3/(direct_neighbours * $2)) axes x1y1 ti"Invalid Direct Neighbours",\
"../data/stabilize.dat" using 1:($6/(distant_neighbours * $2)) axes x1y1 ti"Invalid Distant Neighbours",\
"../data/stabilize.dat" using 1:($4/(num_fingerPoints * $2)) axes x1y2 ti"Invalid FingerPoints"

set terminal postscript eps "Helvetica" 24
set output "../graphics/stabilizeRation.eps"
replot

reset


#Stabilization invalid direct neighbours and distant neighbours
set key right
set xlabel "Simulation-Time [Min]"
set ylabel "invalid neighbours"
set title "Invalid Direct and Distant Neighbours"

set xrange [0:]
set yrange [0:]

set terminal png giant
set output "../graphics/stabilizeDD.png"
set style data linespoints
plot "../data/stabilize.dat" using 1:3 ti"Direct Neighbours",\
"../data/stabilize.dat" using 1:6  ti"Distant Neighbours"


set terminal postscript eps "Helvetica" 24
set output "../graphics/stabilizeDD.eps"
replot

reset