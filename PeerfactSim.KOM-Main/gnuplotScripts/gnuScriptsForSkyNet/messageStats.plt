reset

#################################################################
#Plotting for the dropped messages during a simulation
#################################################################

#plotting the amount of dropped messages
#set pointsize 1.3
set xlabel "Rank"
set ylabel "Number of dropped messages"
set title "Ranking of dropped Messages"

set terminal png giant
set output "../graphics/messages/droppedMessageAmount.png"
set style data linespoints
set logscale x
plot "../data/SortedMessagesDropped.dat" using 0:1 ti"all messages",\
"../data/SortedMessagesDropped.dat" using 0:2 ti"overlay-messages",\
"../data/SortedMessagesDropped.dat" using 0:3 ti"SkyNet-messages"
set terminal postscript eps "Helvetica" 24
set output "../graphics/messages/droppedMessageAmount.eps"
replot

reset

#plotting the size of dropped messages
#set pointsize 1.3
set xlabel "Rank"
set ylabel "Size of dropped messages"
set title "Ranking of the size of dropped Messages [bytes]"

set terminal png giant
set output "../graphics/messages/droppedSizeMessageAmount.png"
set style data linespoints
set logscale x
plot "../data/SortedSizeMessagesDropped.dat" using 0:1 ti"all messages",\
"../data/SortedSizeMessagesDropped.dat" using 0:2 ti"overlay-messages",\
"../data/SortedSizeMessagesDropped.dat" using 0:3 ti"SkyNet-messages"
set terminal postscript eps "Helvetica" 24
set output "../graphics/messages/droppedSizeMessageAmount.eps"
replot

reset

#plotting the amount of dropped metricUpdate-, metricUpdateACK-, attributeUpdate-messages
#set pointsize 1.3
set xlabel "Rank"
set ylabel "Number of dropped messages"
set title "Ranking of dropped Messages"

set terminal png giant
set output "../graphics/messages/droppedUpdateMessageAmount.png"
set style data linespoints
set logscale x
plot "../data/SortedMessagesDropped.dat" using 0:4 ti"metricUpdate-messages",\
"../data/SortedMessagesDropped.dat" using 0:5 ti"metricUpdateACK-messages",\
"../data/SortedMessagesDropped.dat" using 0:6 ti"attributeUpdate-messages"
set terminal postscript eps "Helvetica" 24
set output "../graphics/messages/droppedUpdateMessageAmount.eps"
replot

reset

#plotting the size of dropped metricUpdate-, metricUpdateACK-, attributeUpdate-messages
#set pointsize 1.3
set xlabel "Rank"
set ylabel "Size of dropped messages [bytes]"
set title "Ranking of the size of dropped Messages"

set terminal png giant
set output "../graphics/messages/droppedSizeUpdateMessageAmount.png"
set style data linespoints
set logscale x
plot "../data/SortedSizeMessagesDropped.dat" using 0:4 ti"metricUpdate-messages",\
"../data/SortedSizeMessagesDropped.dat" using 0:5 ti"metricUpdateACK-messages",\
"../data/SortedSizeMessagesDropped.dat" using 0:6 ti"attributeUpdate-messages"
set terminal postscript eps "Helvetica" 24
set output "../graphics/messages/droppedSizeUpdateMessageAmount.eps"
replot

reset

#################################################################
#Plotting for the sent messages during a simulation
#################################################################

#plotting the amount of sent messages
#set pointsize 1.3
set xlabel "Rank"
set ylabel "Number of sent messages"
set title "Ranking of sent Messages"

set terminal png giant
set output "../graphics/messages/sentMessageAmount.png"
set style data linespoints
set logscale x
plot "../data/SortedMessagesSent.dat" using 0:1 ti"all messages",\
"../data/SortedMessagesSent.dat" using 0:2 ti"overlay-messages",\
"../data/SortedMessagesSent.dat" using 0:3 ti"SkyNet-messages"
set terminal postscript eps "Helvetica" 24
set output "../graphics/messages/sentMessageAmount.eps"
replot

reset

#plotting the size of sent messages
#set pointsize 1.3
set xlabel "Rank"
set ylabel "Size of sent messages [bytes]"
set title "Ranking of the size of sent Messages"

set terminal png giant
set output "../graphics/messages/sentSizeMessageAmount.png"
set style data linespoints
set logscale x
plot "../data/SortedSizeMessagesSent.dat" using 0:1 ti"all messages",\
"../data/SortedSizeMessagesSent.dat" using 0:2 ti"overlay-messages",\
"../data/SortedSizeMessagesSent.dat" using 0:3 ti"SkyNet-messages"
set terminal postscript eps "Helvetica" 24
set output "../graphics/messages/sentSizeMessageAmount.eps"
replot

