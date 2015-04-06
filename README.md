# QodBot
IRC dice roller bot.


This bot is intended as a simple way to roll dice in Internet Relay Chat channels, adding some often-missed functionality.
Here, you CAN see the individual results of your rolls.

Usage once compiled:
java QodBot <server> <channel1> <channel2> etc.

At least the server and one channel must be provided.

Syntax in-channel:
">roll (NdM|Ndf|advantage|disadvantage)([+-](NdM|Ndf)){0-8}([+-]M)?"

NdM: N M-sided dice.
Ndf: N Fudge/FATE dice (d3-2).
advantage: 2d20, pick the highest.
disadvantage: 2d20, pick the lowest.
+-M: final modifier.

Max number of dice in one roll (N in NdM) is 100.
Max number of different dice rolls: 9 plus final modifier

Too-large rolls might make the result bigger than the max message size in IRC.
Be less greedy if that happens.
