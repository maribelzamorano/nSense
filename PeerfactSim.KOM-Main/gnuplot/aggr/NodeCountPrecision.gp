set terminal png transparent nocrop enhanced font arial 8 size 2000, 1000
set output 'results/nodeCountPrecision.png'

set style fill
set boxwidth 1
set ticslevel 0

set grid

set xrange [* : *]
set yrange [-1 : 300]
set zrange [0 : *]
set view 5,20,1.3,2.5
#set view map
splot '../../outputs/aggr/measuredNodeCounts' using 1:2:3:3 title 'Measured node counts' with boxes linecolor palette lw 1, '../../outputs/aggr/actualNodeCount' using 1:2:(0) title 'Exact node count' with lines linecolor rgb "green" lw 2
