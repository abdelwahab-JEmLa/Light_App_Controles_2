import os
import re
import zipfile

def zip_session_files():
    hist_path = r"C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_context\copy_skill\references\hist_copie.md"
    if not os.path.exists(hist_path):
        print("ERROR: hist_copie.md not found.")
        return

    paths = []
    with open(hist_path, 'r', encoding='utf-8') as f:
        for line in f:
            match = re.search(r'file:///(.*?)\)', line)
            if match:
                path = match.group(1).replace('%20', ' ')
                path = os.path.normpath(path)
                if os.path.exists(path):
                    paths.append(path)

    if not paths:
        print("ERROR: No valid files found to zip.")
        return

    # Find the session directory name
    session_dir = None
    for p in paths:
        if "historique_explication" in p:
            parts = p.split(os.sep)
            for i, part in enumerate(parts):
                if part == "historique_explication" and i + 1 < len(parts):
                    session_dir = parts[i + 1]
                    break
            if session_dir:
                break

    if not session_dir:
        session_dir = "session_archive"

    # Zip output path
    dest_dir = os.path.dirname(paths[0]) if "historique_explication" in paths[0] else os.path.dirname(hist_path)
    zip_name = f"{session_dir}.zip"
    zip_path = os.path.join(dest_dir, zip_name)

    # Create the zip file
    with zipfile.ZipFile(zip_path, 'w', zipfile.ZIP_DEFLATED) as zip_file:
        for file_path in paths:
            arcname = os.path.basename(file_path)
            # If the file is inside files_edited, keep that subfolder structure in the ZIP for clarity
            if "files_edited" in file_path:
                arcname = os.path.join("files_edited", arcname)
            zip_file.write(file_path, arcname)

    print(f"SUCCESS:{zip_path}")

if __name__ == "__main__":
    zip_session_files()
