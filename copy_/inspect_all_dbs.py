import sqlite3
import os
import sys

sys.stdout.reconfigure(encoding='utf-8')

db_files = [f for f in os.listdir(".") if os.path.isfile(f) and not f.endswith(".py") and not f.endswith(".png") and not f.endswith(".xml") and not f.endswith(".md") and not f.endswith(".kts") and not f.endswith(".properties") and not f.endswith("bat") and not f.startswith(".")]

print("Found DB files in root:", db_files)

for db_file in db_files:
    try:
        conn = sqlite3.connect(db_file)
        cursor = conn.cursor()
        cursor.execute("SELECT name FROM sqlite_master WHERE type='table'")
        tables = [r[0] for r in cursor.fetchall()]
        for table in tables:
            cursor.execute(f"PRAGMA table_info({table})")
            cols = [c[1] for c in cursor.fetchall()]
            for col in cols:
                try:
                    cursor.execute(f"SELECT * FROM {table} WHERE {col} LIKE '%xp4%'")
                    res = cursor.fetchall()
                    if res:
                        print(f"[{db_file}] Table {table}, Col {col} contains xp4:")
                        for r in res:
                            print(r)
                except Exception:
                    pass
        conn.close()
    except Exception as e:
        print(f"Error reading {db_file}: {e}")
