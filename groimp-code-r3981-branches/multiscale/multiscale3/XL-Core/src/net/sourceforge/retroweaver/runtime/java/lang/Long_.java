package net.sourceforge.retroweaver.runtime.java.lang;

public class Long_ {

	private Long_() {
		// private constructor
	}

	public static Long valueOf(final long val) {
		return new Long(val);
	}

	public static int numberOfLeadingZeros (long i)
	{
		if (i == 0)
		{
			return 64;
		}
		int n = 1;
		int x = (int) (i >>> 32);
		if (x == 0)
		{
			n += 32;
			x = (int) i;
		}
		if (x >>> 16 == 0)
		{
			n += 16;
			x <<= 16;
		}
		if (x >>> 24 == 0)
		{
			n += 8;
			x <<= 8;
		}
		if (x >>> 28 == 0)
		{
			n += 4;
			x <<= 4;
		}
		if (x >>> 30 == 0)
		{
			n += 2;
			x <<= 2;
		}
		return n - (x >>> 31);
	}

}
