import argparse

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("filename", help="name of input .asm file")
    args = parser.parse_args()
    filename = args.filename

    lines = load_file(filename)
    clean_lines = cleanup_lines(lines)

    symbol_table = get_initial_symbol_table()
    label_free_lines = extract_labels(clean_lines, symbol_table)

    converted_lines = []
    next_address_to_allocate = 16
    for line in label_free_lines:
        if line[0] == '@':
            if line[1:].isdigit():
                binary_a_instruction = convert_a_instruction(line)
                converted_lines.append(binary_a_instruction)
            else:
                binary_symbol_instruction = convert_symbol(line, symbol_table, str(next_address_to_allocate))
                converted_lines.append(binary_symbol_instruction)
                next_address_to_allocate += 1
        else:
            convert_c_instruction(line, symbol_table, str(next_address_to_allocate))

    print converted_lines

    # spit out "xxx.hack" file

def extract_labels(lines, symbol_table):
    """
    Scan through lines, extracting the line numbers corresponding to labels
    Return a list of lines that DON'T iclude the 'label' lines
    """
    n = 0 # line number of the current line (note that label lines don't get a 'line number')

    non_label_lines = []
    for line in lines:
        # for label declarations lines, store the label in our table and toss the line
        if line[0] == '(' and line[-1] == ')':
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
    if not filename.split('.')[-1] == "asm":
        raise Exception("filename has to end in '.asm', you gave '{}'".format(filename))

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


def convert_a_instruction(line):
    """
    Convert the given a-instruction into its HACK machine language representation
    Instruction should start with a "@" followed exclusively by digits
    Return the resulting string of 1s and 0s (of length 16)
    EX: if input line = '@8', we'll return '0000000000001000'
    """
    number_str = line[1:]
    if line[0] != '@' or not number_str.isdigit():
        raise Exception("ERROR: a-instruction is in the wrong format '{}'".format(line))

    return get_padded_bin_string(number_str)

def convert_symbol(line, symbol_table, next_address_to_allocate_str):
    """
    Convert the given symbol line into a-instruction using the given symbol table
    EX: if input line = '@SCREEN', we'll return '0100000000000000'
    """

    symbol = line[1:]
    if line[0] != '@' or symbol.isdigit():
        raise Exception("Symbol instruction is in the wrong format '{}'".format(line))

    if not isinstance(next_address_to_allocate_str, str):
        raise Exception("Addesses must be given as strings '{}'".format(next_address_to_allocate_str))

    symbol_int_value = None
    if symbol in symbol_table:
        symbol_int_value = symbol_table[symbol]
    else:
        symbol_table[symbol] = next_address_to_allocate_str # the able stores strings!
        symbol_int_value = next_address_to_allocate_str

    return get_padded_bin_string(symbol_int_value)


def convert_c_instruction(line, symbol_table, next_address_to_allocate_srt):
    """
    Convert the given c-instruction into its HACK machine language representation
    Return the resulting string of 1s and 0s (of length 16)
    EX:
    """

    # strip ALL whitespace

    # initialize string with 3 1s
    print "yo"

def get_padded_bin_string(int_str):
    """ EX: if number_str = "8", we'll return "0000000000001000" """
    if not isinstance(int_str, str) or not int_str.isdigit():
        raise Exception("Can only convert int strings to binary: {}".format(int_str))

    unpadded_bin_string = bin(int(int_str))[2:] # if number_str = "8", bin_string will be '1000'
    if len(unpadded_bin_string) > 15:
        raise Exception("ERROR: can't have numbers with > 15 bits '{}' -> '{}'".format(int_str, unpadded_bin_string))
    return unpadded_bin_string.zfill(16) # pad left with zeroes


def get_initial_symbol_table():
    return {
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
