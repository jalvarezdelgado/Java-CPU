
public class computer {
	private memory compMem = new memory();
	private bit power = new bit(0); //Bit used to determine whether or not the computer is running, off by default
	private bit performInstruction = new bit(0); //Used to determine whether instructions should be carried out in 
	longword PC = new longword();
	longword currentInstruction = new longword();
	longword[] register = new longword[16];
	longword op1 = new longword();
	longword op2 = new longword();
	longword opCode = new longword();
	longword resultAddress = new longword();
	longword result = new longword();
	bit comparisonBit1 = new bit(0);
	bit comparisonBit2 = new bit(0);
	longword SP = new longword(1020);
	
	public computer() {
		for(int i = 0; i < this.register.length; i++) {
			this.register[i] = new longword(0);
		}
	}
	
	public computer(memory mem, longword PC, longword[] register) {
		this.compMem = mem;
		this.PC = PC;
		this.register = register;
	}
	
	public void run() {
		this.power.set(1);
		while(power.getValue() == 1) {
			fetch();
			decode();
			execute();
			store();
		}
	}
	
	void fetch() {
		this.currentInstruction = this.compMem.read(PC);
		this.currentInstruction = this.currentInstruction.leftShift(8);
		this.currentInstruction = rippleAdder.add(this.compMem.read(rippleAdder.add(PC, new longword(1))), this.currentInstruction);
		//String instruct = "";
		/*for(int i = 0; i < 16; i++) {
			instruct += this.currentInstruction.getBit(15 - i).toString();
		}*/
		//System.out.println("\nCurrent Instruction is: " + instruct);
		//System.out.println("In other words, Current Instruction is: " + this.currentInstruction);
		PC = rippleAdder.add(PC, new longword(2));
	}
	
	void decode() {
		this.opCode = operationMask(this.currentInstruction);
		this.op1 = op1Mask(this.currentInstruction);
		this.op2 = op2Mask(this.currentInstruction);
		this.resultAddress = resultAddrMask(this.currentInstruction);
	}
	
