import sqlite3
import sys

sys.stdout.reconfigure(encoding='utf-8')

conn = sqlite3.connect("fb_db")
cursor = conn.cursor()

cursor.execute("SELECT path FROM serverCache")
paths = [r[0] for r in cursor.fetchall()]

print("Checking fb_db paths for xp4 or 7xp4:")
for p in paths:
    if "xp4" in p or "7xp4" in p:
        print("MATCH PATH:", p)

conn.close()
