import json

def main():
    with open("copy_/parsed_bons.json", "r", encoding="utf-8") as f:
        bons = json.load(f)
    client_key = "-OWI8JQlhGjA_HzMCGFD"
    client_bons = [b for b in bons if b.get("parent_M2Client_KeyID") == client_key]
    print(f"Total client bons: {len(client_bons)}")
    states = {}
    for b in client_bons:
        s = b.get("etateActuellementEst")
        states[s] = states.get(s, 0) + 1
    print("States:", states)

if __name__ == "__main__":
    main()
