import sqlite3
import json

def main():
    conn = sqlite3.connect("app_database")
    conn.row_factory = sqlite3.Row
    cursor = conn.cursor()
    
    try:
        cursor.execute("SELECT * FROM M8BonVent;")
        rows = cursor.fetchall()
        parsed_bons = [dict(row) for row in rows]
        print(f"Found {len(parsed_bons)} M8BonVent records in app_database (Android Studio datas)")
    except Exception as e:
        print("Error querying M8BonVent from app_database:", e)
        parsed_bons = []
        
    # Write to parsed_bons.json
    with open("copy_/parsed_bons.json", "w", encoding="utf-8") as f:
        json.dump(parsed_bons, f, ensure_ascii=False, indent=2)
        
    print(f"Successfully parsed and wrote {len(parsed_bons)} records to copy_/parsed_bons.json")
    
    states = {}
    for bon in parsed_bons:
        state = bon.get("etateActuellementEst")
        states[state] = states.get(state, 0) + 1
    print("States count:", states)
    
    # Print first 5 records with key details
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