	void execute() {
		bit[] op = new bit[4];
		for(int i = 0; i < 4; i++) {
			op[i] = opCode.getBit(i);
		}
		if(op[0].or(op[1]).getValue() == 0 && op[2].or(op[3]).getValue() == 0) { //Checks that the first and second pair of bits are all 0
			this.performInstruction.set(0);
			System.out.println("Received an instruction to shut down..");
			this.power.set(0);
		} else if(op[0].getValue() == 1 && op[1].getValue() == 0 && op[2].or(op[3]).getValue() == 0) { //Checks that the first bit is 1, the second bit is 0, and the last two bits are 0, 0001
			this.performInstruction.set(0);
			longword registerAddr = this.op1;
			longword value = new longword(0);
			this.op2 = this.op2.leftShift(4); // ...0000XXXX -> ...XXXX0000
			value = rippleAdder.add(this.op2, this.resultAddress); //...XXXX0000 + ...0000XXXX = ...XXXXXXXX, this way the last four bits are assigned
			int signedValue = 0;
			if(value.getBit(7).getValue() == 1) { //This means we were given a negative number
				for(int i = 0; i < 7; i++) {
					if(value.getBit(i).getValue() == 1) {
						signedValue += Math.pow(2, i);
					}
				}
				signedValue -= 128;
			} else {
				signedValue = value.getSigned();
			}
			System.out.println("Moving value " + signedValue + " to register at index " + registerAddr.getSigned());
			move(registerAddr, value);
		} else if(op[0].getValue() == 0 && op[1].getValue() == 1 && op[2].or(op[3]).getValue() == 0) { //0010, Interrupt instruction
			this.performInstruction.set(0);
			if(this.resultAddress.getBit(0).getValue() == 1) { //Checks the first bit of the last group of four bits, determines whether to print all registers or all bits of memory
				//Print the memory
				System.out.println("PRINTING FULL MEMORY: " + this.compMem.toString());
			} else {
				//Print our registers
				System.out.println("PRINTING REGISTERS: ");
				this.displayRegisters();
			}
		} else if(op[0].and(op[1]).getValue() == 1 && op[2].or(op[3]).getValue() == 0) { //First two bits are 1, last two are 0, 0011, Jump
			this.performInstruction.set(0);
			longword tempLong = this.op1.leftShift(4);
			tempLong = rippleAdder.add(tempLong, this.op2).leftShift(4);
			tempLong = rippleAdder.add(tempLong, this.resultAddress);
			if(tempLong.getSigned() % 2 == 1) { //Filters out odd addresses, since instructions begin at even addresses
				System.out.println("Tried jumping to an odd address, but instructions begin at even addresses! Rounding down to an odd number..");
				tempLong = new longword(tempLong.getSigned() - 1);
			}
			if(tempLong.getSigned() < 0) {
				System.out.println("Can't jump to negative address! Proceeding to the next command..");
			}
			this.PC = tempLong;
			System.out.println("Set PC to " + tempLong.getSigned());
			System.out.println("Memory at address is: " + this.compMem.byteToString(tempLong.getSigned()) + this.compMem.byteToString(tempLong.getSigned() + 1));
		} else if(op[0].or(op[1]).getValue() == 0 && op[2].getValue() == 1 && op[3].getValue() == 0) { //0100, instruction to compare two registers
			int firstRegister = 0;
			int secondRegister = 0;
			for(int i = 0; i < 4; i++) {
				if(this.op2.getBit(i).getValue() == 1) {
					firstRegister += Math.pow(2, i);
				}
			}
			for(int j = 0; j < 4; j++) {
				if(this.resultAddress.getBit(j).getValue() == 1) {
					secondRegister += Math.pow(2, j);
				}
			}
			longword comparisonResult = rippleAdder.subtract(register[firstRegister], register[secondRegister]);
			if(comparisonResult.getBit(31).getValue() == 0) { //Positive, indicates that the first value is "greater than" the second value
				this.comparisonBit1.set(1);
			} else { //Otherwise, if "less than" set to 0, "either one" if equal
				this.comparisonBit1.set(0);
			}
			
			this.comparisonBit2.set(1);
			for(int i = 0; i < 31; i++) { //Runs through the result longword, if it finds a non-zero bit, the longword doesn't represent 0
				if(comparisonResult.getBit(i).getValue() == 1) {
					this.comparisonBit2.set(0); //Indicates that the two registers are not equal
				}
			}
			
			System.out.println("\nCompared values at R" + firstRegister + "(" + register[firstRegister].getSigned() + ") and R" + secondRegister + "(" + register[secondRegister].getSigned() + ").");
			System.out.println("Set comparison bits to " + comparisonBit2.getValue() + comparisonBit1.getValue());
			System.out.println("First (right) bit: 0 -> Less Than, 1 -> Greater Than, either one if equal\nSecond (left) bit: 0 -> Not Equal, 1 -> Equal\n");
		} else if(op[0].and(op[2]).getValue() == 1 && op[1].or(op[3]).getValue() == 0) { //0101 opcode representing a conditional jump
			this.performInstruction.set(0);
			if(this.op1.getBit(3).and(this.comparisonBit2).getValue() == 1) { //Looking for equals, the comparison was resulted in equals
				this.performInstruction.set(1);
			} else if(this.op1.getBit(3).getValue() == 0 && this.op1.getBit(2).getValue() == 1) { //Greater than, not equals
				if(this.comparisonBit1.getValue() == 1) {
					this.performInstruction.set(1);
				}
			} else if(this.op1.getBit(3).getValue() == 1 && this.op1.getBit(2).getValue() == 1) { //11, Greater than or equal to
				if(this.comparisonBit1.getValue() == 1 || this.comparisonBit2.getValue() == 1) {
					this.performInstruction.set(1);
				}
			} else if(this.op1.getBit(3).getValue() == 0) { //Not equal, checking the last bit
				if(this.comparisonBit2.getValue() == 0) {
					this.performInstruction.set(1);
				}
			}
			
			/*
			 * 01 Greater than
			 * 11 Greater than or equal to
			 * 0x Not equal
			 * 1x Equal
			 */
			
		} else if(op[0].or(op[3]).getValue() == 0 && op[1].and(op[2]).getValue() == 1) { //0110, stack operations
			this.performInstruction.set(0);
			bit secondOp[] = new bit[4];
			for(int i = 0; i < 4; i++) {
				secondOp[i] = this.op1.getBit(i);
			}
			
			if(secondOp[0].or(secondOp[1]).getValue() == 0 && secondOp[2].or(secondOp[3]).getValue() == 0) { //0000, Push
				longword pushedValue = this.register[this.resultAddress.getSigned()]; //Retrieving a value from the specified register
				longword writeBytes[] = {new longword(), new longword(), new longword(), new longword()}; //Written this way since otherwise new longword[4] leads to a NullPointerException
				for(int j = 0; j < 8; j++) {
					writeBytes[0].setBit(j, pushedValue.getBit(j + 24));
				}
				for(int k = 0; k < 8; k++) {
					writeBytes[1].setBit(k, pushedValue.getBit(k + 16));
				}
				for(int l = 0; l < 8; l++) {
					writeBytes[2].setBit(l, pushedValue.getBit(l + 8));
				}
				for(int o = 0; o < 8; o++) {
					writeBytes[3].setBit(o, pushedValue.getBit(o));
				}
				//Pushing values into each of the bytes
				this.compMem.write(this.SP, writeBytes[0]);
				this.compMem.write(rippleAdder.add(this.SP, new longword(1)), writeBytes[1]);
				this.compMem.write(rippleAdder.add(this.SP, new longword(2)), writeBytes[2]);
				this.compMem.write(rippleAdder.add(this.SP, new longword(3)), writeBytes[3]);
				this.SP = rippleAdder.subtract(this.SP, new longword(4));
			} else if(secondOp[0].or(secondOp[1]).getValue() == 0 && secondOp[2].getValue() == 1 && secondOp[3].getValue() == 0) { //0100, Pop
				this.SP = rippleAdder.add(this.SP, new longword(4));
				
				longword readBytes[] = {new longword(), new longword(), new longword(), new longword()};
				
				readBytes[0] = this.compMem.read(this.SP).leftShift(24);
				readBytes[1] = this.compMem.read(rippleAdder.add(this.SP, new longword(1))).leftShift(16);
				readBytes[2] = this.compMem.read(rippleAdder.add(this.SP, new longword(2))).leftShift(8);
				readBytes[3] = this.compMem.read(rippleAdder.add(this.SP, new longword(3)));
				
				this.compMem.write(this.SP, new longword(0));
				this.compMem.write(rippleAdder.add(this.SP, new longword(1)), new longword(0));
				this.compMem.write(rippleAdder.add(this.SP, new longword(2)), new longword(0));
				this.compMem.write(rippleAdder.add(this.SP, new longword(3)), new longword(0));
				
				longword poppedValue = rippleAdder.add(readBytes[1], rippleAdder.add(readBytes[2], readBytes[3]));
				for(int i = 31; i > 23; i--) { //It's done this way because rippleAdder begins subtracting when negative numbers are involved, messes with the final result
					poppedValue.setBit(i, readBytes[0].getBit(i));
				}
				this.register[this.resultAddress.getSigned()] = poppedValue;
				
			} else if(secondOp[2].getValue() == 0 && secondOp[3].getValue() == 1) { //10XX, Call
				longword pushedPC = this.PC;
				longword newPC = this.op2.leftShift(4);
				newPC = rippleAdder.add(this.resultAddress, newPC);
				newPC.setBit(8, this.op1.getBit(0));
				newPC.setBit(9, this.op1.getBit(1));
				this.PC = newPC;
				System.out.println("PC being pushed to the stack: " + pushedPC.getSigned());
				System.out.println("PC set to: " + newPC.getSigned() + "\n");
				
				longword pushedBytes[] = {new longword(), new longword(), new longword(), new longword()};
				for(int i = 0; i < 8; i++) {
					pushedBytes[0].setBit(i, pushedPC.getBit(i + 24));
				}
				for(int j = 0; j < 8; j++) {
					pushedBytes[1].setBit(j, pushedPC.getBit(j + 16));
				}
				for(int k = 0; k < 8; k++) {
					pushedBytes[2].setBit(k, pushedPC.getBit(k + 8));
				}
				for(int l = 0; l < 8; l++) {
					pushedBytes[3].setBit(l, pushedPC.getBit(l));
				}
				
				//Pushing the PC to the stack
				this.compMem.write(this.SP, pushedBytes[0]);
				this.compMem.write(rippleAdder.add(this.SP, new longword(1)), pushedBytes[1]);
				this.compMem.write(rippleAdder.add(this.SP, new longword(2)), pushedBytes[2]);
				this.compMem.write(rippleAdder.add(this.SP, new longword(3)), pushedBytes[3]);
				
				this.SP = rippleAdder.subtract(this.SP, new longword(4));
			} else if(secondOp[0].or(secondOp[1]).getValue() == 0 && secondOp[2].and(secondOp[3]).getValue() == 1) { //1100, Return
				this.SP = rippleAdder.add(this.SP, new longword(4));
				
				longword newPC = compMem.read(rippleAdder.add(this.SP, new longword(1))); //....XXXXXXX
				newPC = newPC.leftShift(8);//....XXXXXXXX00000000
				newPC = rippleAdder.add(newPC, compMem.read(rippleAdder.add(this.SP, new longword(2))));//....XXXXXXXXYYYYYYYY
				newPC = newPC.leftShift(8); //....XXXXXXXXYYYYYYYY00000000
				newPC = rippleAdder.add(newPC, compMem.read(rippleAdder.add(this.SP, new longword(3)))); //....XXXXXXXXYYYYYYYYWWWWWWWW
				for(int i = 0; i < 8; i++) {
					newPC.setBit(i + 24, compMem.read(this.SP).getBit(i));
				}
				System.out.println("\nRETURNING TO PC: " + newPC.getSigned() + "\n");
				
				this.PC = newPC;
			}
		} else {
			this.performInstruction.set(1);
			System.out.println("Performing operation " + op[3]+op[2]+op[1]+op[0] + " on values of registers " + this.op1.getSigned() + " and " + this.op2.getSigned() + ", the result will be sent to register " + this.resultAddress.getSigned());
			result = ALU.doOp(op, register[op1.getSigned()], register[op2.getSigned()]);
		}
	}
	
