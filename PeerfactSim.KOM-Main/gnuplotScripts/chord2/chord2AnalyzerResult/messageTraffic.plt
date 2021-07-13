#
# The author of this plot script is Minh Hoang Nguyen
#
# The script uses the output of chord2 analyzers, 
# defined in package 'de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer'.
#

#Number of Messages - Sent and Receive
set key right
set xlabel "Simulation-Time [Min]"
set ylabel "Network-Traffic [bytes/min]"
set title "Network-Traffic of sent and rec"

set xrange [0:] 
set yrange [0:]

set terminal png giant
set output "../graphics/msgTraffic.png"
set style data linespoints
plot "../data/chord.dat" using 1:($9+$11) ti"Network-Traffic of sent Messages",\
"../data/chord.dat" using 1:($10+$12) ti"Network-Traffic of rec Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/msgTraffic.eps"
replot

reset

#Number of Messages - Sent and Receive per Peer
set key right
set xlabel "Simulation-Time [Min]"
set ylabel "Network-Traffic [bytes/min] per Peer"
set title "Network-Traffic of sent \nand rec per Peer"

set xrange [0:] 
set yrange [0:]

set terminal png giant
set output "../graphics/msgTrafficPerPeer.png"
set style data linespoints
plot "../data/chord.dat" using 1:(($9+$11)/$2) ti"Network-Traffic of sent Messages",\
"../data/chord.dat" using 1:(($10+$12)/$2) ti"Network-Traffic of rec Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/msgTrafficPerPeer.eps"
replot

reset