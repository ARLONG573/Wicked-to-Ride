# Wicked-to-Ride
MCTS implementation for a bot that can play Ticket to Ride.

The game components shall be defined in a JSON file as follows:

- Number of cars per player

- Deck distribution
	- must use the following color names only:
		- RED
		- ORANGE
		- YELLOW
		- GREEN
		- BLUE
		- PINK
		- WHITE
		- BLACK
		- WILD

- Destination tickets
	- 2 connected cities (using the predefined city names)
	- point value

- List of connections (using the predefined color names, plus the GRAY color)

- Points values of awards at the end of the game
	- LONGEST ROUTE
	- GLOBETROTTER
