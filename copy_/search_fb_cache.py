import sqlite3
import json
import sys

sys.stdout.reconfigure(encoding='utf-8')

conn = sqlite3.connect("fb_db_93f88")
cursor = conn.cursor()

cursor.execute("SELECT path, value FROM serverCache")
rows = cursor.fetchall()
found = 0
for path, value in rows:
    # Decode BLOB
    try:
        val_str = value.decode('utf-8')
        if 'xp4' in val_str or 'xp4' in path:
            print(f"Path: {path}")
            print(f"Value: {val_str}")
            found += 1
    except Exception as e:
        pass

print(f"Search done. Found {found} matching paths/values.")
conn.close()
