#
# The author of this plot script is Minh Hoang Nguyen
#
# The script uses the output of chord2 analyzers, 
# defined in package 'de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer'.
#

#Number of Messages - Sent and Receive
set key right
set xlabel "Simulation-Time [Min]"
set ylabel "Number of Messages [1/min]"
set title "Number of Messages - Sent and Receive"

set yrange [0:]
set xrange [0:] 

set terminal png giant
set output "../graphics/msgExchange.png"
set style data linespoints
plot "../data/chord.dat" using 1:($5+$7) ti"Sent Messages",\
"../data/chord.dat" using 1:($6+$8) ti"Received Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/msgExchange.eps"
replot

reset

#Number of Messages - Sent and Receive per Peer
set key right
set xlabel "Simulation-Time [Min]"
set ylabel "Number of Messages [1/min] per Peer"
set title "Number of Messages - Sent and Receive per Peer"

set yrange [0:]
set xrange [5:] 

set terminal png giant
set output "../graphics/msgExchangePerPeer.png"
set style data linespoints
plot "../data/chord.dat" using 1:(($5+$7)/$2) ti"Sent Messages",\
"../data/chord.dat" using 1:(($6+$8)/$2) ti"Received Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/msgExchangePerPeer.eps"
replot

reset