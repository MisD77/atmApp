//Author DA
//layout class for text absed interaction between user and the machine

public class Layout
{
    public static void Banner()
    {
      System.out.println();   
      System.out.println("*************************************************");
      System.out.println("*             WELCOME TO CITY ATM               *");
      System.out.println("*************************************************");
      System.out.println();    
    }
    
    public static void Login()
    {
         System.out.print("Enter [1] Login\t[2] SignUp : ");                  
    }
    
    public static void SignUp()
    {
         System.out.print("Enter [1] New user \t[2] SignUp : ");                  
    }
    
    public static void Navigation()
    {
         System.out.println("------------------------------------");
         System.out.println("*********** SELECT BELOW ***********");
         System.out.println("[c] - Check Balance");
         System.out.println("[d] - Deposit");
         System.out.println("[w] - Withdraw Cash");
         System.out.println("[t] - Transaction View");
         System.out.println("[s] - View Statement");
         System.out.println("------------------------------------");
         System.out.print("\nEnter the transaction type: ");
    }
    
    public static void exit()
    {
         System.out.println("\n---------------------------------------------");
         System.out.println("********* Thank you. Have A Nice Day. *********");
         System.out.println("-----------------------------------------------");
    }
    
}