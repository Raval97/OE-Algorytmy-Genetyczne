import math
import multiprocessing
import random
import time
from itertools import repeat

import matplotlib.pyplot as plt
from deap import base
from deap import creator
from deap import tools

try:
    from collections.abc import Sequence
except ImportError:
    from collections import Sequence


def cxArithemtic(ind1, ind2):
    k = random.uniform(0, 1)
    new_ind1 = tuple(k * x for x in ind1) + tuple((1 - k) * x for x in ind2)
    new_ind2 = tuple((1 - k) * x for x in ind1) + tuple(k * x for x in ind2)
    return new_ind1, new_ind2


def cxHeuristic(ind1, ind2):
    k = random.uniform(0, 1)
    if ind2.fitness.values >= ind1.fitness.values:
        x1new = k * (ind2[0] - ind1[0]) + ind2[0]
        y1new = k * (ind2[1] - ind1[1]) + ind2[1]
        new_ind = (x1new, y1new)
    else:
        x1new = k * (ind1[0] - ind2[0]) + ind1[0]
        y1new = k * (ind1[1] - ind2[1]) + ind1[1]
        new_ind = (x1new, y1new)
    return new_ind


def mutUniformIntOurSelf(individual, low, up, indpb):
    size = len(individual)
    if not isinstance(low, Sequence):
        low = repeat(low, size)
    elif len(low) < size:
        raise IndexError("low must be at least the size of individual: %d < %d" % (len(low), size))
    if not isinstance(up, Sequence):
        up = repeat(up, size)
    elif len(up) < size:
        raise IndexError("up must be at least the size of individual: %d < %d" % (len(up), size))
    for i, xl, xu in zip(range(size), low, up):
        if random.random() < indpb:
            individual[i] = random.uniform(xl, xu)
    return individual,


def set_type_of_find_element(type):
    if type == 1:
        creator.create("FitnessMin", base.Fitness, weights=(-1.0,))
        creator.create("Individual", list, fitness=creator.FitnessMin)
    elif type == 2:
        creator.create("FitnessMax", base.Fitness, weights=(1.0,))
        creator.create("Individual", list, fitness=creator.FitnessMax)


def set_selection_method(type, tournsize):
    if type == 1:
        toolbox.register("select", tools.selTournament, tournsize=tournsize)
    elif type == 2:
        toolbox.register("select", tools.selRandom)
    elif type == 3:
        toolbox.register("select", tools.selBest)
    elif type == 4:
        toolbox.register("select", tools.selWorst)
    elif type == 5:
        toolbox.register("select", tools.selRoulette)


def set_crossing_method(type):
    if type == 1:
        toolbox.register("mate", cxArithemtic)
    if type == 2:
        toolbox.register("mate", cxHeuristic)
    if type == 3:
        toolbox.register("mate", tools.cxOnePoint)
    if type == 4:
        toolbox.register("mate", tools.cxUniform, indpb=1)


def set_mutation_method(type, probability_mutation):
    if type == 1:
        toolbox.register("mutate", mutUniformIntOurSelf, low=[-1.5, -3], up=4, indpb=probability_mutation)
    if type == 2:
        toolbox.register("mutate", tools.mutGaussian, mu=0, sigma=0.2, indpb=probability_mutation)
    if type == 3:
        toolbox.register("mutate", tools.mutPolynomialBounded, eta=0.5, low=[-1.5, -3], up=4, indpb=probability_mutation)
    # if muttype == 4:
    #     toolbox.register("mutate", tools.mutShuffleIndexes, indpb=probability_mutation)


def fitness_function(individual):
    result = math.sin((individual[0] + individual[1])) + math.pow((individual[0] - individual[1]), 2) - 1.5 * individual[0] + 2.5 * individual[1] + 1;
    return result,


def individual(icls):
    genome = list()
    genome.append(random.uniform(-1.5, 4))
    genome.append(random.uniform(-3, 4))
    return icls(genome)


def main_ui():
    print("##############################################")
    print("############  Genetic Algorithm  #############")
    print("#####  Optimization McCornick Functions  #####")
    print("##############################################\n")
    print("Select type of optimization: ")
    print("1. Minimization")
    print("2. Maximization")
    optimalization_type = int(input())
    print("Select method of selection: ")
    print("1. Tournament")
    print("2. Random")
    print("3. Best")
    print("4. Worst")
    print("5. Roulette")
    select_type = int(input())
    print("Select method of crossing: ")
    print("1. Arithmetic")
    print("2. Heuristic")
    print("3. One Point")
    print("4. Uniform")
    cx_type = int(input())
    print("Select method of mutation: ")
    print("1. Uniform Int")
    print("2. Gaussian")
    print("3. Polynomial bounded")
    # print("4. Shuffle indexes")
    mut_type = int(input())
    return select_type, optimalization_type, cx_type, mut_type


