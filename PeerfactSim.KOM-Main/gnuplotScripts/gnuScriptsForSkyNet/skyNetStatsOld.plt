#ratio of online-peers counted by the root compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Number of Peers"
set title "Counted Online-Peers by the Root\nvs.\nReal Amount of Online-Peers"

set terminal png giant
set output "../graphics/RatioOnlinePeers.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:2 ti"Real Online-Peers",\
"../data/Metrics.dat" using 1:3 ti"Real Offline-Peers",\
"../data/Metrics.dat" using 1:50 ti"Counted Online-Peers"
set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioOnlinePeers.eps"
replot

reset

#Amount of utilized Support Peers in relation to the total amount of nodes in the overlay
set key right bottom
set xlabel "Simulation-Time [s]"
set ylabel "Number of Peers"
set y2label "Number of utilised Support Peers"
set y2tics
set title "Amount of utilised Support Peers \nin relation to the total\nAmount of nodes in the overlay"

set terminal png giant
set output "../graphics/UtilisedSupportPeers.png"
set style data linespoints
plot "../data/attributes.dat" using 1:2 lw 5 axes x1y1 ti"Online-Peers",\
"../data/attributes.dat" using 1:3 axes x1y1 ti"Offline-Peers",\
"../data/attributes.dat" using 1:6 axes x1y2 ti"Utilised Support Peers"


set terminal postscript eps "Helvetica" 24
set output "../graphics/UtilisedSupportPeers.eps"
replot

reset

#ratio of attributeEntries counted by the root and its SupportPeer compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Number of AttributeEntries"
set title "AttributeEntries counted by the root and its SupportPeer\nvs.\nAvailable Amount of Online-Peers"

set terminal png giant
set output "../graphics/RatioAttributeEntries.png"
set style data linespoints
plot "../data/attributes.dat" using 1:2 ti"Online-Peers",\
"../data/attributes.dat" using 1:3 ti"Offline-Peers",\
"../data/attributes.dat" using 1:4 ti"AttributeEntries of the root",\
"../data/attributes.dat" using 1:5 ti"AttributeEntries of the SupportPeer",\
"../data/attributes.dat" using 1:($4+$5) ti"AttributeEntries of the root and the SupportPeer"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioAttributeEntries.eps"
replot

reset

#ratio of BandwidthConsumption measured by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Bandwidth-Consumption [%/s]"
set title "Measured Bandwidth-Consumption\nvs.\nReal Bandwidth-Consumption"
set key right center

set terminal png giant
set output "../graphics/RatioBandwidthConsumption.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:($6*100) ti"AverageRecBandwidthConsumption",\
"../data/Metrics.dat" using 1:($7*100) ti"AverageSentBandwidthConsumption",\
"../data/Metrics.dat" using 1:($53*100) ti"Measured AverageRecBandwidthConsumption",\
"../data/Metrics.dat" using 1:($54*100) ti"Measured AverageSentBandwidthConsumption"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioBandwidthConsumption.eps"
replot

reset

#---------------------------------------------------------
#plots of message-exchanges
#---------------------------------------------------------

#ratio of Message-Exchange counted by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Number of Messages [1/s]"
set title "Measured Message-Exchange\nvs.\nReal Message-Exchange"
set key right bottom

set terminal png giant
set output "../graphics/RatioMsgExchange.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:12 ti"Received Messages",\
"../data/Metrics.dat" using 1:31 ti"Sent Messages",\
"../data/Metrics.dat" using 1:59 ti"Measured Received Messages",\
"../data/Metrics.dat" using 1:78 ti"Measured Sent Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioMsgExchange.eps"
replot

reset

#ratio of Overlay-Message-Exchange counted by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Number of Overlay-Messages [1/s]"
set title "Measured Overlay-Message-Exchange\nvs.\nReal Overlay-Message-Exchange"
set key right bottom

set terminal png giant
set output "../graphics/RatioOverlayMsgExchange.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:18 ti"Received Overlay-Messages",\
"../data/Metrics.dat" using 1:37 ti"Sent Overlay-Messages",\
"../data/Metrics.dat" using 1:65 ti"Measured Received Overlay-Messages",\
"../data/Metrics.dat" using 1:84 ti"Measured Sent Overlay-Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioOverlayMsgExchange.eps"
replot

reset

#ratio of SkyNet-Message-Exchange counted by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Number of SkyNet-Messages [1/s]"
set title "Measured SkyNet-Message-Exchange\nvs.\nReal SkyNet-Message-Exchange"
set key right bottom