	void store() {
		if(this.power.getValue() == 1) {
			if(this.performInstruction.getValue() == 1) {
				if(this.opCode.getBit(0).and(this.opCode.getBit(2)).getValue() == 1 && this.opCode.getBit(1).or(this.opCode.getBit(3)).getValue() == 0) {
					int jumpAmount = 0; //As specified in the document, amount of bytes to add to (or subtract from) the PC
					for(int i = 0; i < 4; i++) {
						if(resultAddress.getBit(i).getValue() == 1) {
							jumpAmount += Math.pow(2, i);
						}
					}
					
					for(int j = 0; j < 4; j++) {
						if(op2.getBit(j).getValue() == 1) {
							jumpAmount += Math.pow(2, j + 4);
						}
					}
					
					if(op1.getBit(0).getValue() == 1) {
						jumpAmount += Math.pow(2, 8);
					}
					if(op1.getBit(1).getValue() == 1) { //Signed bit, indicating that the value is negative
						jumpAmount -= 1024;
					}
					this.PC = rippleAdder.add(this.PC, new longword(jumpAmount));
				}
				register[resultAddress.getSigned()] = result;
			}
		}
		
	}
	
	/**
	 * Helper method to mask the bits representing the operation
	 * @param instruction Longword containing the instructions
	 * @return A longword whose first four bits only represent the operation
	 */
	private longword operationMask(longword instruction) {
		longword tempInstruction = instruction;
		longword mask = new longword(15);
		tempInstruction = tempInstruction.rightShift(12);
		return tempInstruction.and(mask);
		
	}
	
