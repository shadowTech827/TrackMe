package trackme.test.com.myapplication.datamanager.database;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public abstract class AbstractDatabaseManager<T> {

    private static String TAG = AbstractDatabaseManager.class.getSimpleName();
    protected SQLiteDatabase database;
    Cursor cursor;
    protected static final String KEY_ID = "key_id";


    public AbstractDatabaseManager(SQLiteDatabase database) {
        this.database = database;
    }

    public AbstractDatabaseManager() {

    }

    public abstract ContentValues generateContentValuesFromObject(T t);

    public boolean saveAll(AbstractDatabaseManager abstractDM, String tableName, List<T> entity) {
        database = DatabaseManager.getInstance().openDatabase();
        if (entity == null) {
            return false;
        }

        database.beginTransaction();
        long result = 0;
        for (int i = 0; i < entity.size(); i++) {
            try {
                result = database.insertOrThrow(tableName, null, abstractDM.generateContentValuesFromObject(entity.get(i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        DatabaseManager.getInstance().closeDatabase();

        return result != -1;
    }

    public boolean save(String tableName, ContentValues contentValues) {
        database = DatabaseManager.getInstance().openDatabase();

        if (contentValues == null)
            return false;
        if (!database.isOpen()) {
            database = DatabaseManager.getInstance().openDatabase();
        }

        long result = database.insertOrThrow(tableName, null, contentValues);
        DatabaseManager.getInstance().closeDatabase();

        return result > 0;
    }


    public boolean delete(String table, String whereClause, String[] whereArgs) {
        database = DatabaseManager.getInstance().openDatabase();

        long result = database.delete(table, whereClause, whereArgs);
        DatabaseManager.getInstance().closeDatabase();

        if (result > 0)
            return true;
        else
            return false;
    }

    public boolean update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        database = DatabaseManager.getInstance().openDatabase();

        long rows = 0;
        if (values == null)
            return false;
        try {
            rows = database.update(table, values, whereClause, whereArgs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        DatabaseManager.getInstance().closeDatabase();

        return rows != -1;
    }

    /*public boolean update(String rawQuery) {
        if (rawQuery == null)
            return false;

        Cursor c = database.rawQuery(rawQuery, null);

        c.moveToFirst();
        c.close();
        return true;

    }*/

    public List load(AbstractDatabaseManager abstractDM, String table, String[] columns, String selection,
                     String[] selectionArgs, String groupBy, String having, String orderBy) {
        database = DatabaseManager.getInstance().openDatabase();

        Cursor cursor = database.query(table, columns, selection,
                selectionArgs, groupBy, having, orderBy);
        List<Object> orders = new ArrayList();

        if (cursor != null && cursor.moveToFirst()) {

            while (!cursor.isAfterLast()) {

                orders.add(abstractDM.generateObjectFromCursor(cursor));
                cursor.moveToNext();

            }
            cursor.close();
        }
        DatabaseManager.getInstance().closeDatabase();

        return orders;


    }

    public List load(AbstractDatabaseManager abstractDM, String query) {
        database = DatabaseManager.getInstance().openDatabase();

        Cursor cursor = database.rawQuery(query, null);
        List<Object> dataObject = new ArrayList();

        if (cursor != null && cursor.moveToFirst()) {

            while (!cursor.isAfterLast()) {

                dataObject.add(abstractDM.generateObjectFromCursor(cursor));
                cursor.moveToNext();

            }
            cursor.close();
        }
        DatabaseManager.getInstance().closeDatabase();

        return dataObject;

    }

    public T loadRecord(AbstractDatabaseManager abstractDM, String query) {
        database = DatabaseManager.getInstance().openDatabase();

        try {
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {

                return (T) abstractDM.generateObjectFromCursor(cursor);

            }
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
        DatabaseManager.getInstance().closeDatabase();

        return null;

    }

    public List<Object> loadAll(AbstractDatabaseManager dbManager, final String query) {

        database = DatabaseManager.getInstance().openDatabase();

        Cursor cursor = database.rawQuery(query, null);
        List<Object> data = new LinkedList<>();

        if (cursor != null && cursor.moveToFirst()) {

            while (!cursor.isAfterLast()) {

                data.add(dbManager.generateObjectFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        DatabaseManager.getInstance().closeDatabase();


        return data;
    }

    public List<Object> loadColumn(String query, String column) {
        database = DatabaseManager.getInstance().openDatabase();

        Cursor cursor = database.rawQuery(query, null);
        List<Object> data = new ArrayList();

        if (cursor != null && cursor.moveToFirst()) {

            while (!cursor.isAfterLast()) {
                switch (cursor.getType(cursor.getColumnIndex(column))) {
                    case Cursor.FIELD_TYPE_NULL:
                        data.add(null);
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        data.add(cursor.getLong(cursor.getColumnIndex(column)));
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        data.add(cursor.getBlob(cursor.getColumnIndex(column)));

                        break;

                    case Cursor.FIELD_TYPE_FLOAT:
                        data.add(cursor.getFloat(cursor.getColumnIndex(column)));

                        break;

                    case Cursor.FIELD_TYPE_STRING:
                        data.add(cursor.getString(cursor.getColumnIndex(column)));
                        break;

                    default:
                        data.add(cursor.getString(cursor.getColumnIndex(column)));
                        break;

                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        DatabaseManager.getInstance().closeDatabase();

        return data;

    }

    public abstract T generateObjectFromCursor(Cursor c);

    public long count(final String TABLE_NAME) {
        database = DatabaseManager.getInstance().openDatabase();
        long count = DatabaseUtils.queryNumEntries(database, TABLE_NAME);
        DatabaseManager.getInstance().closeDatabase();
        return count;

    }
}