set terminal png giant
set output "../graphics/RatioSkyNetMsgExchange.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:29 ti"Received SkyNet-Messages",\
"../data/Metrics.dat" using 1:48 ti"Sent SkyNet-Messages",\
"../data/Metrics.dat" using 1:76 ti"Measured Received SkyNet-Messages",\
"../data/Metrics.dat" using 1:95 ti"Measured Sent SkyNet-Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioSkyNetMsgExchange.eps"
replot

reset

#ratio of Lookup-Message-Exchange counted by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Number of Lookup-Messages [1/s]"
set title "Measured Lookup-Message-Exchange\nvs.\nReal Lookup-Message-Exchange"
set key right bottom

set terminal png giant
set output "../graphics/RatioLookupMsgExchange.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:14 ti"Received Lookup-Messages",\
"../data/Metrics.dat" using 1:33 ti"Sent Lookup-Messages",\
"../data/Metrics.dat" using 1:61 ti"Measured Received Lookup-Messages",\
"../data/Metrics.dat" using 1:80 ti"Measured Sent Lookup-Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioLookupMsgExchange.eps"
replot

reset

#ratio of PingPong-Message-Exchange counted by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Number of PingPong-Messages [1/s]"
set title "Measured PingPong-Message-Exchange\nvs.\nReal PingPong-Message-Exchange"

set terminal png giant
set output "../graphics/RatioPingPongMsgExchange.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:19 ti"Received PingPong-Messages",\
"../data/Metrics.dat" using 1:38 ti"Sent PingPong-Messages",\
"../data/Metrics.dat" using 1:66 ti"Measured Received PingPong-Messages",\
"../data/Metrics.dat" using 1:85 ti"Measured Sent PingPong-Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioPingPongMsgExchange.eps"
replot

reset

#ratio of metricUpdate-Message-Exchange counted by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Number of MetricUpdate-Messages [1/s]"
set title "Measured MetricUpdate-Message-Exchange\nvs.\nReal MetricUpdate-Message-Exchange"
set key right bottom

set terminal png giant
set output "../graphics/RatioMetricUpdateMsgExchange.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:17 ti"Received MetricUpdate-Messages",\
"../data/Metrics.dat" using 1:36 ti"Sent MetricUpdate-Messages",\
"../data/Metrics.dat" using 1:64 ti"Measured Received MetricUpdate-Messages",\
"../data/Metrics.dat" using 1:83 ti"Measured Sent MetricUpdate-Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioMetricUpdateMsgExchange.eps"
replot

reset

#ratio of metricUpdateACK-Message-Exchange counted by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Number of MetricUpdateACK-Messages [1/s]"
set title "Measured MetricUpdateACK-Message-Exchange\nvs.\nReal MetricUpdateACK-Message-Exchange"
set key right bottom

set terminal png giant
set output "../graphics/RatioMetricUpdateACKMsgExchange.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:16 ti"Received MetricUpdateACK-Messages",\
"../data/Metrics.dat" using 1:35 ti"Sent MetricUpdateACK-Messages",\
"../data/Metrics.dat" using 1:63 ti"Measured Received MetricUpdateACK-Messages",\
"../data/Metrics.dat" using 1:82 ti"Measured Sent MetricUpdateACK-Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioMetricUpdateACKMsgExchange.eps"
replot

reset

#ratio of AttributeUpdate-Message-Exchange counted by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Number of AttributeUpdate-Messages [1/s]"
set title "Measured AttributeUpdate-Message-Exchange\nvs.\nReal AttributeUpdate-Message-Exchange"
set key right bottom

set terminal png giant
set output "../graphics/RatioAttributeUpdateMsgExchange.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:11 ti"Received AttributeUpdate-Messages",\
"../data/Metrics.dat" using 1:30 ti"Sent AttributeUpdate-Messages",\
"../data/Metrics.dat" using 1:58 ti"Measured Received AttributeUpdate-Messages",\
"../data/Metrics.dat" using 1:77 ti"Measured Sent AttributeUpdate-Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioAttributeUpdateMsgExchange.eps"
replot

reset

#---------------------------------------------------------
#plots of amounts of bytes, sent and received
#---------------------------------------------------------

#ratio of the network-traffic measured by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Complete network-traffic [bytes/s]"
set title "Measured Network-Traffic\nvs.\nReal network-traffic"
set key right bottom

