#------------------------------------------------------------
# Visualization of the polishing-mode, the total error
# as well as the relative error of this mode
#------------------------------------------------------------

# Visualization of the total error of the currently used polishing-mode
set xlabel "Simulation-Time [s]"
set ylabel "Total Error [#Peers]"
set title "Total-Error of the utilized polishing-mode"
set grid ytics front

set terminal png giant
set output "../graphics/TotalErrPolishingMode.png"
set style data linespoints
plot "../data/AggregateStatistics.dat" using 1:4 ti"Utilized polishing-mode"
set terminal postscript eps "Helvetica" 24
set output "../graphics/TotalErrPolishingMode.eps"
replot

reset

# Visualization of the relative error of the currently used polishing-mode
set xlabel "Simulation-Time [s]"
set ylabel "Relative Error [%]"
set title "Relative Error of the utilized polishing-mode"
set grid ytics front

set terminal png giant
set output "../graphics/RelErrPolishingMode.png"
set style data linespoints
plot "../data/AggregateStatistics.dat" using 1:($5*100) ti"Utilized polishing-mode"
set terminal postscript eps "Helvetica" 24
set output "../graphics/RelErrPolishingMode.eps"
replot

reset

# Visualization of the least square Error of the currently used polishing-mode
set xlabel "Simulation-Time [s]"
set ylabel "Error"
set title "Least Square Error of the utilized polishing-mode"
set grid ytics front

set terminal png giant
set output "../graphics/LeastSquareErrPolishingMode.png"
set style data lines
plot "../data/AggregateStatistics.dat" using 1:6 ti"Utilized polishing-mode"
set terminal postscript eps "Helvetica" 24
set output "../graphics/LeastSquareErrPolishingMode.eps"
replot

reset