def set_algorithm(select_type, find_element_type, cx_type, mut_type, probability_mutation):
    set_type_of_find_element(find_element_type)
    toolbox.register('individual', individual, creator.Individual)
    toolbox.register("population", tools.initRepeat, list, toolbox.individual)
    toolbox.register("evaluate", fitness_function)
    set_selection_method(select_type, 4)
    set_crossing_method(cx_type)
    set_mutation_method(mut_type, probability_mutation)
    pop = toolbox.population(n=population_size)
    fitnesses = list(toolbox.map(toolbox.evaluate, pop))
    for ind, fit in zip(pop, fitnesses):
        ind.fitness.values = fit
    return pop


def generate_plot(value, yLabel, title):
    plt.xlabel("epoch", fontsize=10)
    plt.ylabel(yLabel, fontsize=10)
    plt.title(title, fontsize=10)
    plt.plot(value)
    plt.tight_layout()
    plt.show()


def example_plot(ax, value, ylabel, title):
    ax.plot(value, linewidth=1)
    ax.set_xlabel('epochs', fontsize=6)
    ax.set_ylabel(ylabel, fontsize=6)
    ax.set_title(title, fontsize=6, fontweight='bold')


def generate_plots(best, mean, std):
    plt.figure(figsize=(15, 10), dpi=100)
    plt.rcParams.update({'font.size': 4})
    ax1 = plt.subplot(211)
    ax2 = plt.subplot(223)
    ax3 = plt.subplot(224)
    # fig = plt.gcf()
    # fig.set_size_inches(20, 20, forward=True)
    example_plot(ax1, best, "fitness", "Best function fitness")
    example_plot(ax2, mean, "population fitness", "Mean fitness of population")
    example_plot(ax3, std, "Std value", "Standard deviation")
    # plt.subplots_adjust(top=0.9)
    plt.show()


def run_algorithm(pop):
    g = 0
    number_elitism = 2
    best_list = []
    mean_list = []
    std_list = []
    start_time = time.time()
    while g < epoch:
        g = g + 1
        print("-- Generation %i --" % g)

        list_elitism = []
        for x in range(0, number_elitism):
                best = tools.selBest(pop, 1)[0]
                list_elitism.append(best)
                pop.remove(best)

        # Select the next generation individuals
        selected = toolbox.select(pop, (len(pop) // 3))
        # Clone the selected individuals
        # offspring = list(map(toolbox.clone, offspring))
        offspring = []
        iter = 0
        while (len(offspring) < len(pop)):
            offspring.append(toolbox.clone(selected[iter]))
            iter += 1
            if(iter == len(selected)):
                iter = 0
        pop[:] = offspring

        # Apply crossover and mutation on the offspring
        for child1, child2 in zip(offspring[::2], offspring[1::2]):

            # cross two individuals with probability CXPB
            if random.random() < crossover_probability:
                toolbox.mate(child1, child2)
                # fitness values of the children
                # must be recalculated later
                del child1.fitness.values
                del child2.fitness.values

        for mutant in offspring:
            # mutate an individual with probability MUTPB
            # if random.random() < probability_mutation:
            toolbox.mutate(mutant)
            del mutant.fitness.values

            # Evaluate the individuals with an invalid fitness
        invalid_ind = [ind for ind in offspring if not ind.fitness.valid]
        fitnesses = toolbox.map(toolbox.evaluate, invalid_ind)
        for ind, fit in zip(invalid_ind, fitnesses):
            ind.fitness.values = fit

        print("  Evaluated %i individuals" % len(invalid_ind))
        pop[:] = offspring + list_elitism

        # Gather all the fitnesses in one list and print the stats
        fits = [ind.fitness.values[0] for ind in pop]

        length = len(pop)
        mean = sum(fits) / length
        sum2 = sum(x * x for x in fits)
        std = abs(sum2 / length - mean ** 2) ** 0.5

        print("  Min %s" % min(fits))
        print("  Max %s" % max(fits))
        print("  Avg %s" % mean)
        print("  Std %s" % std)
        best_ind = tools.selBest(pop, 1)[0]
        print("Best individual is %s, %s" % (best_ind, best_ind.fitness.values))
        mean_list.append(mean)
        std_list.append(std)
        best_list.append(best_ind.fitness.values)
        #
    print("-- End of (successful) evolution --")
    print("\nAlgorithm Duration: ", time.time() - start_time)
    generate_plots(best_list, mean_list, std_list)
    # generate_plot(best_list, "fitness", "Best function fitness")
    # generate_plot(mean_list, "population fitness", "Mean fitness of population")
    # generate_plot(std_list, "MSD", "Mean standard deviation")


if __name__ == '__main__':
    epoch = 100
    population_size = 100
    mutation_probability = 0.2
    crossover_probability = 0.8
    toolbox = base.Toolbox()
    select, find_element, cross, mut = main_ui()
    pop = set_algorithm(select, find_element, cross, mut, mutation_probability)
    pool = multiprocessing.Pool(processes=4)
    toolbox.register("map", pool.map)
    run_algorithm(pop)
    pool.close()

# 4: 25.33812713623047
# 2: 24.50377869606018
# 1: 25.72955870628357