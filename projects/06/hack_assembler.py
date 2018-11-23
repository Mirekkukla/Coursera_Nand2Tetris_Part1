import argparse

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('filename', help='name of input .asm file')
    args = parser.parse_args()
    filename = args.filename

    lines = load_file(filename)
    clean_lines = cleanup_lines(lines)

    symbol_table = CONSTANTS
    label_free_lines = extract_labels(clean_lines, symbol_table)

    print label_free_lines
    print symbol_table

    # spit out "xxx.hack" file

def extract_labels(lines, symbol_table):
    """
    Scan through lines, extracting the line numbers corresponding to labels
    Return a list of lines that DON'T iclude the 'label' lines
    """
    n = 0 # line number of the current line (note that label lines don't get a 'line number')

    non_label_lines = []
    for line in lines:
        if len(line) < 2: # i don't think 1 char lines are legal but lets be safe
            non_label_lines.append(line)
            n += 1
            continue

        # for label declarations lines, store the label in our table and toss the line
        if line[0] == "(" and line[-1] == ")":
            label_name = line[1:-1]
            if label_name in symbol_table:
                print "ERROR: dublicate label {} in table".format(label_name)

            # since label lines don't get a line number, we're effectively storing the
            # 'line number' of the _following_ line
            symbol_table[label_name] = n
        else:
            non_label_lines.append(line)
            n += 1

    return non_label_lines

def load_file(filename):
    """ Load file with given ".asm" filename, return list of lines"""
    if not filename.split('.')[-1] == 'asm':
        print 'filename has to end in ".asm", you gave "{}"'.format(filename)
        exit(0)

    print "Loading file '{}'".format(filename)
    lines = []
    with open(filename) as f:
        for line in f:
            lines.append(line.strip('\r\n'))

    return lines

def cleanup_lines(lines):
    """ Trim all whitespace the given lines (removing lines that are only whitespace) """
    clean_lines = []
    for line in lines:
        line_without_comment = line.split('//')[0]
        clean_line = line_without_comment.strip()
        if not clean_line:
            continue
        clean_lines.append(clean_line)
    return clean_lines


def convert_a_instruction(instruction):
    """ instruction should be in the form "@abc" where abc is a digit """
    print "yo"


def convert_c_instruction(instruction):
    """ instruction should be in the form "@abc" where abc is a digit """

    # strip ALL whitespace

    # initialize string with 3 1s
    print "yo"


CONSTANTS = {
    'SP': '0',
    'LCL': '1',
    'ARG': '2',
    'THIS': '3',
    'THAT': '4',
    'R0': '0',
    'R1': '1',
    'R2': '2',
    'R3': '3',
    'R4': '4',
    'R5': '5',
    'R6': '6',
    'R7': '7',
    'R8': '8',
    'R9': '9',
    'R10': '10',
    'R11': '11',
    'R12': '12',
    'R13': '13',
    'R14': '14',
    'R15': '15',
    'SCREEN': '16384',
    'KBD': '24576'
}

if __name__ == "__main__":
    main()
