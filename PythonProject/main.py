from deap import base
from deap import algorithms
from deap import creator
from deap import tools
import multiprocessing
import random
import sys
import array
import numpy
import math
import copy



def individual(icls):
    genome = list()
    genome.append(random.uniform(-1.5, 4))
    genome.append(random.uniform(-3, 4))
    return icls(genome)


def fitnessFunction(individual):
    # result = (individual[0] + 2 * individual[1] - 7) ** 2 + (2 * individual[0] + individual[1] - 5) ** 2
    result = math.sin((individual[0] + individual[1])) + math.pow((individual[0] - individual[1]), 2) - 1.5 * individual[0] + 2.5 * individual[1] + 1;
    return result

#     private Osobnik krzyzowanieHeurystyczne(Osobnik rodzic1, Osobnik rodzic2) {
#         double k = random.nextDouble();
#         Chromosom x1 = new Chromosom(
#                 (k * (rodzic2.chromosomy.get(0).wartoscRzeczywista - rodzic1.chromosomy.get(0).wartoscRzeczywista) + rodzic1.chromosomy.get(0).wartoscRzeczywista),
#                 rodzic1.chromosomy.get(0).zakres);
#         Chromosom x2 = new Chromosom(
#                 (k * (rodzic2.chromosomy.get(1).wartoscRzeczywista - rodzic1.chromosomy.get(1).wartoscRzeczywista) + rodzic1.chromosomy.get(1).wartoscRzeczywista),
#                 rodzic1.chromosomy.get(1).zakres);
#         return new Osobnik(x1, x2);
#     }

def heuristic(val1, val2):
    k = random.uniform(0, 1)
    ind1 = k * (val2[0] - val1[0]) + val1[0]
    ind2 = k * (val2[1] - val1[1]) + val1[1]
    return ind1, ind2

# value = input("cos tam")

if __name__ == "__main__":
    krzyzowanie = "h"

    print("start")
    # creator.create("FitnessMin", base.Fitness, weights=(-1.0,))
    creator.create("FitnessMax", base.Fitness, weights=(1.0,))
    # creator.create("Individual", list, fitness=creator.FitnessMin)
    creator.create("Individual", list, fitness=creator.FitnessMax)
    toolbox = base.Toolbox()
    pool = multiprocessing.Pool(processes=4)
    toolbox.register("map", pool.map)
    toolbox.register('individual', individual, creator.Individual)
    toolbox.register("population", tools.initRepeat, list, toolbox.individual)
    toolbox.register("evaluate", fitnessFunction)
    toolbox.register("select", tools.selTournament, tournsize=3)  # inne mtody
    # toolbox.register("mate", tools.cxSimulatedBinary, eta=0.8)  # heurystyczna i ta druga
    if(krzyzowanie == "h"):
        toolbox.register("mate", heuristic)
    # toolbox.register("mate", heuristic)
    toolbox.register("mutate", tools.mutGaussian, mu=5, sigma=10, indpb=0.1)  # przetestowac inne
    sizePopulation = 100
    probabilityMutation = 0.2
    probabilityCrossover = 0.8
    numberIteration = 100
    pop = toolbox.population(n=sizePopulation)
    fitnesses = list(toolbox.map(toolbox.evaluate, pop))
    for ind, fit in zip(pop, fitnesses):
        ind.fitness.values = (fit,)

    numberElitism = 1
    g = 0
    while g < numberIteration:
        g = g + 1
        print("-- Generation %i --" % g)
        # Select the next generation individuals
        offspring = toolbox.select(pop, len(pop))
        # Clone the selected individuals
        offspring = list(map(toolbox.clone, offspring))
        listElitism = []
        for x in range(0, numberElitism):
            listElitism.append(tools.selBest(pop, 1)[0])

        # Apply crossover and mutation on the offspring
        # if(krzyzowanie == "h"):
        #     offspringCopy = copy.copy(offspring)
        #     offspring.clear()
        #     size = len(offspringCopy)
        #     while (len(offspring) < size):
        #         child1 = offspringCopy[random.randint(0, size/2-1)]
        #         child2 = offspringCopy[random.randint(size/2, size-1)]
        #         if((child1[0] > child2[0] and child1[1] > child2[1]) or
        #             child1[0] < child2[0] and child1[1] < child2[1]):
        #             indyviduals = toolbox.mate(child1, child2)
        #             child1 = indyviduals
        #             print(indyviduals)
        #             print("aa")
        #             print(child1)
        #
        #             offspring.append(child1)

        for child1, child2 in zip(offspring[::2], offspring[1::2]):
            # cross two individuals with probability CXPB
            if random.random() < probabilityCrossover:
                toolbox.mate(child1, child2)
                # print(type(toolbox.mate(child1, child2)))
                # print('aaa')
                # print(type(child1))
            # fitness values of the children
            # must be recalculated later
            del child1.fitness.values
            del child2.fitness.values

        for mutant in offspring:
            # mutate an individual with probability MUTPB
            if random.random() < probabilityMutation:
                toolbox.mutate(mutant)
                # toolbox.mutate(mutant,)
            del mutant.fitness.values

        # Evaluate the individuals with an invalid fitness
        invalid_ind = [ind for ind in offspring if not ind.fitness.valid]
        fitnesses = toolbox.map(toolbox.evaluate, invalid_ind)
        for ind, fit in zip(invalid_ind, fitnesses):
            ind.fitness.values = (fit,)
        print(" Evaluated %i individuals" % len(invalid_ind))
        pop[:] = offspring + listElitism
        # Gather all the fitnesses in one list and print the stats
        fits = [ind.fitness.values[0] for ind in pop]
        length = len(pop)
        mean = sum(fits) / length
        sum2 = sum(x * x for x in fits)
        std = abs(sum2 / length - mean ** 2) ** 0.5
        print(" Min %s" % min(fits))
        print(" Max %s" % max(fits))
        print(" Avg %s" % mean)
        print(" Std %s" % std)
        best_ind = tools.selBest(pop, 1)[0]
        print("Best individual is %s, %s" % (best_ind,
                                             best_ind.fitness.values))
    #
    print("-- End of (successful) evolution --")

    print('koniec')

