# Genetic Algorithms

The project presents the operation of genetic algorithms on the example 
of McCornick functions using the Graphical Interface:

![Image description](img/1.png?raw=true)

The Graphical Interface was created using the Java language, and the JFrame package.

![Image description](img/2.png?raw=true)

As part of an activity, you can set values:
- range of variables x1 and x2
- computational accuracy (binary representation of the chromosome)
- number of epochs
- the percentage of the best for reproduction after the selection stage
- number of elite strategies
- crossing probability
- mutation probability
- the probability of inversion (binary representation of the chromosome)
- choice of chromosome representation
    - binary
    - real
- choice of selection method
    - selection of the best
    - roulette wheel selection
    - tournament selection
- selection of the crossing method
    - single point
    - two-point
    - three-point
    - homo
    - *arithmetic (real representation of the chromosome)
    - *heuristic (real representation of the chromosome)
- choice of mutation method
    - single point
    - two-point
    - boundary 
    - *uniform (real representation of the chromosome)
- type of optimization
    - minimalization
    - maximization

As a result, a folder is returned containing:
- results of particular epochs in the form of a .txt file
- graph of the standard deviation from successive eopok
- a graph of the average adaptation of the population from subsequent epochs

![Image description](img/3.png?raw=true)



