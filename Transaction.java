//Author DA
//Transaction class basically for all the transactions in atm

import java.sql.*;
import java.math.BigDecimal;
import java.text.*;
import java.util.*;
import java.sql.Connection;

public class Transaction
{
   public static Scanner kb = new Scanner(System.in);

   //instance variable
   public int accNo;
   public String transType;
      
   //constructors
   public Transaction(int accNo, String transType)
   {
      this.accNo = accNo;
      this.transType = transType;
   }
   
   public String handleAction()
   {
      if (this.transType.equals("c"))
            showBalance();
      else if (this.transType.equals("w"))
            withdraw();
      else if (this.transType.equals("d"))
            deposit();
      else if (this.transType.equals("t"))
            viewTransactions();
      else if(this.transType.equals("s"))
            viewStatement();      
      boolean exit = false;
      String decision="";
      do{
         System.out.print("\n Press '1'- [GO BACK TO MENU] '0'- [LOGOUT]: ");
         decision = kb.next();
         
         if (decision.equals("1") || decision.equals("0"))
         {
            exit = true;
         }
         else
         {
            exit = false;
            System.out.print("Invalid input. ");
         }
      }while(!exit);
      
      return decision;
   }//handleAction ends
   
   //Displays the balance for the account
   public void showBalance()
   {
      getBalance();
      insertIntoTransaction(0);
   }//showBalance ends
   
   
   //helper method for balance check method
   public void getBalance()
   {         
      BigDecimal in = new BigDecimal(0);
      Statement stmt = null;
      String query = "select balance from Account where accNo="+this.accNo+"";

      Connection conn = null;
      try 
      {
        conn = Connect.getConnection();
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        while(rs.next())
        {
            in = new BigDecimal(rs.getBigDecimal("balance")+"");
            System.out.println("Current Balance in account [" + this.accNo + "] : $"+ in);
        }
      } 
      catch (SQLException e ) {}
      finally{if (conn != null) {try{conn.close();}catch (SQLException e) {}}}
   }//getBalance ends
   
   
   
   //lets the user withdraw cash
   public void withdraw()
   {
      Scanner kb = new Scanner(System.in);
      boolean success = false;
         System.out.println("|\t$20\t|\t$50\t|\t$100\t|\t$200\t|\t$500\t|");
         System.out.println();         
         System.out.print("Enter the amount to withdraw: $");
         double amount = kb.nextDouble();
         
      if (sufficientBalChk(amount).equals("good"))
         {
            withdrawOrDeposit(amount);
            System.out.println("Withdraw successful!\n");
            insertIntoTransaction(amount);
         } 
      else if (sufficientBalChk(amount).equals("zero"))
         {
            System.out.println("Failed. You have $0.0 in your account.");
         }
      else
         {
            System.out.println("Failed. Not sufficient balance to withdraw/balance after withdraw less than minimum balance.");  
         }
      
   }//withdraw ends
   
   
   
   //checks if the amount entered by user is more than the mimimum requirements of account type
   public String sufficientBalChk(double amnt)
   {
      Statement stmt = null;
      Connection conn = null;
      String chk = "";
      String query1 = "select * from Account where accNo="+this.accNo+"";

      try 
      {
        conn = Connect.getConnection();
        stmt = conn.createStatement();
        ResultSet rs1 = stmt.executeQuery(query1);
        String aType= "";
        double bal=0;
        while(rs1.next())
        {
            aType = rs1.getString("typeId");
            bal = rs1.getDouble("balance");
        } 
        
        double mBal = 0;
        String query2 = "select * from AccountType where typeId='"+aType+"'";
        ResultSet rs2 = stmt.executeQuery(query2);
        while(rs2.next())
        {
            mBal = rs2.getDouble("minBalance");
        }
         
            if(bal >= (amnt + mBal))
               chk = "good";
            else if(bal == 0.0)
               chk = "zero";
            else
               chk = "nogood";  
      
        conn.close();
      } 
      catch (SQLException e ) 
      {
         System.out.println(e.getMessage());
      }
      finally
      {
         if (conn != null) {try{conn.close();}catch (SQLException e) {}}
      }
      return chk;
   }//sufficientBalChk ends
   
   
   
   //lets the user to make deposit into the specified account type
   public void deposit()
   {
      boolean success = false;
      System.out.print("Enter the amount to deposit: $");
      double amount = kb.nextDouble();
      withdrawOrDeposit(amount);
      System.out.println("$"+ amount + " deposited.\n");
      insertIntoTransaction(amount);
   }//deposit ends
   
   
   
