import sqlite3
import sys

sys.stdout.reconfigure(encoding='utf-8')

conn = sqlite3.connect("app_database_temp")
cursor = conn.cursor()

# Find all tables
cursor.execute("SELECT name FROM sqlite_master WHERE type='table'")
tables = [r[0] for r in cursor.fetchall()]

for table in tables:
    try:
        cursor.execute(f"PRAGMA table_info({table})")
        cols = [c[1] for c in cursor.fetchall()]
        if 'keyID' in cols:
            cursor.execute(f"SELECT * FROM {table} WHERE keyID LIKE '%xp4'")
            res = cursor.fetchall()
            if res:
                print(f"Table {table} has matching rows for keyID ending in xp4:")
                for r in res:
                    # Zip with columns
                    row_dict = dict(zip(cols, r))
                    print(row_dict)
    except Exception as e:
        print(f"Error checking table {table}: {e}")

conn.close()
