// DatabaseConnector.java
// Provides easy connection and creation of UserContacts database.
package com.example.cmp354_covidtracer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseConnector 
{
   // database name
   private static final String DATABASE_NAME = "Exposures";
   private SQLiteDatabase database; // TS: to run SQL commands 
   private DatabaseOpenHelper databaseOpenHelper; // TS: create or open the database

   // public constructor for DatabaseConnector
   public DatabaseConnector(Context context) 
   {
      // create a new DatabaseOpenHelper
      databaseOpenHelper = 
         new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
   } // end DatabaseConnector constructor

   // open the database connection
   public void open() throws SQLException 
   {
      // create or open a database for reading/writing
      database = databaseOpenHelper.getWritableDatabase();//TS: at the first call, onCreate is called
   } // end method open

   // close the database connection
   public void close() 
   {
      if (database != null)
         database.close(); // close the database connection
   } // end method close

   // inserts a new contact in the database
   public void insertExposure(Exposure exposure)
   {
      ContentValues newExposure = new ContentValues();
      newExposure.put("posId", exposure.getPositiveId());
      newExposure.put("userId", exposure.getUserId());
      newExposure.put("lat", exposure.getLocation().getLat());
      newExposure.put("lng", exposure.getLocation().getLng());
      newExposure.put("timestamp", exposure.getLocation().getTs());

      open(); // open the database
      database.insert("exposures", null, newExposure);
      close(); // close the database
   } // end method insertContact

   public void updateExposure(long id, Exposure exposure)
   {
      ContentValues updatedExposure = new ContentValues();

      updatedExposure.put("posId", exposure.getPositiveId());
      updatedExposure.put("userId", exposure.getUserId());
      updatedExposure.put("lat", exposure.getLocation().getLat());
      updatedExposure.put("lng", exposure.getLocation().getLng());
      updatedExposure.put("timestamp", exposure.getLocation().getTs());

      open(); // open the database
      database.update("exposures", updatedExposure, "_id=" + id, null);
      close(); // close the database
   } // end method updateContact

   // return a Cursor with all contact information in the database
   public Cursor getAllExposuresOfUser(String userIdToSelect)
   {
	  //return database.query("contacts", new String[] {"_id", "name"}, 
	  //	         null, null, null, null, "name"/*order by*/);
      return database.rawQuery("SELECT * FROM exposures WHERE userId = '" + userIdToSelect +"'" , null);

   } // end method getAllContacts

   public Cursor getExposureByUsersAndTimestamp(String userIdToSelect, String positiveIdToSelect, long timestampToSelect)
   {
      //return database.query("contacts", new String[] {"_id", "name"},
      //	         null, null, null, null, "name"/*order by*/);
      return database.rawQuery("SELECT * FROM exposures WHERE userId = '" + userIdToSelect  + "' AND posId = '" + positiveIdToSelect + "' AND timestamp = " + timestampToSelect, null);

   } // end method getAllContacts

   // get a Cursor containing all information about the contact specified
   // by the given id
   public Cursor getOneExposure(long id)
   {
      //return database.query(
      //   "contacts", null/*get all fields*/, "_id=" + id /*selection*/, null, null, null, null);
	  //TS: OR
	  return database.rawQuery("SELECT * FROM exposures WHERE _id = " + String.valueOf(id)  , null);
	  
   } // end method getOnContact

   // delete the contact specified by the given String name
   public void deleteExposure(long id)
   {
      open(); 
      database.delete("exposures", "_id=" + id, null);
      close();
   }
   
   private class DatabaseOpenHelper extends SQLiteOpenHelper 
   {
      // public constructor
      public DatabaseOpenHelper(Context context, String name,
         CursorFactory factory, int version) 
      {
         super(context, name, factory, version);
      } // end DatabaseOpenHelper constructor

      // creates the contacts table when the database is created
      // TS: this is called from  open()->getWritableDatabase(). Only if the database does not exist
      @Override
      public void onCreate(SQLiteDatabase db) 
      {
         // query to create a new table named contacts
         String createQuery = "CREATE TABLE exposures" +
            "(_id integer primary key autoincrement," +
            "posId TEXT, userId TEXT, lat DOUBLE," +
            "lng DOUBLE, timestamp long);";

         db.execSQL(createQuery); // execute the query
      } // end method onCreate

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, 
          int newVersion) 
      {
      } // end method onUpgrade
   } // end class DatabaseOpenHelper
} // end class DatabaseConnector


/**************************************************************************
 * (C) Copyright by Deitel & Associates, Inc. and                         *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 **************************************************************************/
