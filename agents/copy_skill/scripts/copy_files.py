import sys, ctypes, struct

def copy_files_to_clipboard(files):
    CF_HDROP = 15
    # DROPFILES struct: pFiles, pt.x, pt.y, fNC, fWide
    # struct format: 5 dwords (20 bytes). pFiles is the offset to the file list.
    # fWide = 1 (True) for wide strings
    pFiles = 20
    header = struct.pack("IIIII", pFiles, 0, 0, 0, 1)
    
    # Null-separated double-null-terminated wide strings
    file_strings = "\0".join(files) + "\0\0"
    file_bytes = file_strings.encode("utf-16le")
    
    data = header + file_bytes
    
    ctypes.windll.user32.OpenClipboard(0)
    ctypes.windll.user32.EmptyClipboard()
    
    hGlobalMem = ctypes.windll.kernel32.GlobalAlloc(0x0042, len(data))
    lpGlobalMem = ctypes.windll.kernel32.GlobalLock(hGlobalMem)
    
    ctypes.memmove(lpGlobalMem, data, len(data))
    
    ctypes.windll.kernel32.GlobalUnlock(hGlobalMem)
    ctypes.windll.user32.SetClipboardData(CF_HDROP, hGlobalMem)
    ctypes.windll.user32.CloseClipboard()

if __name__ == "__main__":
    if len(sys.argv) > 1:
        copy_files_to_clipboard(sys.argv[1:])
