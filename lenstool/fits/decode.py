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
    while True:
        chunk = f.read(CHUNK_SIZE)
        if len(chunk) < CHUNK_SIZE:
            if len(chunk) != 0 and chunk != '\n':
                print('ignored %d stray bytes (%s) at end of input' % (len(chunk), chunk), file=sys.stderr)
            break

        print('\t'.join(str(x) for x in struct.unpack(FORMAT, chunk)))