   //helper method to update the balance in Account table depending upon deposit or withdrawl
   public void withdrawOrDeposit(double amount)
   {
         Statement stmt = null;
         Connection conn = null;
         try 
         {
           conn = Connect.getConnection();
           stmt = conn.createStatement();
           String queryWithdraw = "UPDATE Account SET Balance = Balance - "+amount+" WHERE accNo = "+this.accNo+"";
           String queryDeposit = "UPDATE Account SET Balance = Balance + "+amount+" WHERE accNo = "+this.accNo+"";
           BigDecimal newBal;
           int stat = 0;
           
           if (this.transType.equals("w"))
              stat = stmt.executeUpdate(queryWithdraw);
           else if (this.transType.equals("d")){
              stat = stmt.executeUpdate(queryDeposit);
           } 
           
        } 
        catch (SQLException e ) 
        {
            System.out.println(e.getMessage());
        }
        finally
        {
            if (conn != null) {try{conn.close();}catch (SQLException e) {}}
        }     
   }//withdrawOrDeposit
   
   
   
   //for every transaction the user does, the activity is inserted into Trans table
   public void insertIntoTransaction(double amount)//(BigDecimal amount)
   {
      String qry = "";
      switch(this.transType.toLowerCase())
      {
         case "c":
            {
               qry = "INSERT INTO Trans (transDate, accNo, transType, amount)"+
                        "VALUES (NOW(), "+this.accNo+", 'Balance Inquiry', "+amount+")";
               performInsert(qry);         
               break;
            }
         case "w": 
           {
               qry = "INSERT INTO Trans (transDate, accNo, transType, amount)"+
                        "VALUES (NOW(), "+this.accNo+", 'Withdraw', "+amount+")";
               performInsert(qry);
               break;
            }
         case "d":
           {
               qry = "INSERT INTO Trans (transDate, accNo, transType, amount)"+
                        "VALUES (NOW(), "+this.accNo+", 'Deposit', "+amount+")";
               performInsert(qry);
               break;
            }
          default:
            break;           
      }
   }//insertIntoTransaction ends
   
   
   
   //helper method for insertIntoTransaction method for updates/insert
   public void performInsert(String query)
   {
      Statement stmt = null;
      Connection conn = null;
      try 
      {
        conn = Connect.getConnection();
        stmt = conn.createStatement();
        int stat = stmt.executeUpdate(query);
      } 
      catch (SQLException e ) 
      {
         System.out.println(e.getMessage());
      }
      finally
      {
         if (conn != null) {try{conn.close();}catch (SQLException e) {}}
      }
   }//performInsert ends
   
   
   
   //lets the user to view their transaction details
   public void viewTransactions()
   {
      System.out.println("Transactions details for account["+this.accNo+"]");
      System.out.println("----------------------------------------------------------");
      System.out.println("|\t Id  |\t   Type   \t| \t Amount  \t|\t   Date  \t\t|");
      System.out.println("----------------------------------------------------------");

      String query = "SELECT * FROM Trans WHERE accNo="+this.accNo;
      Statement stmt = null;
      Connection conn = null;
      try 
      {
        conn = Connect.getConnection();
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while(rs.next())
        {
            System.out.print("|\t"+rs.getInt("transId")+"\t");
            String tType = (rs.getString("transType").equals("Withdraw"))||(
                                rs.getString("transType").equals("Deposit")) ?
                                rs.getString("transType")+"\t\t\t\t":rs.getString("transType")+"\t";               
            System.out.print(tType);
            System.out.print("$"+rs.getDouble("amount")+"\t");
            System.out.println("\t\t\t "+rs.getDate("transDate")+"\t\t|");
        }
      System.out.println("----------------------------------------------------------");

      } 
      catch (SQLException e) 
      {
         System.out.println(e.getMessage());
      }
      finally
      {
         if (conn != null) {try{conn.close();}catch (SQLException e) {}}
      }
   }//viewTransactions ends
   
   
   
   //gives a statement of the customer, account and balance details till date.
   public void viewStatement()
   {
      System.out.println("Statement for account["+this.accNo+"]");
      
      String query = "SELECT * FROM BankStatement WHERE account ="+this.accNo;
      Statement stmt = null;
      Connection conn = null;
      try 
      {
        conn = Connect.getConnection();
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        System.out.println("==================================");

        while(rs.next())
        {
            System.out.println("\tFirst Name   :\t "+rs.getString("fName")+"\t\t\t\t");
            System.out.println("----------------------------------");
            System.out.println("\tLast Name    :\t "+rs.getString("lName")+"\t\t\t\t");
            System.out.println("----------------------------------");
            System.out.println("\tPhone No     :\t"+rs.getString("phoneNo")+"\t");
            System.out.println("----------------------------------");
            System.out.println("\tUser ID      :\t"+rs.getString("userId")+"\t\t\t\t");
            System.out.println("----------------------------------");
            System.out.println("\tAccount No   :\t"+rs.getInt("account")+"\t\t\t\t");
            System.out.println("----------------------------------");
            System.out.println("\tAccount Type :\t"+rs.getString("accType")+"\t\t\t\t\t");
            System.out.println("----------------------------------");
            System.out.println("\tBalance      :\t$"+rs.getDouble("balance")+"\t\t\t\t");

        }
        System.out.println("==================================");

      } 
      catch (SQLException e) 
      {
         System.out.println(e.getMessage());
      }
      finally
      {
         if (conn != null) {try{conn.close();}catch (SQLException e) {}}
      }
   }//viewStatement ends    
     
}//Transaction class ends
