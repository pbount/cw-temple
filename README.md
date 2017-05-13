# cw-temple
The solution to the coursework is located in two seperate files. The Explorer and the toolbox. The Explorer class makes decisions like direction and movement, when and if to pick up gold etc, while the toolbox provides the information to help with those decisions like which tile has the most gold, which tile is the closest, how many steps till the exit etc.

## Exploration phase
The exploration logic is very simplistic. As the explorer moves around, he leaves a breadcrum on a tile. The explorer selects the next tile to move to, by two criteria. The ammount of breadcrums on the tile (how many times was the tile already visited) and the distance to the orb. The explorer always selects the tile with the least breadcrums, and then the tile which is closest to the orb.

## Escape Phase
All neighbouring nodes are of equal distance from each other (the explorer can reach any adjusent node in a single step). In order for the explorer to find a shortest path from a node to another node the rules below must be true:

### Shortest Path
- There is always at least one path from any transversible node, to any other transversible node.
- The explorer can reach a transversible node from any transversible node in a finite number of steps within the remaining time.

Since this is always the case, every tile can be categorised by how many steps it takes to reach it. Then those tiles can be grouped and treated as transversible layers expanding outwards from the initial tile until the destination is found. For example:

![Image of Yaktocat](https://image.ibb.co/k5pWd5/concept2.png)

In image above, we can assume that the initial tile is the explorers current location, and the destination is the red tile. It is evident that there are 4 tiles to which the explorer can go to. These 4 tiles can be conidered the first layer of possible steps and are marked with number 1. All the neighbours of all tiles of the first layer, excluding those which are shared and those which are in a previous layer, can be considered the next layer marked with number 2 and so on. We can see that going from the initial tile to the destination tile, by layer alone, is not easy (for example two of the possible paths for reaching layer 4 for end up in a dead end). However going backwords from the destination to the source is trivial. For example the destination tile 25 will always have at least one tile 24, which will always have at least one tile 23 and so on, leading always to the first tile. Even though there may be multiple paths (for example going to the tile 17 from 21 may include 5 different paths) all those paths will have the same number of steps, and can thus be equaly considered as the shortest path.

### Gold collection
During the escape phase the explorer has a certain ammount of time during which he can collect some gold. Picking up gold reduces the available time. A simple approach to get an acceptable ammount of gold, is the greedy collection. Simply find the nearest tile containing any ammount of gold, move to it and pick it up. then estimate the time it will take to go to the exit by following a path having the nearest tile with gold. If the time is not sufficient then move to the exit without picking anything up (as it might compromise the remaining ammount of time). The average gold collection is between 13K-15K.