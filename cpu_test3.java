
public class cpu_test3 {
	
	public static void main(String args[]) {
		runTests();
	}
	
	public static void runTests() {
		System.out.println("Performing tests of the stack operations:");
		System.out.println("\nTesting the PUSH operation:");
		System.out.println("In this test, four values will be pushed onto the stack, then the computer's registers and full memory will be displayed:");
		computer pushCPU = new computer();
		String pushCmds[] = {"move R0 20", "move R1 -50", "move R2 -100", "move R3 90", "push R0", "push R1", "push R2", "push R3", "Interrupt 0", "Interrupt 1", "halt"};
		pushCPU.preload(Assembler.assemble(pushCmds));
		pushCPU.run();
		
		System.out.println("\nNow testing the POP operation:\nValues will be pushed onto the stack and then they'll be popped into the latter registers. Then the computer's registers and memory will be printed.");
		computer popCPU = new computer();
		String popCmds[] = {"move R0 44", "move R1 -100", "push R0", "push R1", "pop R15", "pop R14", "Interrupt 1", "Interrupt 0", "halt"};
		popCPU.preload(Assembler.assemble(popCmds));
		popCPU.run();
		
		System.out.println("\nNow testing the CALL operation:\nFor now, we'll only be using it to push addresses into the stack, then the memory will be printed.");
		computer callCPU = new computer();
		String callCmds[] = {"call 5", "call 10", "call 20", "Interrupt 1", "halt"};
		callCPU.preload(Assembler.assemble(callCmds));
		callCPU.run();
		
		System.out.println("\nLastly, performing a test of the RETURN operation:");
		System.out.println("For this test, a small function will be written to add numbers from two registers and move the sum to a third register.");
		computer returnCPU = new computer();
		String returnCmds[] = {"move R0 20", "move R1 15", "push R0", "push R1", "call 16", "Interrupt 1", "Interrupt 0", "halt", "pop R15", "pop R0", "pop R1", "add R0 R1 R2", "push R2", "push R15", "return"};		
		returnCPU.preload(Assembler.assemble(returnCmds));
		returnCPU.run();
		
		System.out.println("As can be seen in the computer's registers, the sum of 20 and 15, 35, can be found as \"100011\" in R2, meaning the program was sucessfully executed.");
	}
}
