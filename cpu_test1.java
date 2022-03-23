
public class cpu_test1 {
	
	public static void main(String args[]) {
		runTests();
		ALU_test.runTests();
		System.out.println("\n");
		multiplier_test.runTests();
		System.out.println("\n\nTESTING RIPPLEADDER OPERATIONS:");
		rippleAdder_test.runTests();
		System.out.println("TESTING LONGWORD OPERATIONS:");
		longword_test.runTests();
		System.out.println("TESTING BIT OPERATIONS:");
		bit_test.runTests();
	}
	
	/**
	 * Method to run the tests of the computer class. A new computer will be created for moving values and each operation of the ALU
	 * The computer's registers and memory will be displayed to verify the results
	 */
	public static void runTests() {
		moveTest();
		ALUTests();
	}
	
	public static void moveTest() {
		String shutdown = "0000 0000 0000 0000"; //Used in all tests to halt the computer once the tests are completed
		String printRegisters = "0010 0000 0000 0000";
		String printMemory = "0010 0000 0000 0001";
		
		//Move values into the registers
		System.out.println("\nMoving values into the computers registers:");
		System.out.println("10 (1010) going to R2, 20 (10100) going to R3, 230 (11100110) going to R6");
		computer moveComp = new computer();
		String move10ToR2 = "0001 0010 0000 1010";
		String move20ToR3 = "0001 0011 0001 0100";
		String move230ToR6 = "0001 0110 1110 0110";
		
		String[] moveCommands = {move10ToR2, move20ToR3, move230ToR6, printRegisters, printMemory, shutdown};
		moveComp.preload(moveCommands);
		moveComp.run();
	}
	
	public static void ALUTests() {
		String shutdown = "0000 0000 0000 0000";
		String printRegisters = "0010 0000 0000 0000";
		String printMemory = "0010 0000 0000 0001";
		
		//Using the ALU
		System.out.println("\nTesting the ALU through computer:");
		System.out.println("\nTesting Additon:");
		computer addComputer = new computer();
		String addMove1 = "0001 0000 0000 1111"; //Moving 15 to R0
		String addMove2 = "0001 0001 0001 0100"; //Moving 20 to R1
		String addR0R1ToR3 = "1110 0000 0001 0011"; //Adding R0 and R1, result in R3
		System.out.println("Adding 15 from register 0 and 20 from register 1, recording result in register 3. Expecting 35 (100011)");
		String[] addCommands = {addMove1, addMove2, addR0R1ToR3, printRegisters, printMemory, shutdown};
		addComputer.preload(addCommands);
		addComputer.run();
			
		System.out.println("\nTesting Substraction:");
		computer subtractComputer = new computer();
		String subMove1 = "0001 0001 0110 1100"; //Moving 108 to R1
		String subMove2 = "0001 0010 0010 1100"; //Moving 44 to R2
		String subtractR1R2ToR5 = "1111 0001 0010 0101"; //Subtracting R2 from R1, recording result in R5
		System.out.println("Subtracting 44 (register 2) from 108 (register 1), sending the difference to register 5. Expecting 64 (1000000)");
		String[] subCommands = {subMove1, subMove2, subtractR1R2ToR5, printRegisters, printMemory, shutdown};
		subtractComputer.preload(subCommands);
		subtractComputer.run();
		
		System.out.println("\nTesting AND operation: ");
		computer andComputer = new computer();
		String andMove1 = "0001 0110 0101 1001"; //Moving bits representing 89 to R6
		String andMove2 = "0001 0011 1100 1101"; //Moving bits representing 205 to R3
		String andOp = "1000 0110 0011 0000"; //Performing AND operation between R4 and R3, recording result in R0
		System.out.println("Performing AND operation between \"11001101\" (register 3) and \"01011001\" (register 6). Expecting 1001001");
		String[] andCommands = {andMove1, andMove2, andOp, printRegisters, printMemory, shutdown};
		andComputer.preload(andCommands);
		andComputer.run();
	}
}
