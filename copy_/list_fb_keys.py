import sqlite3
import json
import sys

sys.stdout.reconfigure(encoding='utf-8')

conn = sqlite3.connect("fb_db_93f88")
cursor = conn.cursor()

cursor.execute("SELECT path FROM serverCache")
paths = [r[0] for r in cursor.fetchall()]

m8_paths = [p for p in paths if "M08BonVent" in p]
m2_paths = [p for p in paths if "M2Client" in p or "Client" in p]

print(f"M08BonVent paths: {len(m8_paths)}")
for p in m8_paths[:10]:
    print("  ", p)

print(f"M2Client paths: {len(m2_paths)}")
for p in m2_paths[:10]:
    print("  ", p)

print("\nChecking if any path contains xp4 or 7xp4:")
for p in paths:
    if "xp4" in p or "7xp4" in p:
        print("MATCH PATH:", p)

conn.close()
