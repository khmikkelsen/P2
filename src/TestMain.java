public class TestMain
{
    public static void main (String[] args)
    {
        KeyPair alice = new KeyPair(61, 53);

        System.out.print("\npublic key for "+61+" & "+53+" er "+alice.getPublicKey()+"\n");

        System.out.print("private key for "+61+" & "+53+" er "+alice.getPrivateKey()+"\n");


    }
}
