package src.solutions;

import java.util.ArrayList;
import java.util.List;

public class IntCodeComputer {
    private static final String INTO_POSITION = " into position ";
    private static final String COMPARING = "Comparing ";
    public int[] memory;
    public int instructionPointer = 0;

    public boolean diagnosticMode = false;

    List<Integer> inputs = new ArrayList<>();
    List<Integer> outputs = new ArrayList<>();
    int inputIndex = 0;

    public IntCodeComputer(String line){
        this(line, new ArrayList<>());
    }

    public IntCodeComputer(String line, List<Integer> inputs){
        String[] memoryString = line.split(",");
        memory = new int[memoryString.length];
        for(int i = 0; i < memoryString.length; i++){
            memory[i] = Integer.parseInt(memoryString[i]);
        }
        this.inputs.addAll(inputs);
    }

    public void run() {
        while (instructionPointer < memory.length) {
            int opcodeValue = memory[instructionPointer];
            int opcode = opcodeValue % 100;
            opcodeValue /= 100;

            List<Integer> parameterModes = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                parameterModes.add(opcodeValue % 10);
                opcodeValue /= 10;
            }

            int pointerIncrement = 0;

            switch (opcode) {
                case 1:
                    pointerIncrement = add(parameterModes);
                    break;
                case 2:
                    pointerIncrement = multiply(parameterModes);
                    break;
                case 3:
                    pointerIncrement = input();
                    break;
                case 4:
                    pointerIncrement = output(parameterModes);
                    break;
                case 5:
                    pointerIncrement = jumpIfTrue(parameterModes);
                    break;
                case 6:
                    pointerIncrement = jumpIfFalse(parameterModes);
                    break;
                case 7:
                    pointerIncrement = lessThan(parameterModes);
                    break;
                case 8:
                    pointerIncrement = equals(parameterModes);
                    break;
                case 99:
                    return;
                default:
                    throw new IllegalStateException("Unexpected opcode: " + opcode);
            }

            instructionPointer += pointerIncrement;
        }
    }

    private int grabValue(int input, int parameterMode){
        if(parameterMode == 0){
            return memory[input];
        }
        if(parameterMode == 1){
            return input;
        }
        return -1;
    }

    private int add(List<Integer> paramModes){
        int inputPosition1 = memory[instructionPointer + 1];
        int inputPosition2 = memory[instructionPointer + 2];
        int outputPosition = memory[instructionPointer + 3];
        if(diagnosticMode){
            System.out.println("Putting " + grabValue(inputPosition1, paramModes.get(0))  + " + " +  grabValue(inputPosition2, paramModes.get(1)) + INTO_POSITION + outputPosition);
        }
        memory[outputPosition] = grabValue(inputPosition1, paramModes.get(0)) + grabValue(inputPosition2, paramModes.get(1));
        return 4;
    }

    private int multiply(List<Integer> paramModes){
        int inputPosition1 = memory[instructionPointer + 1];
        int inputPosition2 = memory[instructionPointer + 2];
        int outputPosition = memory[instructionPointer + 3];
        if(diagnosticMode){
            System.out.println("Putting " + grabValue(inputPosition1, paramModes.get(0))  + " * " +  grabValue(inputPosition2, paramModes.get(1)) + INTO_POSITION + outputPosition);
        }
        memory[outputPosition] = grabValue(inputPosition1, paramModes.get(0)) * grabValue(inputPosition2, paramModes.get(1));
        return 4;
    }

    private int input(){
        int location = memory[instructionPointer + 1];
        memory[location] = inputs.get(inputIndex);
        if(diagnosticMode) {
            System.out.println("Putting  " + inputs.get(inputIndex) + INTO_POSITION + location);
        }
        inputIndex++;
        return 2;
    }

    private int output(List<Integer> paramModes){
        int location = memory[instructionPointer + 1];
        outputs.add(grabValue(location, paramModes.get(0)));
        if(diagnosticMode){
            System.out.println("Outputting " + grabValue(location, paramModes.get(0)));
        }
        return 2;
    }

    private int jumpIfTrue(List<Integer> paramModes){
        int input = memory[instructionPointer + 1];
        int resolvedInput = grabValue(input, paramModes.get(0));
        int output = memory[instructionPointer + 2];
        int resolvedOutput = grabValue(output, paramModes.get(1));
        if(diagnosticMode){
            System.out.println(COMPARING + resolvedInput + " to 0");
        }
        if(resolvedInput != 0){
            instructionPointer = resolvedOutput;
            return 0;
        }
        else{
            return 3;
        }
    }

    private int jumpIfFalse(List<Integer> paramModes){
        int input = memory[instructionPointer + 1];
        int resolvedInput = grabValue(input, paramModes.get(0));
        int output = memory[instructionPointer + 2];
        int resolvedOutput = grabValue(output, paramModes.get(1));
        if(diagnosticMode){
            System.out.println(COMPARING + resolvedInput + " to 0");
        }
        if(resolvedInput == 0){
            instructionPointer = resolvedOutput;
            return 0;
        }
        else{
            return 3;
        }
    }

    private int lessThan(List<Integer> paramModes){
        int input1 = memory[instructionPointer + 1];
        int resolvedInput1 = grabValue(input1, paramModes.get(0));
        int input2 = memory[instructionPointer + 2];
        int resolvedInput2 = grabValue(input2, paramModes.get(1));
        int output = memory[instructionPointer + 3];
        if(diagnosticMode){
            System.out.println(COMPARING + resolvedInput1 + " to " + resolvedInput2);
        }
        if(resolvedInput1 < resolvedInput2){
            memory[output] = 1;
        }
        else{
            memory[output] = 0;
        }
        return 4;
    }

    private int equals(List<Integer> paramModes){
        int input1 = memory[instructionPointer + 1];
        int resolvedInput1 = grabValue(input1, paramModes.get(0));
        int input2 = memory[instructionPointer + 2];
        int resolvedInput2 = grabValue(input2, paramModes.get(1));
        int output = memory[instructionPointer + 3];
        if(diagnosticMode){
            System.out.println(COMPARING + resolvedInput1 + " to " + resolvedInput2);
        }
        if(resolvedInput1 == resolvedInput2){
            memory[output] = 1;
        }
        else{
            memory[output] = 0;
        }
        return 4;
    }

}

//docs
//Opcode 1 adds together numbers read from two positions and stores the result in a third position. The three integers immediately after the opcode tell you these three positions - the first two indicate the positions from which you should read the input values, and the third indicates the position at which the output should be stored.
//Opcode 2 works exactly like opcode 1, except it multiplies the two inputs instead of adding them. Again, the three integers after the opcode indicate where the inputs and outputs are, not their values.
//Opcode 3 takes a single integer as input and saves it to the position given by its only parameter. For example, the instruction 3,50 would take an input value and store it at address 50.
//Opcode 4 outputs the value of its only parameter. For example, the instruction 4,50 would output the value at address 50.
//Opcode 5 is jump-if-true: if the first parameter is non-zero, it sets the instruction pointer to the value from the second parameter. Otherwise, it does nothing.
//Opcode 6 is jump-if-false: if the first parameter is zero, it sets the instruction pointer to the value from the second parameter. Otherwise, it does nothing.
//Opcode 7 is less than: if the first parameter is less than the second parameter, it stores 1 in the position given by the third parameter. Otherwise, it stores 0.
//Opcode 8 is equals: if the first parameter is equal to the second parameter, it stores 1 in the position given by the third parameter. Otherwise, it stores 0.