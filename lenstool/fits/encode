#!/usr/bin/env python

from __future__ import print_function
import sys
import struct

if len(sys.argv) < 2:
    print('usage: %s format [ infile ]' % sys.argv[0], file=sys.stderr)

    relevant_paragraphs = struct.__doc__.split('\n\n')[1:-1]
    indented_paragraphs = [ '    ' + '\n    '.join(line for line in para.split('\n'))
                            for para in relevant_paragraphs ]
    print('\nFormat:\n%s\n' % '\n\n'.join(indented_paragraphs))
    sys.exit(1)

IN_FILE = '/dev/stdin'
if len(sys.argv) > 2 and sys.argv[2] != '-':
    IN_FILE = sys.argv[2]

FORMAT = sys.argv[1]
CHUNK_SIZE = struct.calcsize(FORMAT)

with open(IN_FILE) as f:
    for line in f.xreadlines():
        vals = [ eval(x) if x != 'nan' else float('nan') for x in line.strip().split() ]
        sys.stdout.write(struct.pack(FORMAT, *vals))
