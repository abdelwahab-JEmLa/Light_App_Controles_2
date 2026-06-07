import sqlite3
import sys

sys.stdout.reconfigure(encoding='utf-8')

conn = sqlite3.connect("app_database_temp")
cursor = conn.cursor()

# Find cols of M2Client
cursor.execute("PRAGMA table_info(M2Client)")
m2_cols = [c[1] for c in cursor.fetchall()]
print("M2Client columns:", m2_cols)

# Find cols of M8BonVent
cursor.execute("PRAGMA table_info(M8BonVent)")
m8_cols = [c[1] for c in cursor.fetchall()]
print("M8BonVent columns:", m8_cols)

print("\n--- Searching M2Client for keys ending in xp4 ---")
cursor.execute("SELECT * FROM M2Client")
for r in cursor.fetchall():
    row_dict = dict(zip(m2_cols, r))
    key = row_dict.get('keyID', '')
    if 'xp4' in key or '7xp4' in key:
        print("M2 Match:", row_dict)

print("\n--- Searching M8BonVent for keys ending in xp4 ---")
cursor.execute("SELECT * FROM M8BonVent")
for r in cursor.fetchall():
    row_dict = dict(zip(m8_cols, r))
    key = row_dict.get('keyID', '')
    if 'xp4' in key or '7xp4' in key:
        print("M8 Match:", row_dict)

conn.close()
