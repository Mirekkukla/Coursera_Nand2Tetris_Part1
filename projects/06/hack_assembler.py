import argparse

JUMP_MAP = {
    'JGT': '001',
    'JEQ': '010',
    'JGE': '011',
    'JLT': '100',
    'JNE': '101',
    'JLE': '110',
    'JMP': '111'
}

DEST_MAP = {
    'M': '001',
    'D': '010',
    'MD': '011',
    'A': '100',
    'AM': '101',
    'AD': '110',
    'AMD': '111'
}

COMP_MAP_WITH_A = {
    '0': '101010',
    '1': '111111',
    '-1': '111010',
    'D': '001100',
    'A': '110000',
    '!D': '001101',
    '!A': '11000M',
    '-D': '001111',
    '-A': '110011',
    'D+1': '011111',
    'A+1': '110111',
    'D-1': '001110',
    'A-1': '110010',
    'D+A': '000010',
    'D-A': '010011',
    'A-D': '000111',
    'D&A': '000000',
    'D|A': '010101'
}

COMP_MAP_WITH_M = {
    'M': '110000',
    '!M': '11000M',
    '-M': '110011',
    'M+1': '110111',
    'M-1': '110010',
    'D+M': '000010',
    'D-M': '010011',
    'M-D': '000111',
    'D&M': '000000',
    'D|M': '010101'
}

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
        binary_instruction = None
        if line[0] == '@':
            if line[1:].isdigit():
                binary_instruction = convert_a_instruction(line)
            else:
                binary_instruction = convert_symbol(line, symbol_table, next_address_to_allocate)
                next_address_to_allocate += 1
        else:
            binary_instruction = convert_c_instruction(line, symbol_table)

        if len(binary_instruction) != 16:
            raise Exception("Binary instruction needs to have 16 bits '{}'".format(binary_instruction))
        converted_lines.append(binary_instruction)

    output_filename = filename[:-4] + ".hack" # change extension from .asm to .hack
    export_to_file(output_filename, converted_lines)
    print "ALL DONE"

# not a contant since we're going to be appending to it
def get_initial_symbol_table():
    return {
        'SP': 0,
        'LCL': 1,
        'ARG': 2,
        'THIS': 3,
        'THAT': 4,
        'R0': 0,
        'R1': 1,
        'R2': 2,
        'R3': 3,
        'R4': 4,
        'R5': 5,
        'R6': 6,
        'R7': 7,
        'R8': 8,
        'R9': 9,
        'R10': 10,
        'R11': 11,
        'R12': 12,
        'R13': 13,
        'R14': 14,
        'R15': 15,
        'SCREEN': 16384,
        'KBD': 24576
    }


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
                raise Exception("dublicate label {} in table".format(label_name))

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

    return get_padded_bin_string(int(number_str))


def convert_symbol(line, symbol_table, next_address_to_allocate):
    """
    Convert the given symbol line into a-instruction using the given symbol table
    EX: if input line = '@SCREEN', we'll return '0100000000000000'
    """

    symbol = line[1:]
    if line[0] != '@' or symbol.isdigit():
        raise Exception("Symbol instruction is in the wrong format '{}'".format(line))

    if not isinstance(next_address_to_allocate, int):
        raise Exception("Addesses must be given as ints '{}'".format(next_address_to_allocate))

    symbol_int_value = None
    if symbol in symbol_table:
        symbol_int_value = symbol_table[symbol]
    else:
        symbol_table[symbol] = next_address_to_allocate
        symbol_int_value = next_address_to_allocate

    return get_padded_bin_string(symbol_int_value)


def convert_c_instruction(line, symbol_table):
    """
    Convert the given c-instruction into its HACK machine language representation
    Return the resulting string of 1s and 0s (of length 16)
    EX:  XXXXXXXXXXXXXXXXXX
    """
    jump_bits = get_jump_bits(line)
    dest_bits = get_dest_bits(line)
    comp_bits = get_comp_bits(line)
    return "111" + comp_bits + dest_bits + jump_bits


def get_jump_bits(line):
    """
    Takes a c-instruction line (looks like 'dest=comp;jump')
    Returns a 3 character string consisting of the jump bits
    """
    split_for_jump = line.split(';')
    if len(split_for_jump) != 2:
        return "000"
    jump_condition = split_for_jump[1]
    return JUMP_MAP[jump_condition]


def get_dest_bits(line):
    """
    Takes a c-instruction line (looks like 'dest=comp;jump')
    Returns a 3 character string consisting of the dest bits
    """
    split_for_dest = line.split('=')
    if len(split_for_dest) != 2:
        return "000"
    dest_condition = split_for_dest[0]
    return DEST_MAP[dest_condition]


def get_comp_bits(line):
    """
    Takes a c-instruction line (looks like 'dest=comp;jump')
    Returns the 6 character string consisting of the comp bits
    """
    line_without_jump_command = line.split(';')[0] # strip the optional ';jump' suffix
    comp_command = line_without_jump_command.split('=')[-1] # strip the optional 'dest=' prefix
    if 'M' in comp_command:
        return '1' + COMP_MAP_WITH_M[comp_command]
    return '0' + COMP_MAP_WITH_A[comp_command]


def get_padded_bin_string(int_value):
    """ EX: if int_value = 8, we'll return "0000000000001000" """
    if not isinstance(int_value, int):
        raise Exception("Can only convert ints to binary: {}".format(int_value))

    unpadded_bin_string = bin(int_value)[2:] # if number_str = "8", bin_string will be '1000'
    if len(unpadded_bin_string) > 15:
        raise Exception("ERROR: can't have numbers with > 15 bits '{}' -> '{}'".format(
            int_value, unpadded_bin_string))
    return unpadded_bin_string.zfill(16) # pad left with zeroes


def export_to_file(filename, converted_lines):
    """ Write the given list of string out the given filename, one string per line """
    print "Writing to {}".format(filename)
    with open(filename, 'w') as f:
        for line in converted_lines:
            f.write(line + '\n')


if __name__ == "__main__":
    main()
