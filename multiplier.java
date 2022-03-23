
public class multiplier {
	
	/**
	 * Multiplication algorithm utilizing longword shifts and rippleAdder operations. 64-bit number represented by two 32-bit longwords
	 * (longwords A and Q). 
	 * @param a First longword used in the operation
	 * @param b Second longword used in the operation
	 * @return Longword representing the product of two longwords
	 */
	public static longword multiply2(longword a, longword b) {
		longword Q = a;
		longword B = b;
		longword A = new longword();
		bit C = new bit();
		
		for(int i = 0; i < 32; i++) {
			//System.out.println("Start of cycle " + i + ":\n" + C + " " + A + " " + Q);
			if(Q.getBit(0).getValue() == 0) {
				//System.out.println("Q0 == 0, will only shift");
				Q = Q.rightShift(1);
				Q.setBit(31, A.getBit(0)); //Shifts the rightmost bit of A into the leftmost position in Q
				
				A = A.rightShift(1);
				A.setBit(31, C); //Shifting C, leftmost bit involved, into the leftmost position in A
				
				C.set(0);
				//groupRightshift(C, A, Q);
			} else if(Q.getBit(0).getValue() == 1) {
			//	System.out.println("Q0 == 1, will add and shift");
				A = rippleAdder.add(A, B);
				
				Q = Q.rightShift(1);
				Q.setBit(31, A.getBit(0));
				
				A = A.rightShift(1);
				A.setBit(31, C);
				
				C.set(0);
			}
			//System.out.println("End of cycle "+ i + ":\n" + C + " " + A + " " + Q + "\n");
		} 
		longword product = Q;
		if(a.getBit(31).xor(b.getBit(31)).getValue() == 1) { //In the case that a negative and non-negative number are multiplied
															 //The product should be negative
			product.setBit(31, new bit(1));
		} else {
			product.setBit(31, new bit(0));
		}
		
		return product;
	}
	
	public static longword multiply(longword a, longword b) {
		longword product = new longword();
		for(int i = 0; i < 31; i++) {
			if(a.getBit(i).getValue() == 1) {
				product = rippleAdder.add(product, b.leftShift(i));
			}
		}
		if(a.getBit(31).xor(b.getBit(31)).getValue() == 1) {
			product.setBit(31, new bit(1));
		}
		return product;
	}
	
	/**
	 * Helper method to perform rightShift on two longwords and a bit for the multiplication process
	 * @param C Bit used in the multiplication
	 * @param A First longword used in the multiplication
	 * @param Q Second longword used in the multiplication
	 */
	static void groupRightshift(bit C, longword A, longword Q) {
		//System.out.println(C + " " + A + " " + Q);
		Q = Q.rightShift(1);
		Q.setBit(31, A.getBit(0)); //Shifts the rightmost bit of A into the leftmost position in Q
		//System.out.println(Q);
		
		A = A.rightShift(1);
		A.setBit(31, C); //Shifting C, leftmost bit involved, into the leftmost position in A
		
		C.set(0);
		//System.out.println(C + " " + A + " " + Q);
	}
}