reset

#plotting the amount of sent metricUpdate-, metricUpdateACK-, attributeUpdate-messages
#set pointsize 1.3
set xlabel "Rank"
set ylabel "Number of sent messages"
set title "Ranking of sent Messages"

set terminal png giant
set output "../graphics/messages/sentUpdateMessageAmount.png"
set style data linespoints
set logscale x
plot "../data/SortedMessagesSent.dat" using 0:4 ti"metricUpdate-messages",\
"../data/SortedMessagesSent.dat" using 0:5 ti"metricUpdateACK-messages",\
"../data/SortedMessagesSent.dat" using 0:6 ti"attributeUpdate-messages"
set terminal postscript eps "Helvetica" 24
set output "../graphics/messages/sentUpdateMessageAmount.eps"
replot

reset

#plotting the size of sent metricUpdate-, metricUpdateACK-, attributeUpdate-messages
#set pointsize 1.3
set xlabel "Rank"
set ylabel "Size of sent messages [bytes]"
set title "Ranking of the size of sent Messages"

set terminal png giant
set output "../graphics/messages/sentSizeUpdateMessageAmount.png"
set style data linespoints
set logscale x
plot "../data/SortedSizeMessagesSent.dat" using 0:4 ti"metricUpdate-messages",\
"../data/SortedSizeMessagesSent.dat" using 0:5 ti"metricUpdateACK-messages",\
"../data/SortedSizeMessagesSent.dat" using 0:6 ti"attributeUpdate-messages"
set terminal postscript eps "Helvetica" 24
set output "../graphics/messages/sentSizeUpdateMessageAmount.eps"
replot

reset

#################################################################
#Plotting for the received messages during a simulation
#################################################################

#plotting the amount of received messages
#set pointsize 1.3
set xlabel "Rank"
set ylabel "Number of received messages"
set title "Ranking of received Messages"

set terminal png giant
set output "../graphics/messages/recMessageAmount.png"
set style data linespoints
set logscale x
plot "../data/SortedMessagesReceived.dat" using 0:1 ti"all messages",\
"../data/SortedMessagesReceived.dat" using 0:2 ti"overlay-messages",\
"../data/SortedMessagesReceived.dat" using 0:3 ti"SkyNet-messages"
set terminal postscript eps "Helvetica" 24
set output "../graphics/messages/recMessageAmount.eps"
replot

reset

#plotting the size of received messages
#set pointsize 1.3
set xlabel "Rank"
set ylabel "Size of received messages"
set title "Ranking of the size of received Messages"

set terminal png giant
set output "../graphics/messages/receivedSizeMessageAmount.png"
set style data linespoints
set logscale x
plot "../data/SortedSizeMessagesReceived.dat" using 0:1 ti"all messages",\
"../data/SortedSizeMessagesReceived.dat" using 0:2 ti"overlay-messages",\
"../data/SortedSizeMessagesReceived.dat" using 0:3 ti"SkyNet-messages"
set terminal postscript eps "Helvetica" 24
set output "../graphics/messages/receivedSizeMessageAmount.eps"
replot

reset

#plotting the amount of received metricUpdate-, metricUpdateACK-, attributeUpdate-messages
#set pointsize 1.3
set xlabel "Rank"
set ylabel "Number of received messages"
set title "Ranking of received Messages"

set terminal png giant
set output "../graphics/messages/receivedUpdateMessageAmount.png"
set style data linespoints
set logscale x
plot "../data/SortedMessagesReceived.dat" using 0:4 ti"metricUpdate-messages",\
"../data/SortedMessagesReceived.dat" using 0:5 ti"metricUpdateACK-messages",\
"../data/SortedMessagesReceived.dat" using 0:6 ti"attributeUpdate-messages"
set terminal postscript eps "Helvetica" 24
set output "../graphics/messages/receivedUpdateMessageAmount.eps"
replot

reset

#plotting the size of received metricUpdate-, metricUpdateACK-, attributeUpdate-messages
#set pointsize 1.3
set xlabel "Rank"
set ylabel "Size of received messages [bytes]"
set title "Ranking of the size of received Messages"

set terminal png giant
set output "../graphics/messages/receivedSizeUpdateMessageAmount.png"
set style data linespoints
set logscale x
plot "../data/SortedSizeMessagesReceived.dat" using 0:4 ti"metricUpdate-messages",\
"../data/SortedSizeMessagesReceived.dat" using 0:5 ti"metricUpdateACK-messages",\
"../data/SortedSizeMessagesReceived.dat" using 0:6 ti"attributeUpdate-messages"
set terminal postscript eps "Helvetica" 24
set output "../graphics/messages/receivedSizeUpdateMessageAmount.eps"
replot

reset