import sqlite3
import json

def main():
    conn = sqlite3.connect("fb_db_93f88")
    cursor = conn.cursor()
    cursor.execute("SELECT path, value FROM serverCache WHERE path LIKE '%M08BonVent%';")
    rows = cursor.fetchall()
    print(f"Found {len(rows)} M08BonVent records in fb_db_93f88")
    
    parsed_bons = []
    for path, val_bytes in rows:
        try:
            val_str = val_bytes.decode('utf-8')
            data = json.loads(val_str)
            if data is not None:
                parsed_bons.append(data)
        except Exception as e:
            pass
            
    print(f"Successfully parsed {len(parsed_bons)} non-null records.")
    
    with open("copy_/parsed_bons.json", "w", encoding="utf-8") as f:
        json.dump(parsed_bons, f, ensure_ascii=False, indent=2)
        
    states = {}
    for bon in parsed_bons:
        state = bon.get("etateActuellementEst")
        states[state] = states.get(state, 0) + 1
    print("States count:", states)
    
    # Print the first 5 records with key details
    for i, bon in enumerate(parsed_bons[:5]):
        print(f"\nBon {i+1}:")
        print(f"  keyID: {bon.get('keyID')}")
        print(f"  creationTimestamps: {bon.get('creationTimestamps')}")
        print(f"  etateActuellementEst: {bon.get('etateActuellementEst')}")
        print(f"  montant_principale_du_type: {bon.get('montant_principale_du_type')}")
        print(f"  versement_fait: {bon.get('versement_fait')}")
        print(f"  ancien_credit: {bon.get('ancien_credit')}")
        print(f"  new_credit_apre_tout_fait: {bon.get('new_credit_apre_tout_fait')}")
        print(f"  sum_De_Credit_Fait: {bon.get('sum_De_Credit_Fait')}")
        print(f"  versement: {bon.get('versement')}")
        print(f"  credit_fait: {bon.get('credit_fait')}")
        print(f"  new_situation: {bon.get('new_situation')}")
        print(f"  totale_saved: {bon.get('totale_saved')}")
        print(f"  parent_M2Client_KeyID: {bon.get('parent_M2Client_KeyID')}")
        
    conn.close()

if __name__ == "__main__":
    main()
