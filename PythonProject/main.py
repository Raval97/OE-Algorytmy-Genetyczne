from deap import base
from deap import creator
from deap import tools
import random


def individual(icls):
    genome = list()
    genome.append(random.uniform(-10, 10))
    genome.append(random.uniform(-10, 10))
    return icls(genome)


def fitnessFunction(individual):
    result = (individual[0] + 2 * individual[1] - 7) ** 2 + (2 * individual[0] + individual[1] - 5) ** 2
    return result



def print_hi(name):
    print(f'Hello, {name}')

if __name__ == '__main__':
    print_hi('PyCharm')


