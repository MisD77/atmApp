//Author- DA
//Project ATM, CS 415
//main class for application

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.sql.*; 


public class MainApp 
{
    public static Scanner kb = new Scanner(System.in);

    public static void main(String[] args) 
    {
      Layout layout = new Layout();
      loginScreen(layout);
    }//main method ends
    
    //Welcome screen for ATM
    public static void loginScreen(Layout layout)
    {
      layout.Banner(); //displays the welcome message to the customers
      layout.Login(); //gives an option to login or create new account
      
      boolean flag = true;
      do 
      {
         int input = kb.nextInt();
         if (input == 1)
         {
            handleLogin(layout);
            flag = false;
         }
         else if (input == 2)
         {
            createNewUser();
            flag = false;
         }
         else
         {
            System.out.println("Invalid input");
            loginScreen(layout);
         }
     }while(flag);    
    }//LoginScreen ends
    
    
    
    //login method to handle the way user wants to proceed
    public static void handleLogin(Layout layout)
    {
     try
      {
         System.out.println();
         //customer id from customer
         System.out.print("Enter user ID: ");
         String uId = kb.next();
         
         //pincode from customer
         System.out.print("Enter password: ");
         String pin = kb.next();      
         
         Connection conn = Connect.getConnection();
         if(conn !=null)
         {
            loadHomePage(conn,uId,pin);
         } 
      }
      catch(SQLException e) 
      {
         System.out.print("Database connection error!! Try again");
         loginScreen(layout);
	   }  
      catch(Exception e)
      {
         System.out.print("Invalid Input!! Try again");
         loginScreen(layout);
      }
    }//handleLogin ends
   
   
   public static void loadHomePage(Connection conn, String uId, String pin)
   {
      Layout layout = new Layout();
      Statement stmt = null;
      String query1 = "SELECT * FROM Customer WHERE userId='"+uId+"' and pincode=SHA('"+pin+"')";
      String query2 = "SELECT accNo, typeId FROM Account WHERE userId='"+uId+"'";
      
      try 
      {
        stmt = conn.createStatement();
        ResultSet rs1 = stmt.executeQuery(query1);
        boolean authUser = false;
        
           while (rs1.next()) 
           {
               String fName = rs1.getString("fName");
               String lName = rs1.getString("lName");
               System.out.println();
               System.out.println("Hello "+ fName + " " + lName+ "!");
               System.out.println("-----------------------");
               authUser = true;
           }

           if(authUser)
           {                    
              System.out.println("\nYour account details.");
              ResultSet rs2 = stmt.executeQuery(query2);
              while(rs2.next())
              {
                  System.out.print(rs2.getInt("accNo")+ "\t");
                  String actype =(rs2.getString("typeId")).equals("s")?"SAVING":"CHECKING";
                  System.out.println(actype);
              }
              
              System.out.println();
              System.out.print("\nEnter the account you want to use: ");
              int accNo = kb.nextInt();
              
              while (true)
               {
                 layout.Navigation();
                 String transType= kb.next();
                 Transaction tb = new Transaction(accNo, transType);
                 String t = tb.handleAction();
                 if (t.equals("0"))
                 {
                   break;
                 }

               }
                uId = "";pin = "";
                System.out.println("Logout successful!");
                System.out.println("---------------------------------------------");
                System.out.print("Startover? Enter [1]- YES [0]- No : ");
                int yes_No = kb.nextInt();
                if (yes_No == 1)
                  loginScreen(layout);
                else
                  layout.exit();
              
           }
           else
           {
               System.out.println("Invalid login. Try again!");
               handleLogin(layout);
           }
      }
      catch (SQLException e ) 
      {
         //JDBCTutorialUtilities.printSQLException(e);
      }
      finally { if (conn != null) {try{conn.close();}catch (SQLException e) { /* ignored */}}}   
   }//loadhomepage ends
   
   
   //method to create new customers
   public static void createNewUser()
   {
   Statement stmt = null;
   Connection conn = null;
   System.out.println("====================================================================================");
   System.out.println("This screen allows you to open a checking account with opening balance of $0.00." );
   System.out.println("Saving account can be opened only in Bank. Provide customer information below.");
   System.out.println("=====================================================================================");
   System.out.println();
   System.out.print("Enter first name: ");
   String fName = kb.next();
   System.out.println();
   System.out.print("Enter last name: ");
   String lName = kb.next();
   System.out.println();
   System.out.print("Enter your phone number[xxx-xxx-xxxx]: ");
   String phoneNo = kb.next();
   System.out.println();
   do{
   System.out.print("Enter user ID: ");
   String userId = kb.next();
   System.out.println();
   System.out.print("Enter password: ");
   String pincode = kb.next();
   System.out.println();

   
   String queryCustInfo = "INSERT INTO Customer(userId, lName, fName, phoneNo, pincode)"
   + " VALUES('"+userId+"','"+lName+"','"+fName+"','"+phoneNo+"', SHA('"+pincode+"'))";
   
   try 
   {
     conn = Connect.getConnection();
     stmt = conn.createStatement();
     int stat = stmt.executeUpdate(queryCustInfo);
     if(stat>0)
     {
         try{Thread.sleep(500);}catch(InterruptedException e){System.out.println(e);}              
         String getActNoQry = "select Max(accNo) from account";
         Statement selectstmt = null;
         Connection conns = null;
         try 
         {
           conns = Connect.getConnection();
           selectstmt = conns.createStatement();
           ResultSet rsel = selectstmt.executeQuery(getActNoQry);
           
           while(rsel.next())
           {
               int newAcc= rsel.getInt("Max(accNo)");
               System.out.println("Account created successfully.");
               System.out.print("Your account number is: "+newAcc);
               //String newUserId= rsel.getString("Max(custId)");
           }                  
            System.out.println(". Your user ID is: "+ userId);
            System.out.println();
            System.out.println("Login using the user ID and password you entered.");
            signupSuccess();
         } 
         catch (SQLException e ) {}
         finally{if (conns != null) {try{conns.close();}catch (SQLException e) {}}}
         
         }
         break;
      } 
      catch (SQLException e ) 
      {
         //System.out.println(e.getMessage());
         System.out.println("User Id exists. Choose another.");
      }
      finally
      {
         if (conn != null) {try{conn.close();}catch (SQLException e) {}}
      }
      }while(true);
   }//createNewUser ends
   
   
   public static void signupSuccess()
   {
      Layout layout = new Layout();
      System.out.println("====================================================================================="); 
      System.out.print("Enter [1] to login and [0] to exit: ");
      int nxtOption = kb.nextInt();
      if(nxtOption==1)
         handleLogin(layout);
      else if(nxtOption==0)
         layout.exit();
      else
      {
        System.out.println("Invalid login!");
        signupSuccess();
      }
   }//signupSuccess end 
 }