	/**
	 * Helper methods masking the instruction to isolate the first operator
	 * @param instruction Longword containing the instructions
	 * @return A longword whose first four bits only represent the first operator
	 */
	private longword op1Mask(longword instruction) {
		longword tempInstruction = instruction.leftShift(20);
		tempInstruction = tempInstruction.rightShift(28);
		longword mask = new longword(15);
		return tempInstruction.and(mask);
	}
	
	/**
	 * Helper method masking the instruction to isolate the second operator
	 * @param instruction Longword containing the instructions
	 * @return Longword whose first four bits only represent the second operator
	 */
	private longword op2Mask(longword instruction) {
		longword tempInstruction = instruction.leftShift(24);
		tempInstruction = tempInstruction.rightShift(28);
		longword mask = new longword(15);
		return tempInstruction.and(mask);
	}
	
	/**
	 * Helper method to mask the instructions and isolate the result address
	 * @param instruction Longword containing the instructions
	 * @return Longword whose first four bits only represent the result address
	 */
	private longword resultAddrMask(longword instruction) {
		longword tempInstruction = instruction.leftShift(28);
		tempInstruction = tempInstruction.rightShift(28);
		longword mask = new longword(15);
		return tempInstruction.and(mask);
	}
	
	/**
	 * Helper method to set the value of the register at the specified address to a given value
	 * @param registerAddress The address of the register we want to modify
	 * @param value The value to be assigned to the register
	 */
	private void move(longword registerAddress, longword value) {
		if(value.getBit(7).getValue() == 1) {
			int signedValue = 0;
			for(int i = 0; i < 7; i++) {
				if(value.getBit(i).getValue() == 1) {
					signedValue += Math.pow(2, i);
				}
			}
			signedValue -= 128;
			longword temp = new longword(signedValue);
			this.register[registerAddress.getSigned()] = rippleAdder.complement(temp);
			this.register[registerAddress.getSigned()].setBit(31, new bit(1));
		} else {
			this.register[registerAddress.getSigned()] = value;
		}
	}
	
