#!/usr/bin/env python3

import sys
import os
import collections
from statistics import mean

INPUT_DIRS = sys.argv[1:]

Result = collections.namedtuple('Result', ['metaepochs', 'levels', 'mutation_rate', 'population_size', 'time', 'fit'])

def parse_time(time_m_colon_s):
    words = time_m_colon_s.split(':')
    return float(words[0]) * 60.0 + float(words[1])

def read_best_fit(in_file):
    best_fit = -1
    with open(in_file) as f:
        for line in f.readlines():
            fit = float(line.split()[-1][:-1])
            best_fit = max(fit, best_fit)
    return best_fit

def group(iterable, n):
    return zip(*[iterable[i::n] for i in range(n)])

def read_results(dir_name):
    result = []
    with open(os.path.join(dir_name, 'times.txt')) as f:
        for info, times in group(f.readlines(), 2):
            info_words = info.strip().split()
            metaepochs = int(info_words[1])
            levels = int(info_words[3])
            mutation_rate = float(info_words[7][:-1])
            population_size = int(info_words[10])
            result_input_file = os.path.join(dir_name, '%s_metaepochs_%s_levels_%s_mut_rate_%s_population.aggregate1.txt' % (metaepochs, levels, mutation_rate, population_size))

            best_fit = read_best_fit(result_input_file)

            times_words = times.strip().split()
            time = parse_time(times_words[0])

            result.append(Result(metaepochs, levels, mutation_rate, population_size, time, best_fit))
    return result

all_data = collections.defaultdict(lambda: [])

for dir_name in INPUT_DIRS:
    for result in read_results(dir_name):
        all_data[result[:-2]].append(result)

print('metaepochs levels mutation_rate population_size time_min[s] time_avg[s] time_max[s] fit_min fit_avg fit_max efficiency')
for result in sorted(all_data.values()):
    print(' '.join(str(x) for x in (
        result[0].metaepochs,
        result[0].levels,
        result[0].mutation_rate,
        result[0].population_size,
        min(t.time for t in result),
        mean(t.time for t in result),
        max(t.time for t in result),
        min(t.fit for t in result),
        mean(t.fit for t in result),
        max(t.fit for t in result),
        (100000.0 - mean(t.fit for t in result)) / mean(t.time for t in result))))