set terminal png giant
set output "../graphics/RatioAllNetworkTraffic.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:24 ti"Network-Traffic of rec Messages",\
"../data/Metrics.dat" using 1:43 ti"Network-Traffic of sent Messages",\
"../data/Metrics.dat" using 1:71 ti"Measured Network-Traffic of rec Messages",\
"../data/Metrics.dat" using 1:90 ti"Measured Network-Traffic of sent Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioAllNetworkTraffic.eps"
replot

reset

#ratio of the network-traffic for Overlay measured by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Network-Traffic for the Overlay [bytes/s]"
set title "Measured Network-Traffic for the Overlay\nvs.\nReal Network-Traffic for the Overlay"
set key right bottom

set terminal png giant
set output "../graphics/RatioOverlayNetworkTraffic.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:27 ti"Network-Traffic of rec Overlay-Messages",\
"../data/Metrics.dat" using 1:46 ti"Network-Traffic of sent Overlay-Messages",\
"../data/Metrics.dat" using 1:74 ti"Measured Network-Traffic of rec Overlay-Messages",\
"../data/Metrics.dat" using 1:93 ti"Measured Network-Traffic of sent Overlay-Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioOverlayNetworkTraffic.eps"
replot

reset

#ratio of the network-traffic for SkyNet measured by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Network-Traffic for SkyNet [bytes/s]"
set title "Measured Network-Traffic for SkyNet\nvs.\nReal Network-Traffic for SkyNet"
set key right bottom

set terminal png giant
set output "../graphics/RatioSkyNetNetworkTraffic.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:28 ti"Network-Traffic of rec SkyNet-Messages",\
"../data/Metrics.dat" using 1:47 ti"Network-Traffic of sent SkyNet-Messages",\
"../data/Metrics.dat" using 1:75 ti"Measured Network-Traffic of rec SkyNet-Messages",\
"../data/Metrics.dat" using 1:94 ti"Measured Network-Traffic of sent SkyNet-Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioSkyNetNetworkTraffic.eps"
replot

reset

#ratio of the Lookup-traffic for the Overlay measured by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Lookup-Traffic for the Overlay [bytes/s]"
set title "Measured Lookup-Traffic for the Overlay\nvs.\nReal Lookup-Traffic for the Overlay"
set key right bottom

set terminal png giant
set output "../graphics/RatioLookupNetworkTraffic.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:15 ti"Lookup-Traffic of rec Overlay-Messages",\
"../data/Metrics.dat" using 1:34 ti"Lookup-Traffic of sent Overlay-Messages",\
"../data/Metrics.dat" using 1:62 ti"Measured Lookup-Traffic of rec Overlay-Messages",\
"../data/Metrics.dat" using 1:81 ti"Measured Lookup-Traffic of sent Overlay-Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioLookupNetworkTraffic.eps"
replot

reset

#ratio of the PingPong-traffic for the Overlay measured by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "PingPong-Traffic for the Overlay [bytes/s]"
set title "Measured PingPong-Traffic for the Overlay\nvs.\nReal PingPong-Traffic for the Overlay"

set terminal png giant
set output "../graphics/RatioPingPongNetworkTraffic.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:20 ti"PingPong-Traffic of rec Overlay-Messages",\
"../data/Metrics.dat" using 1:39 ti"PingPong-Traffic of sent Overlay-Messages",\
"../data/Metrics.dat" using 1:67 ti"Measured PingPong-Traffic of rec Overlay-Messages",\
"../data/Metrics.dat" using 1:86 ti"Measured PingPong-Traffic of sent Overlay-Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioPingPongNetworkTraffic.eps"
replot

reset

#ratio of the network-traffic for MetricUpdates measured by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Network-Traffic for MetricUpdates [bytes/s]"
set title "Measured Network-Traffic for MetricUpdates\nvs.\nReal Network-Traffic for MetricUpdates"
set key right bottom

set terminal png giant
set output "../graphics/RatioMetricUpdateTraffic.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:26 ti"Network-Traffic of rec MetricUpdate-Messages",\
"../data/Metrics.dat" using 1:45 ti"Network-Traffic of sent MetricUpdate-Messages",\
"../data/Metrics.dat" using 1:73 ti"Measured Network-Traffic of rec MetricUpdate-Messages",\
"../data/Metrics.dat" using 1:92 ti"Measured Network-Traffic of sent MetricUpdate-Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioMetricUpdateTraffic.eps"
replot

reset

