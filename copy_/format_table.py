import json
from datetime import datetime

def format_val(val):
    if val is None or val == 0 or val == "" or val == "null" or val == 0.0:
        return "-"
    try:
        f_val = float(val)
        return f"{f_val:.2f} دج"
    except:
        return str(val)

def main():
    with open("copy_/parsed_bons.json", "r", encoding="utf-8") as f:
        bons = json.load(f)
        
    client_key = "-OWI8JQlhGjA_HzMCGFD"
    CREDIT_VERSEMENT_STATES = {
        "COMMANDE_LIVRAI",
        "Versemment",
        "Credit",
        "Cette_Transaction_Type_Est_Credit",
        "Demande_Versemet",
        "New_Situation_Credit"
    }
    
    client_bons = [
        b for b in bons 
        if b.get("parent_M2Client_KeyID") == client_key 
        and b.get("etateActuellementEst") in CREDIT_VERSEMENT_STATES
    ]
    
    # Sort by creationTimestamps descending
    client_bons.sort(key=lambda x: x.get("creationTimestamps", 0), reverse=True)
    
    months = ["Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"]
    
    lines = []
    lines.append("| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |")
    lines.append("| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |")
    
    for b in client_bons:
        key_id = b.get("keyID", "")
        short_id = key_id[-4:] if len(key_id) >= 4 else key_id
        
        ts = b.get("creationTimestamps", 0)
        dt = datetime.fromtimestamp(ts / 1000.0)
        date_str = f"{dt.day} {months[dt.month - 1]}"
        heur = b.get("heurDebutInString", "Non Defini")
        date_heure = f"{date_str} \\| {heur}"
        
        etat = b.get("etateActuellementEst", "-")
        
        # Smart computation
        m_principal_raw = b.get("montant_principale_du_type")
        c_fait_raw = b.get("credit_fait")
        v_fait_raw = b.get("versement_fait")
        
        if etat in ["Credit", "Cette_Transaction_Type_Est_Credit"]:
            montant_principal = format_val(m_principal_raw if m_principal_raw else c_fait_raw)
            versement_fait = format_val(v_fait_raw)
            ancien_credit = format_val(b.get("ancien_credit"))
            nouveau_credit = format_val(b.get("new_credit_apre_tout_fait") if b.get("new_credit_apre_tout_fait") else c_fait_raw)
            credit_cumule = format_val(b.get("sum_De_Credit_Fait") if b.get("sum_De_Credit_Fait") else c_fait_raw)
            versement = format_val(b.get("versement"))
            credit_fait = format_val(c_fait_raw)
            nouvelle_situation = format_val(b.get("new_situation") if b.get("new_situation") else c_fait_raw)
            totale_saved = format_val(b.get("totale_saved"))
        elif etat == "Versemment":
            montant_principal = format_val(m_principal_raw if m_principal_raw else v_fait_raw)
            versement_fait = format_val(v_fait_raw)
            ancien_credit = format_val(b.get("ancien_credit"))
            nouveau_credit = format_val(b.get("new_credit_apre_tout_fait"))
            credit_cumule = format_val(b.get("sum_De_Credit_Fait"))
            versement = format_val(b.get("versement"))
            credit_fait = format_val(c_fait_raw)
            nouvelle_situation = format_val(b.get("new_situation"))
            totale_saved = format_val(b.get("totale_saved"))
        else:
            montant_principal = format_val(m_principal_raw)
            versement_fait = format_val(v_fait_raw)
            ancien_credit = format_val(b.get("ancien_credit"))
            nouveau_credit = format_val(b.get("new_credit_apre_tout_fait"))
            credit_cumule = format_val(b.get("sum_De_Credit_Fait"))
            versement = format_val(b.get("versement"))
            credit_fait = format_val(c_fait_raw)
            nouvelle_situation = format_val(b.get("new_situation"))
            totale_saved = format_val(b.get("totale_saved"))
        
        client_dbg = b.get("parent_M2Client_DebugInfos", "")
        # extract GFD or use last 4 chars
        client_code = "GFD"
        if "[" in client_dbg and "]" in client_dbg:
            client_code = client_dbg.split("[")[-1].split("]")[0]
        else:
            p_key = b.get("parent_M2Client_KeyID", "")
            client_code = p_key[-4:] if len(p_key) >= 4 else p_key
            
        row = f"| `{short_id}` | {date_heure} | {etat} | {montant_principal} | {versement_fait} | {ancien_credit} | {nouveau_credit} | {credit_cumule} | {versement} | {credit_fait} | {nouvelle_situation} | {totale_saved} | `{client_code}` |"
        lines.append(row)
        
    markdown_table = "\n".join(lines)
    
    with open("copy_/formatted_table.md", "w", encoding="utf-8") as out:
        out.write(markdown_table)
        
    print(f"Generated computed table with {len(client_bons)} rows.")

if __name__ == "__main__":
    main()
