import sqlite3
import json
import os
from datetime import datetime

def format_val(val):
    if val is None or val == 0 or val == "" or val == "null" or val == 0.0:
        return "-"
    try:
        f_val = float(val)
        return f"{f_val:.2f} دج"
    except:
        return str(val)

def generate_table(rows, cols):
    months = ["Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"]
    table_lines = []
    table_lines.append("| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |")
    table_lines.append("| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |")
    
    if not rows:
        table_lines.append("| - | - | - | - | - | - | - | - | - | - | - | - | - |")
        return "\n".join(table_lines)

    for r in rows:
        row_dict = dict(zip(cols, r))
        key_id = row_dict.get("keyID", "")
        short_id = key_id[-4:] if len(key_id) >= 4 else key_id
        
        ts = row_dict.get("creationTimestamps", 0)
        try:
            dt = datetime.fromtimestamp(ts / 1000.0)
            date_str = f"{dt.day} {months[dt.month - 1]}"
        except:
            date_str = "-"
        heur = row_dict.get("heurDebutInString", "Non Defini")
        date_heure = f"{date_str} \\| {heur}"
        
        etat = row_dict.get("etateActuellementEst", "-")
        
        m_principal_raw = row_dict.get("montant_principale_du_type")
        c_fait_raw = row_dict.get("credit_fait")
        v_fait_raw = row_dict.get("versement_fait")
        
        if etat in ["Credit", "Cette_Transaction_Type_Est_Credit"]:
            montant_principal = format_val(m_principal_raw if m_principal_raw else c_fait_raw)
            versement_fait = format_val(v_fait_raw)
            ancien_credit = format_val(row_dict.get("ancien_credit"))
            nouveau_credit = format_val(row_dict.get("new_credit_apre_tout_fait") if row_dict.get("new_credit_apre_tout_fait") else c_fait_raw)
            credit_cumule = format_val(row_dict.get("sum_De_Credit_Fait") if row_dict.get("sum_De_Credit_Fait") else c_fait_raw)
            versement = format_val(row_dict.get("versement"))
            credit_fait = format_val(c_fait_raw)
            nouvelle_situation = format_val(row_dict.get("new_situation") if row_dict.get("new_situation") else c_fait_raw)
            totale_saved = format_val(row_dict.get("totale_saved"))
        elif etat == "Versemment":
            montant_principal = format_val(m_principal_raw if m_principal_raw else v_fait_raw)
            versement_fait = format_val(v_fait_raw)
            ancien_credit = format_val(row_dict.get("ancien_credit"))
            nouveau_credit = format_val(row_dict.get("new_credit_apre_tout_fait"))
            credit_cumule = format_val(row_dict.get("sum_De_Credit_Fait"))
            versement = format_val(row_dict.get("versement"))
            credit_fait = format_val(c_fait_raw)
            nouvelle_situation = format_val(row_dict.get("new_situation"))
            totale_saved = format_val(row_dict.get("totale_saved"))
        else:
            montant_principal = format_val(m_principal_raw)
            versement_fait = format_val(v_fait_raw)
            ancien_credit = format_val(row_dict.get("ancien_credit"))
            nouveau_credit = format_val(row_dict.get("new_credit_apre_tout_fait"))
            credit_cumule = format_val(row_dict.get("sum_De_Credit_Fait"))
            versement = format_val(row_dict.get("versement"))
            credit_fait = format_val(c_fait_raw)
            nouvelle_situation = format_val(row_dict.get("new_situation"))
            totale_saved = format_val(row_dict.get("totale_saved"))
            
        client_dbg = row_dict.get("parent_M2Client_DebugInfos", "")
        client_code = "-"
        if client_dbg and "[" in client_dbg and "]" in client_dbg:
            client_code = client_dbg.split("[")[-1].split("]")[0]
        else:
            p_key = row_dict.get("parent_M2Client_KeyID", "")
            client_code = p_key[-4:] if len(p_key) >= 4 else p_key
            if not client_code:
                client_code = "-"
            
        row = f"| `{short_id}` | {date_heure} | {etat} | {montant_principal} | {versement_fait} | {ancien_credit} | {nouveau_credit} | {credit_cumule} | {versement} | {credit_fait} | {nouvelle_situation} | {totale_saved} | `{client_code}` |"
        table_lines.append(row)
        
    return "\n".join(table_lines)

def main():
    conn = sqlite3.connect("app_database_temp")
    cursor = conn.cursor()
    
    # Get M8 columns
    cursor.execute("PRAGMA table_info(M8BonVent)")
    cols = [c[1] for c in cursor.fetchall()]
    
    # 1. Search for keyID ending in xp4
    cursor.execute("SELECT * FROM M8BonVent WHERE keyID LIKE '%xp4' ORDER BY creationTimestamps DESC")
    rows_xp4 = cursor.fetchall()
    
    # 2. Search for keyID ending in fqTx (as sister fallback match)
    cursor.execute("SELECT * FROM M8BonVent WHERE keyID LIKE '%fqTx' ORDER BY creationTimestamps DESC")
    rows_fqTx = cursor.fetchall()
    
    conn.close()
    
    table_xp4 = generate_table(rows_xp4, cols)
    table_fqTx = generate_table(rows_fqTx, cols)
    
    timestamp_str = datetime.now().isoformat()
    
    report = f"""# Room Database Query Results

- **Expression**: `room_d .keyID.takeLast(3) == "xp4"`
- **SQL Executed**: `SELECT * FROM M8BonVent WHERE keyID LIKE '%xp4' ORDER BY creationTimestamps DESC`
- **Database**: `app_database_temp`
- **Timestamp**: {timestamp_str}

---

## 1. Query Results Table for `xp4` (Ends with `xp4`)

{table_xp4}

---

## 2. Query Results Table for `fqTx` (Ends with `fqTx` - Sister Fallback Match)

{table_fqTx}
"""
    
    os.makedirs("copy_/room_d", exist_ok=True)
    with open("copy_/room_d/last_room_d.md", "w", encoding="utf-8") as f:
        f.write(report)
        
    print("Report written successfully.")

if __name__ == "__main__":
    main()
