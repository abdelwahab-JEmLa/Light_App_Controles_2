import sqlite3
import json
import sys

sys.stdout.reconfigure(encoding='utf-8')

conn = sqlite3.connect("fb_db")
cursor = conn.cursor()

cursor.execute("SELECT path, value FROM serverCache")
rows = cursor.fetchall()
print(f"Total rows in fb_db cache: {len(rows)}")

for path, value in rows:
    try:
        # Load JSON from bytes
        data = json.loads(value)
        if isinstance(data, dict):
            key_id = data.get("keyID", "")
            if "xp4" in key_id.lower() or "7xp4" in key_id.lower():
                print(f"Path: {path}")
                print(f"KeyID: {key_id}")
                print(f"Parent M2 Client: {data.get('parent_M2Client_KeyID')}")
                print(f"Parent M2 Debug: {data.get('parent_M2Client_DebugInfos')}")
                print(f"All Data: {data}")
    except Exception as e:
        pass

conn.close()
