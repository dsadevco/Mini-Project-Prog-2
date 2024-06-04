public class Woman {
    public static void main(String[] xxx) {
        int n = 5;
        for (int i = 1; i <= n ; i++){
            for(int j = 1; j <=n ; j++){
                if( i % 2 != 0)
                    System.out.printf("%3d", i*j);

            }
            System.out.print("\n");
        }
    }
}
