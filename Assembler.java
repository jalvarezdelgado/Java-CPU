
public class Assembler {
	
	/**
	 * Method to process an array of instructions, each String/instruction is converted into a format that can be loaded
	 * onto a Computer
	 * @param instructionList Array of Strings representing instructions written in a readable language
	 * @return Array of Strings representing the same instructions in the form of bits
	 */
	public static String[] assemble(String[] instructionList) {
		String[] output = new String[instructionList.length];
		for(int i = 0; i < instructionList.length; i++) { //Works through the list of instructions
			output[i] = ""; //Initializes this entry in output, otherwise starts as 'null'
			String[] tempArray = instructionList[i].split(" ");
			if(tempArray[0].equalsIgnoreCase("halt")) {
				output[i] += "0000 0000 0000 0000"; //A "generic" halt instruction, as the bits after the operation code are ignored
			} else if (tempArray[0].equalsIgnoreCase("move")) {
				output[i] += "0001 ";
				
				//Retrieving the register address
				output[i] += getRegister(tempArray[1]) + " "; //Adding the bits representing register to the output array
				
				//Retrieving the value to move to the specified register
				int value = Integer.valueOf(tempArray[2]);
				longword valueBits = new longword(Math.abs(value));
				if(value < 0) { //Dealing with negative numbers
					valueBits = rippleAdder.complement(valueBits);
				}
				String firstValString = "";
				for(int j = 7; j > 3; j--) {
					if(valueBits.getBit(j).getValue() == 1) {
						firstValString += "1";
					} else {
						firstValString += "0";
					}
				}
				output[i] += firstValString + " ";
				
				String secondValString = "";
				for(int k = 3; k >= 0; k--) {
					if(valueBits.getBit(k).getValue() == 1) {
						secondValString += "1";
					} else {
						secondValString += "0";
					}
				}
				output[i] += secondValString;
			} else if(tempArray[0].equalsIgnoreCase("Interrupt")) { //an interrupt function
				if(tempArray[1].equals("0")) {
					output[i] = "0010 0000 0000 0000"; //prints all registers
				} else {
					output[i] = "0010 0000 0000 0001"; //prints the full memory
				}
			} else if(tempArray[0].equalsIgnoreCase("Jump")) {
				output[i] += "0011 ";
				longword tempLong = new longword(Integer.valueOf(tempArray[1]));
				for(int j = 11; j >= 0; j--) {
					if(tempLong.getBit(j).getValue() == 1) {
						output[i] += "1";
					} else {
						output[i] += "0";
					}
					
					if(j % 4 == 0 && j != 0) {
						output[i] += " ";
					}
					
				}
			} else if(tempArray[0].contains("Branch")) {
				output[i] += "0101 ";
				tempArray[0] = tempArray[0].substring(6);
				if(tempArray[0].equalsIgnoreCase("ifGreaterThanOrEqual")) {
					output[i] += "11";
				} else if(tempArray[0].equalsIgnoreCase("ifGreaterThan")) {
					output[i] += "01";
				} else if(tempArray[0].equalsIgnoreCase("ifEqual")) {
					output[i] += "10";
				} else if(tempArray[0].equalsIgnoreCase("ifNotEqual")) {
					output[i] += "00";
				} else {
					System.out.println("Couldn't identify the condition in Branching statement, " + tempArray[0]);
				}
				
				int jumpAmount = Integer.valueOf(tempArray[1]);
				longword address = new longword(jumpAmount);
				if(jumpAmount < 0) {
					address = rippleAdder.complement(address);
					address.setBit(9, new bit(1));
				} else {
					address.setBit(9, new bit(0));
				}
				for(int j = 9; j >= 0; j--) {
					if(address.getBit(j).getValue() == 1) {
						output[i] += "1";
					} else {
						output[i] += "0";
					}
					if(j == 8 || j == 4) {
						output[i] += " ";
					}
				}
				
			} else if(tempArray[0].equalsIgnoreCase("compare")) {
				output[i] += "0100 0000 " + getRegister(tempArray[1]) + " " + getRegister(tempArray[2]);
			} else if(tempArray[0].equalsIgnoreCase("push")) {
				output[i] += "0110 0000 0000 " + getRegister(tempArray[1]);
			} else if(tempArray[0].equalsIgnoreCase("pop")) {
				output[i] += "0110 0100 0000 " + getRegister(tempArray[1]);
			} else if(tempArray[0].equalsIgnoreCase("call")) {
				output[i] += "0110 10";
				longword callAddress = new longword(Integer.parseInt(tempArray[1]));
				for(int j = 9; j >= 0; j--) {
					if(callAddress.getBit(j).getValue() == 1) {
						output[i] += "1";
					} else {
						output[i] += "0";
					}
					
					if(j == 8 || j == 4) {
						output[i] += " ";
					}
				}
			} else if(tempArray[0].equalsIgnoreCase("return")) {
				output[i] += "0110 1100 0000 0000";
			} else { //Taking it as an ALU operation
				output[i] += getOperation(tempArray[0]) + " ";
				
				output[i] += getRegister(tempArray[1]) + " "; //Retrieving the first register
				output[i] += getRegister(tempArray[2]) + " "; //Second register
				output[i] += getRegister(tempArray[3]); //Third, final register
			}
			
		}
		
		return output;
	}
	
	/**
	 * Helper method to generate a String of bits representing a register address
	 * @param register String in the format of RX, X being the register address
	 * @return A String of bits that can be loaded into a computer
	 */
	private static String getRegister(String register) {
		if(register.charAt(0) != 'R' || register.charAt(1) == '-') { //If there's no register or it is a negative number, there is a problem
			System.out.println("Problem moving value to register "+ register + ", register should be in the form of RX (X being a number between 0-15)");
			return null;
		}
		register = register.substring(1);
		int registerAddr = Integer.valueOf(register);
		String tempRegister = "";
		if(registerAddr < 16) {
			longword registerBits = new longword(registerAddr);
			for(int j = 3; j >= 0; j--) {
				if(registerBits.getBit(j).getValue() == 1) {
					tempRegister += "1";
				} else {
					tempRegister += "0";
				}
			}
		} else {
			System.out.println("Problem moving value to register" + register + ", register should be between 0 and 15");
			return null;
		}
		
		return tempRegister;
	}
	
	/**
	 * Helper method to determine which ALU operation the user is trying to perform. If the instruction
	 * doesn't match a valid operation, null will be returned.
	 * @param operation String holding the user's instruction for the ALU
	 * @return String representing bits corresponding to the desired ALU operation
	 */
	private static String getOperation(String operation) {
		if(operation.equalsIgnoreCase("and")) {
			return "1000";
		} else if(operation.equalsIgnoreCase("or")) {
			return "1001";
		} else if(operation.equalsIgnoreCase("xor")) {
			return "1010";
		} else if(operation.equalsIgnoreCase("not")) {
			return "1011";
		} else if(operation.equalsIgnoreCase("leftshift")) {
			return "1100";
		} else if(operation.equalsIgnoreCase("rightshift")) {
			return "1101";
		} else if(operation.equalsIgnoreCase("add")) {
			return "1110";
		} else if(operation.equalsIgnoreCase("subtract")) {
			return "1111";
		} else if(operation.equalsIgnoreCase("multiply")) {
			return "0111";
		} else {
			System.out.println("Couldn't retrieve ALU operation from " + operation);
			return null;
		}
	}
}
