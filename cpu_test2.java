
public class cpu_test2 {
	
	public static void main(String args[]) {
		runTests();
	}
	
	public static void runTests() {
		System.out.println("RUNNING TESTS OF JUMP, COMPARE, AND BRANCH OPERATIONS\n");
		System.out.println("Interrupt commands will be used as a \"buffer\" when testing Jump operations, as their output is very noticable should the jump not be performed correctly. Registers and/or memory will be displayed at the end of each test\n");
		computer jumpCPU = new computer();
		String jumpCommands[] = {"move R0 20", "jump 6", "move R1 5", "move R2 50", "Interrupt 0", "halt"};
		System.out.println("In this test, the computer will be loaded with commands to move values into the registers and jump over one of these operations.\nIf performed correctly, there will only be values in R0 and R2, the computer will jump over the operation moving into R1");
		jumpCPU.preload(Assembler.assemble(jumpCommands));
		jumpCPU.run();
		
		System.out.println("\nNow performing tests of the Compare and Branch operations");
		greaterThanTest();
		greaterThanOrEqualTest();
		equalTest();
		notEqualTest();
	}
	
	public static void greaterThanTest() {
		System.out.println("!! For this test of the ifGreaterThan Branch, values will be moved to the register and compared. After which, the Branch instruction will find that the first value is greater,\nskipping moving a value into R2 and displaying the memory. This will be visible in the registers and when the memory isn't printed");
		computer greaterThanCPU = new computer();
		String greatCommands[] = {"move R0 20", "move R1 15", "compare R0 R1", "Branchifgreaterthan 4", "move R2 20", "Interrupt 1", "Interrupt 0", "halt"};
		greaterThanCPU.preload(Assembler.assemble(greatCommands));
		greaterThanCPU.run();
		
		System.out.println("\n!! In this test, the computer will be given a BranchifGreaterThan instruction for values that should return false, meaning each line should be carried out. The register at the end will show R0-R5 with values that would have otherwise been empty.");
		computer notGreaterCPU = new computer();
		String notGreatCommands[] = {"move R0 10", "move R1 50", "compare R0 R1", "Branchifgreaterthan 8", "move R2 40", "move R3 60", "move R4 97", "move R5 127", "Interrupt 0", "halt"};
		notGreaterCPU.preload(Assembler.assemble(notGreatCommands));
		notGreaterCPU.run();
	}
	
	public static void greaterThanOrEqualTest() {
		System.out.println("\n!! Now performing a test of the ifGreaterThanOrEqual Branch. Similarly, the computer will receive an instruction to skip over printing the full memory if the first value is greater than or equal to the second");
		computer greaterOrEqual = new computer();
		String greatEqualCommands[] = {"move R0 50", "move R1 50", "compare R0 R1", "Branchifgreaterthanorequal 2", "Interrupt 1", "move R3 25", "compare R0 R3", "Branchifgreaterthanorequal 2", "Interrupt 1", "Interrupt 0", "halt"};
		greaterOrEqual.preload(Assembler.assemble(greatEqualCommands));
		greaterOrEqual.run();
	}
	
	public static void equalTest() {
		System.out.println("\n!! This is a test of the ifEqual Branch. Values will be compared to determine whether they are equivalent");
		System.out.println("In the following case, if the values are equal then only the register will be printed, otherwise the instruction to print the full memory will be carried out.");
		computer equalCPU = new computer();
		String equalCommands[] = {"move R0 30", "move R1 30", "compare R0 R1", "BranchifEqual 2", "Interrupt 1", "Interrupt 0", "halt"};
		equalCPU.preload(Assembler.assemble(equalCommands));
		equalCPU.run();
		
		System.out.println("\n!! This time, two unequal values will be compared. Since the Branch condition is not fulfilled, several values will be moved into the register, which will be displayed at the end.");
		computer unequalCPU = new computer();
		String unequalCommands[] = {"move R2 20", "move R3 50", "compare R2 R3", "Branchifequal 12", "move R1 70", "move R4 30", "move R5 60", "move R6 63", "move R7 90", "move R9 127", "Interrupt 0", "halt"};
		unequalCPU.preload(Assembler.assemble(unequalCommands));
		unequalCPU.run();
	}
	
	public static void notEqualTest() {
		System.out.println("\n!! Tests on the ifNotEqual Branch. If compared values are found to not be equal, a number of instructions will be skipped.");
		computer notEqualCPU = new computer();
		String notEqualCommands[] = {"move R0 15", "move R1 10", "compare R0 R1", "Branchifnotequal 2", "Interrupt 1", "halt"};
		notEqualCPU.preload(Assembler.assemble(notEqualCommands));
		notEqualCPU.run();
		
		System.out.println("\n!! Test in which equivalent values will be compared, since the Branch condition is not met, the full memory and registers will be displayed.");
		computer reallyEqualCPU = new computer();
		String reallyEqualCommands[] = {"move R2 50", "move R3 50", "compare R2 R3", "Branchifnotequal 4", "Interrupt 0", "Interrupt 1", "move R5 63", "halt"};
		reallyEqualCPU.preload(Assembler.assemble(reallyEqualCommands));
		reallyEqualCPU.run();
	}
}
