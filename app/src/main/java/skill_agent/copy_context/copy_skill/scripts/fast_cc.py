import os
import ctypes
import re
import struct

def set_clipboard_files(files):
    CF_HDROP = 15
    CF_UNICODETEXT = 13
    user32 = ctypes.windll.user32
    kernel32 = ctypes.windll.kernel32

    kernel32.GlobalAlloc.argtypes = [ctypes.c_uint, ctypes.c_size_t]
    kernel32.GlobalAlloc.restype = ctypes.c_void_p
    kernel32.GlobalLock.argtypes = [ctypes.c_void_p]
    kernel32.GlobalLock.restype = ctypes.c_void_p
    kernel32.GlobalUnlock.argtypes = [ctypes.c_void_p]
    user32.SetClipboardData.argtypes = [ctypes.c_uint, ctypes.c_void_p]
    user32.SetClipboardData.restype = ctypes.c_void_p

    # Prepare file references (CF_HDROP)
    pFiles = 20
    header = struct.pack("IIIII", pFiles, 0, 0, 0, 1)
    file_strings = "\0".join(files) + "\0\0"
    hdrop_data = header + file_strings.encode("utf-16le")

    # Prepare text content (CF_UNICODETEXT) from context_agy.md if present
    text_data = None
    context_file = next((f for f in files if os.path.basename(f) == "context_agy.md"), None)
    if context_file and os.path.exists(context_file):
        try:
            with open(context_file, 'r', encoding='utf-8') as f:
                text_content = f.read()
            text_data = (text_content + "\0").encode("utf-16le")
        except Exception as e:
            print(f"Error reading context file: {e}")

    import time
    opened = False
    for _ in range(20):
        if user32.OpenClipboard(0):
            opened = True
            break
        time.sleep(0.05)
        
    if not opened:
        print("ERROR: Clipboard locked by another process. Retrying failed.")
        return

    user32.EmptyClipboard()

    # Write files (CF_HDROP)
    hGlobalMemFiles = kernel32.GlobalAlloc(0x0042, len(hdrop_data))
    lpGlobalMemFiles = kernel32.GlobalLock(hGlobalMemFiles)
    ctypes.memmove(lpGlobalMemFiles, hdrop_data, len(hdrop_data))
    kernel32.GlobalUnlock(hGlobalMemFiles)
    user32.SetClipboardData(CF_HDROP, hGlobalMemFiles)

    # Write text (CF_UNICODETEXT)
    if text_data:
        hGlobalMemText = kernel32.GlobalAlloc(0x0042, len(text_data))
        lpGlobalMemText = kernel32.GlobalLock(hGlobalMemText)
        ctypes.memmove(lpGlobalMemText, text_data, len(text_data))
        kernel32.GlobalUnlock(hGlobalMemText)
        user32.SetClipboardData(CF_UNICODETEXT, hGlobalMemText)

    user32.CloseClipboard()

def fast_cc():
    hist_path = r"C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_context\copy_skill\references\hist_copie.md"
    
    if not os.path.exists(hist_path):
        print("EMPTY")
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
        print("EMPTY")
        return
        
    set_clipboard_files(paths)
    
    package = os.path.basename(os.path.dirname(paths[0])) if paths else "Unknown"
    print(package)
    for path in paths:
        lines = 0
        try:
            with open(path, 'r', encoding='utf-8') as src:
                lines = sum(1 for _ in src)
        except:
            pass
        print(f"| `{os.path.basename(path)}` | {lines} |")

    # Clipboard contents verification
    CF_HDROP = 15
    user32 = ctypes.windll.user32
    success = False
    if user32.OpenClipboard(0):
        try:
            hData = user32.GetClipboardData(CF_HDROP)
            if hData:
                success = True
        finally:
            user32.CloseClipboard()
    
    if success:
        print("VERIFICATION: SUCCESS - Files are present in clipboard.")
    else:
        print("VERIFICATION: FAILURE - Clipboard check failed.")

if __name__ == "__main__":
    fast_cc()
