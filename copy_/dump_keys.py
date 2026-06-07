import sqlite3
import sys

sys.stdout.reconfigure(encoding='utf-8')

conn = sqlite3.connect("app_database_temp")
cursor = conn.cursor()

cursor.execute("SELECT keyID, parent_M2Client_KeyID, parent_M2Client_DebugInfos FROM M8BonVent")
rows = cursor.fetchall()
print(f"Total M8 rows: {len(rows)}")
for r in rows:
    # Check if keyID has 'xp' or '7x' or anything
    key = r[0]
    if 'xp' in key.lower() or '7x' in key.lower() or 'p4' in key.lower():
        print("M8 MATCH:", r)

conn.close()
