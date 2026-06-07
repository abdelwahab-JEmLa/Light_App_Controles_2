import sqlite3
import sys

sys.stdout.reconfigure(encoding='utf-8')

conn = sqlite3.connect("app_database_temp")
cursor = conn.cursor()

cursor.execute("SELECT keyID, nom FROM M2Client")
rows = cursor.fetchall()
print(f"Total M2 clients: {len(rows)}")
for r in rows:
    key = r[0]
    name = r[1]
    # Check if 'xp' or '7x' or 'p4' is in key or name
    if any(x in str(key).lower() or x in str(name).lower() for x in ['xp', '7x', 'p4']):
        print("M2 MATCH:", r)

conn.close()
