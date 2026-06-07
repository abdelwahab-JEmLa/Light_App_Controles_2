import sqlite3
import json
import sys
import os

sys.stdout.reconfigure(encoding='utf-8')

db_files = ["app_database", "app_database_temp", "fb_db", "fb_db_93f88"]

for db in db_files:
    if not os.path.exists(db):
        continue
    conn = sqlite3.connect(db)
    cursor = conn.cursor()
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table'")
    tables = [r[0] for r in cursor.fetchall()]
    
    for table in tables:
        try:
            cursor.execute(f"PRAGMA table_info([{table}])")
            cols = [c[1] for c in cursor.fetchall()]
            for col in cols:
                cursor.execute(f"SELECT * FROM [{table}] WHERE CAST([{col}] AS TEXT) LIKE '%fqTx%' OR CAST([{col}] AS TEXT) LIKE '%7xp4%' OR CAST([{col}] AS TEXT) LIKE '%xp4%'")
                res = cursor.fetchall()
                if res:
                    print(f"[{db}] Table [{table}], Col [{col}] matches:")
                    for r in res:
                        print("  ", r)
        except Exception:
            pass
    conn.close()
