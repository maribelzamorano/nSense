#
# The author of this plot script is Julius Rueckert
#
# The script uses the output of the two chord2 analyzers:
#	- de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.ChordStructureAnalyzer
#	- de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.LookupComplexityAnalyzer 
#
# Place the script in the same folder as the two output files
#	- 'Structure.dat'
#	- 'LookupComplexity.dat'
#
# By default these files are located in a sub-folder of 'outputs/Chord/...'
#

#------------------------------------------------------------
# Plot Chord structure metrics
#------------------------------------------------------------

###
# Available Data fields Structure.dat
###
#time[sec]
#time[min]
#PRESENT nodes
#TO_JOIN nodes
#CHURN nodes
#Succ ring size
#Succ ring connected?
#Succ num succ ring breaks
#Succ ring connected (using backups)?
#Succ ring includes all?
#Succ num not included nodes
#Pred ring size
#Pred ring connected?
#Pred num pred ring breaks
#Pred ring connected (using backups)?
#Pred ring includes all?
#Pred num not included nodes


# Plot node states
reset
set title "Node States"
set terminal png giant
set output "Node_States.png"
set style data linespoints
set xlabel "Simulation time [min]"
set ylabel "Number of peers"
plot "Structure.dat" using 2:3 title 'PRESENT',\
"Structure.dat" using 2:4 title 'TO_JOIN',\
"Structure.dat" using 2:5 title 'CHURN'


# Plot ring breaks
reset
set title "Ring breaks"
set terminal png giant
set output "Ring_Breaks.png"
set style data linespoints
set xlabel "Simulation time [min]"
set ylabel "Number of ring breaks"
plot "Structure.dat" using 2:8 title 'Successor ring',\
"Structure.dat" using 2:14 title 'Predecessor ring'


# Plot ring sizes
reset
set title "Ring sizes"
set terminal png giant
set output "Ring_Sizes.png"
set style data linespoints
set xlabel "Simulation time [min]"
set ylabel "Number of peers"
plot "Structure.dat" using 2:6 title 'Successor ring size',\
"Structure.dat" using 2:11 title 'Successor ring - not included',\
"Structure.dat" using 2:12 title 'Predecessor ring size',\
"Structure.dat" using 2:17 title 'Predecessor ring - not included'


# Plot peer states vs. ring sizes
reset
set title "Peer states vs. ring sizes"
set terminal png giant
set output "Peer_States_vs_Ring_Sizes.png"
set style data linespoints
set xlabel "Simulation time [min]"
set ylabel "Number of peers"
plot "Structure.dat" using 2:3 title 'PRESENT',\
"Structure.dat" using 2:4 title 'TO_JOIN',\
"Structure.dat" using 2:5 title 'CHURN',\
"Structure.dat" using 2:6 title 'Successor ring size',\
"Structure.dat" using 2:12 title 'Predecessor ring size'


#------------------------------------------------------------
# Plot Lookup Complexity metrics
#------------------------------------------------------------

###
# Available Data fields LookupComplexity.dat
###
#time[s]
#time[min]
#PRESENT Peers
#TO_JOIN Peers
#CHURN Peers
#NumOfLookups
#Lookup hops(avg)
#Lookup hops(st.Dev.Minus)
#Lookup hops(st.Dev.Plus)
#Lookup hops(median)

# Plot lookup hops avg
reset
set title "Average hops on lookups"
set terminal png giant
set output "Complexity_AvgLookupHops.png"
set style data linespoints
set xlabel "Simulation time [min]"
set ylabel "Number of hops"
set y2label "Number peers / lookups"
set y2tics 0, 1000
set ytics nomirror
plot "LookupComplexity.dat" using 2:7:($7-$8):($7+$9) title 'AVG' with yerrorlines axis x1y1,\
"LookupComplexity.dat" using 2:10 title 'Median' axis x1y1,\
"LookupComplexity.dat" using 2:(log($3)) title 'log(PRESENT Peers)' axis x1y1,\
"LookupComplexity.dat" using 2:3 title 'PRESENT Peers' axis x1y2,\
"LookupComplexity.dat" using 2:5 title 'CHURN Peers' axis x1y2, \
"LookupComplexity.dat" using 2:6 title 'NumOfLookups' axis x1y2


