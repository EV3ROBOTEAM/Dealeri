package dealer;

public class test {
	static int a = 280;
	static int b = 200;
	
   	static int d = (int) (Math.abs(b - a) % 360);
    static int r = d > 180 ? 360 - d : d;
    public static void main (String [] args) {
        System.out.println(r);

        
       boolean g = (b - a + 360) % 360 < 180;
       System.out.println(g);
    }
}