	void preload(String[] args) {
		for(int i = 0; i < args.length; i++) { //Runs through each String/group of bits
			//System.out.println("First working with: " + args[i]);
			longword rightByte = new longword(0);
			longword leftByte = new longword(0);
			String[] splitBits = args[i].split(" "); //Separates string in the format of 0000 0000 0000 0000 into four strings of 0000
			//System.out.println("Becomes: " + splitBits[0] + ", " + splitBits[1] + ", " + splitBits[2] + ", " + splitBits[3]);
			for(int j = 0; j < 4; j++) {
				if(splitBits[3].charAt(3 - j) == '1') {
					rightByte.setBit(j, new bit(1));
				} else {
					rightByte.setBit(j, new bit(0));
				}
			}
			for(int k = 0; k < 4; k++) {
				if(splitBits[2].charAt(3 - k) == '1') {
					rightByte.setBit(k + 4, new bit(1));
				} else {
					rightByte.setBit(k + 4, new bit(0));
				}
			}
			for(int l = 0; l < 4; l++) {
				if(splitBits[1].charAt(3 - l) == '1') {
					leftByte.setBit(l, new bit(1));
				} else {
					leftByte.setBit(l, new bit(0));
				}
			}
			for(int w = 0; w < 4; w++) {
				if(splitBits[0].charAt(3 - w) == '1') {
					leftByte.setBit(w + 4, new bit(1));
				} else {
					leftByte.setBit(w + 4, new bit(0));
				}
			}
			//System.out.println("Original: " + args[i] + "\nleft  byte: " + leftByte + "\nright byte: " + rightByte);
			
			this.compMem.write(determineWriteAddress(i, false), leftByte);
			this.compMem.write(determineWriteAddress(i, true), rightByte);
		}
	}
	
	/**
	 * Helper method to generate a longword representing the appropriate address to preload instructions onto
	 * @return
	 */
	private longword determineWriteAddress(int counter, boolean firstByte) {
		longword output = multiplier.multiply2(new longword(2), new longword(counter + 1));
		if(firstByte) {
			return rippleAdder.subtract(output, new longword(1));
		} else {
			return rippleAdder.subtract(output, new longword(2));
		}
	}
	
	private void displayRegisters() {
		String output = "\n";
		for(int i = 0; i < this.register.length; i++) {
			output += "[" + this.register[i] + "]" + " Address: " + i + "\n";
		}
		System.out.println(output);
	}
	
	public void diagnostics() {
		System.out.println("Current PC value: " + this.PC.getSigned());
		System.out.println("Computer Registers: ");
		this.displayRegisters();
		System.out.println("Computer memory: " + this.compMem.toString());
	}
}