#ratio of the network-traffic for MetricUpdateACKMsgs measured by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Network-Traffic for MetricUpdateACKMsgs [bytes/s]"
set title "Measured Network-Traffic for MetricUpdateACKMsgs\nvs.\nReal Network-Traffic for MetricUpdateACKMsgs"
set key right bottom

set terminal png giant
set output "../graphics/RatioMetricUpdateACKTraffic.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:25 ti"Network-Traffic of rec MetricUpdateACK-Messages",\
"../data/Metrics.dat" using 1:44 ti"Network-Traffic of sent MetricUpdateACK-Messages",\
"../data/Metrics.dat" using 1:72 ti"Measured Network-Traffic of rec MetricUpdateACK-Messages",\
"../data/Metrics.dat" using 1:91 ti"Measured Network-Traffic of sent MetricUpdateACK-Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioMetricUpdateACKTraffic.eps"
replot

reset

#ratio of the network-traffic for AttributeUpdates measured by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Network-Traffic for AttributeUpdates [bytes/s]"
set title "Measured Network-Traffic for AttributeUpdates\nvs.\nReal Network-Traffic for AttributeUpdates"
set key right bottom

set terminal png giant
set output "../graphics/RatioAttributeUpdateTraffic.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:23 ti"Network-Traffic of rec AttributeUpdate-Messages",\
"../data/Metrics.dat" using 1:42 ti"Network-Traffic of sent AttributeUpdate-Messages",\
"../data/Metrics.dat" using 1:70 ti"Measured Network-Traffic of rec AttributeUpdate-Messages",\
"../data/Metrics.dat" using 1:89 ti"Measured Network-Traffic of sent AttributeUpdate-Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioAttributeUpdateTraffic.eps"
replot

reset

#---------------------------------------------------------
#plots of data concerning the query
#---------------------------------------------------------

#ratio of Query-Message-Exchange counted by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Number of Query-Messages [1/s]"
set title "Measured Query-Message-Exchange\nvs.\nReal Query-Message-Exchange"

set terminal png giant
set output "../graphics/RatioQueryMsgExchange.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:21 ti"Received Query-Messages",\
"../data/Metrics.dat" using 1:40 ti"Sent Query-Messages",\
"../data/Metrics.dat" using 1:68 ti"Measured Received Query-Messages",\
"../data/Metrics.dat" using 1:87 ti"Measured Sent Query-Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioQueryMsgExchange.eps"
replot

reset

#ratio of the Query-traffic measured by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "Query-Traffic [bytes/s]"
set title "Measured Query-Traffic\nvs.\nReal Query-Traffic"

set terminal png giant
set output "../graphics/RatioQueryNetworkTraffic.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:22 ti"Query-Traffic of rec Messages",\
"../data/Metrics.dat" using 1:41 ti"Query-Traffic of sent Messages",\
"../data/Metrics.dat" using 1:69 ti"Measured Query-Traffic of rec Messages",\
"../data/Metrics.dat" using 1:88 ti"Measured Query-Traffic of sent Messages"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioQueryNetworkTraffic.eps"
replot

reset

#ratio of the average Query-time measured by the root and compared to the overall-view
set xlabel "Simulation-Time [s]"
set ylabel "average Query-Time [s]"
set title "Measured Query-Time\nvs.\nReal Query-Time"

set terminal png giant
set output "../graphics/RatioQueryTime.png"
set style data linespoints
plot "../data/Metrics.dat" using 1:5 ti"Query-Time",\
"../data/Metrics.dat" using 1:52 ti"Measured Query-Time"

set terminal postscript eps "Helvetica" 24
set output "../graphics/RatioQueryTime.eps"
replot

reset

#---------------------------------------------------------
#Figure for visualizing the used Memory
#---------------------------------------------------------

#Figure, displaying the free, total and max memory
set xlabel "Simulation-Time [s]"
set ylabel "Memory[kb]"
set title "Memory Consumption of a Simulation"

set terminal png giant
set output "../graphics/MemoryConsumption.png"
set style data lines
plot "../data/Metrics.dat" using 1:97 ti"Free Memory",\
"../data/Metrics.dat" using 1:98 ti"Total Memory",\
"../data/Metrics.dat" using 1:99 ti"Max Memory",\
"../data/Metrics.dat" using 1:($98-$97) ti"Consumed Memory"

set terminal postscript eps "Helvetica" 24
set output "../graphics/MemoryConsumption.eps"
replot

reset